package org.notes.core.proxy;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.notes.common.configuration.NotesInterceptors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.net.URLEncoder;

@NotesInterceptors
@Path("/proxy")
public class ProxyEndpoint {

    @HEAD
    public void doHead(@Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletContext context, @QueryParam("url") String url)
            throws ServletException, IOException {
        // Process request without content.
        processRequest(request, response, context, url, false);
    }

    /**
     * Process GET request.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse).
     */
    @GET
    public void doGet(@Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletContext context, @QueryParam("url") String url)
            throws ServletException, IOException {
        // Process request with content.
        processRequest(request, response, context, url, true);
    }

    private void processRequest(HttpServletRequest proxyRequest, HttpServletResponse proxyResponse, ServletContext context, String url, boolean content)
            throws IOException {

        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(url);

//            todo pass language
//            Enumeration<String> headers = proxyRequest.getHeaderNames();
//            while(headers.hasMoreElements()) {
//                String headerName = headers.nextElement();
//                if("cookie".equalsIgnoreCase(headerName)) {
//                    continue;
//                }
//                httpget.setHeader(headerName, proxyRequest.getHeader(headerName));
//            }

            System.out.println("Executing request " + httpget.getRequestLine());
            HttpResponse response;
            try {
                response = httpclient.execute(httpget);

                for (Header header : response.getAllHeaders()) {
                    proxyResponse.addHeader(header.getName(), header.getValue());
                }

                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    if (entity.getContentType().getValue().startsWith("text/html")) {
                        // rewrite
                        Document document = Jsoup.parse(entity.getContent(), "UTF-8", url);
                        for (Element image : document.select("img")) {
                            image.attr("src", image.absUrl("src"));
                        }

                        // with proxy-prefix
                        for (Element a : document.select("a")) {
                            a.attr("href", "/proxy/?url=" + URLEncoder.encode(a.absUrl("href"), "UTF-8"));
                        }
                        for (Element script : document.select("script")) {
                            script.attr("src", script.absUrl("src"));
                        }
                        for (Element link : document.select("link")) {
                            link.attr("href", link.absUrl("href"));
                        }
                        proxyResponse.getWriter().write(document.outerHtml());

                    } else {
                        entity.writeTo(proxyResponse.getOutputStream());
                    }
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        } finally {
        }

    }

}
