package org.notes.plugin.harvest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.notes.plugin.harvest.generated.Mediathek;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;

/**
 * Created by damoeb on 4/13/14.
 */
public class Test {

    public static void main(String[] args) {


        DefaultHttpClient httpClient = new DefaultHttpClient();

        try {

            String sessionId = "vtw6ZaEyN28wJk3HsJqPNExQ.note-1";
            BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", sessionId);

            httpClient.getCookieStore().addCookie(cookie);

            JAXBContext context = JAXBContext.newInstance(Mediathek.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            Mediathek mediathek = (Mediathek) unmarshaller.unmarshal(new FileInputStream("/home/damoeb/dev/Filmliste-xml_23_00"));

            HttpPost request = new HttpPost("http://localhost:8080/notes/rest/document");
            request.setHeader("Cookie", "JSESSIONID=" + sessionId);


            request.setHeader("Content-type", "application/json");

            long start = System.nanoTime();

            int count = 200;

            for (Mediathek.X x : mediathek.getX()) {

                if (count-- == 0) {
                    break;
                }

                JSONObject o = new JSONObject();
                o.put("title", x.getTitel());
                o.put("text", x.getBeschreibung());
                o.put("kind", "VIDEO");
                o.put("source", x.getUrl());

                StringEntity entity = new StringEntity(o.toString(), "UTF-8");
                request.setEntity(entity);
                HttpResponse response = httpClient.execute(request);
                // handle response here...

                System.out.println(EntityUtils.toString(response.getEntity()));

            }

            System.out.println((System.nanoTime() - start) / 10000);


        } catch (Exception e) {
            // handle exception here
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }


    }
}
