package org.nepalitools.asr.androidapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.*;

/**
 * This class makes server calls in a different single dedicated thread.
 * This is needed because Android recommends us to run network calls in a different thread than the main thread.
 *
 * Created by dixya on 8/6/17.
 */
public class NetworkOperation{

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    
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
            return future.get(30, TimeUnit.SECONDS);
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
