package org.notes.core.endpoints;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interceptors.Bouncer;

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
import java.util.Collections;

@NotesInterceptors
@Path("/proxy")
public class ProxyEndpoint {

    private static final Logger LOGGER = Logger.getLogger(ProxyEndpoint.class);

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
    @Bouncer
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

            String[] forwardHeaders = new String[]{
                    "user-agent", "accept-language", "user-agent", "accept", "cookie"
            };
            for (String headerName : Collections.list(proxyRequest.getHeaderNames())) {
                boolean forwardHeader = false;
                for (String forwardHeaderName : forwardHeaders) {
                    if (StringUtils.equalsIgnoreCase(forwardHeaderName, headerName)) {
                        forwardHeader = true;
                    }
                }
                if (forwardHeader) {
                    httpget.setHeader(headerName, proxyRequest.getHeader(headerName));
                }
            }

            LOGGER.info(httpget.getMethod() + " " + httpget.getRequestLine());

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
                        for (Element image : document.select("img[src]")) {
                            image.attr("src", image.absUrl("src"));
                        }

                        // with proxy-prefix
                        for (Element a : document.select("a[href]")) {
                            if (!StringUtils.equals(a.absUrl("href"), "#")) {
                                a.attr("href", "/notes/rest/proxy/?url=" + URLEncoder.encode(a.absUrl("href"), "UTF-8"));
                            }
                        }
                        for (Element script : document.select("script[src]")) {
                            script.attr("src", script.absUrl("src"));
                        }
                        for (Element link : document.select("link[href]")) {
                            link.attr("href", link.absUrl("href"));
                        }

                        Element head = document.select("head").first();
                        head.appendChild(getStyle("/ui/styles/proxy.compiled.css"));
                        head.appendChild(getStyle("/ui/bower_components/font-awesome/css/font-awesome.min.css"));
                        head.appendChild(getScript("/ui/bower_components/modernizr/modernizr.js"));
                        head.appendChild(getScript("/ui/bower_components/jquery/dist/jquery.js"));
                        head.appendChild(getScript("/ui/bower_components/jquery-ui/ui/jquery-ui.js"));
                        head.appendChild(getScript("/ui/bower_components/sass-bootstrap/js/modal.js"));
                        head.appendChild(getScript("/ui/scripts/proxy/core.js"));

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

    private Element getScript(String path) {
        Element script = new Element(Tag.valueOf("script"), "");
        script.attr("src", path);
        return script;
    }

    private Element getStyle(String path) {
        Element style = new Element(Tag.valueOf("link"), "");
        style.attr("rel", "stylesheet");
        style.attr("href", path);
        return style;
    }

}
