package org.notes.core.services.internal;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.services.ProxyService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Collections;

@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ProxyServiceImpl implements ProxyService {

    private static final Logger LOGGER = Logger.getLogger(ProxyServiceImpl.class);

    @Override
    public void proxyRequest(HttpServletRequest proxyRequest, HttpServletResponse proxyResponse, String url) throws NotesException {

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

            HttpResponse response = httpclient.execute(httpget);

            for (Header header : response.getAllHeaders()) {
                proxyResponse.addHeader(header.getName(), header.getValue());
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                if (entity.getContentType().getValue().startsWith("text/html")) {
                    // rewrite
                    org.jsoup.nodes.Document document = Jsoup.parse(entity.getContent(), "UTF-8", url);
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

        } catch (Throwable t) {
            String message = String.format("Cannot proxy request. Reason: %s", t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
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
