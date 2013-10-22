package org.notes.core.dao;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.StreamManager;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class WebsiteManagerBean implements StreamManager {

    private static final Logger LOGGER = Logger.getLogger(WebsiteManagerBean.class);

    @Override
    public File tryDownloadStream(URL url) throws NotesException {
        try {

            return youtubeDl(url);

        } catch (IOException e) {
            throw new NotesException(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException, NotesException {
        System.out.println(youtubeDl(new URL("http://www.youtube.com/watch?v=iTWuZav-elY")));
    }

    private static File youtubeDl(URL url) throws NotesException, IOException {
        Process process = null;
        try {
            String path = "/tmp/stream-xyz";
            process = new ProcessBuilder("youtube-dl", url.toString(), "-o", path, "-f", "mp4", "-q").start();
            process.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // todo does not contain error
            // contentType = StringUtils.trim(br.readLine());
            char[] buffer = new char[256];
            int len;
            while((len = br.read(buffer))>0) {
                System.out.println(new String(buffer, 0, len));
            }


            if(process.exitValue()!=0) {
                LOGGER.error("command exists with " + process.exitValue());
                return null;
            }

            return new File(path);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(process!=null) {
                process.getInputStream().close();
                process.getErrorStream().close();
                process.getOutputStream().close();
                process.destroy();
            }

        }
        return null;
    }
}
