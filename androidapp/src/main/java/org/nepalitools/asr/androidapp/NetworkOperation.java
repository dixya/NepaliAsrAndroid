package org.nepalitools.asr.androidapp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.*;

/**
 * Created by dixya on 8/6/17.
 */
public class NetworkOperation{

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Exception exception;

    protected String getMethodCall(final String urlString) {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    URL url = new URL(urlString);
                    URLConnection conn = url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Future<String> future = executorService.submit(callable);
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return "There was an error";
    }
}
