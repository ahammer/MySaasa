package com.mysaasa;

import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.file.Files;
import org.eclipse.jetty.util.security.Credential;

import java.io.File;
import java.io.IOException;

public class BakedInFileRequestHandler implements IRequestHandler {
    private final Request mRequest;
    private final File file;

    public BakedInFileRequestHandler(Request request) {
        this.mRequest = request;
        String path = request.getUrl().getPath();
        file = new File(".\\webapp\\"+path);
    }

    public static boolean isValidRequest(Request request) {

        String path = request.getUrl().getPath();
        if (path.length() == 0)
            return false;

        File file = new File(".\\webapp\\"+path);

        return file.exists();
    }

    @Override
    public void respond(IRequestCycle requestCycle) {
        final WebResponse response = (WebResponse) requestCycle.getResponse();
        try {
            final byte[] data = Files.readBytes(file);

            response.setHeader("cache-control", "private, max-age=0, no-cache");
            response.setHeader("ETag", Credential.MD5.digest(file.lastModified() + file.getAbsolutePath()));
            response.setContentLength(data.length);
            response.write(data);
        } catch (Exception e) {
            errorResponseBackup(e, response);
        }
    }

    @Override
    public void detach(IRequestCycle requestCycle) {

    }

    private void errorResponseBackup(Exception e, WebResponse response)  {
        final byte[] data = e.toString().getBytes();
        response.setHeader("cache-control", "private, max-age=0, no-cache");
        response.setHeader("ETag", Credential.MD5.digest(file.lastModified() + file.getAbsolutePath()));
        response.setContentLength(data.length);
        response.write(data);
        System.out.println("Error:::: " + e);
        e.printStackTrace();

    }
}
