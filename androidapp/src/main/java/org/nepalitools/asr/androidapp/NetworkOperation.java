package org.nepalitools.asr.androidapp;

import java.io.File;
import java.util.concurrent.*;

import android.util.Log;
import okhttp3.*;

/**
 * This class makes server calls in a different single dedicated thread.
 * This is needed because Android recommends us to run network calls in a different thread than the main thread.
 * <p>
 * Created by dixya on 8/6/17.
 */
public class NetworkOperation {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final OkHttpClient httpClient = new OkHttpClient();

    protected String getMethodCall(final String urlString) {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    Request request = new Request.Builder().url(urlString).build();
                    Call call = httpClient.newCall(request);
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        String returnedText = response.body().string();
                        Log.i("OkHttpResult", "Response=" + returnedText);
                        return returnedText;
                    } else {
                        Log.i("OkHttpResult", "Response not successful");
                        return "Get call failed";
                    }
                } catch (Exception e) {
                    Log.i("OkHttpResult", "Exception in making get call", e);
                    throw new RuntimeException(e);
                }
            }
        };
        return getResult(callable);
    }

    protected String uploadFile(final String urlString, final String absolutePathFileLocation) {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    File file = new File(absolutePathFileLocation);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("text", "Sample text")
                            .addFormDataPart("file", "recording.3gpp", RequestBody.create(MediaType.parse("video/3gpp"), file))
                            .build();

                    Request request = new Request.Builder()
                            //.header("Authorization", "Client-ID " + "not_used")
                            .url(urlString)
                            .post(requestBody)
                            .build();

                    Response response = httpClient.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        return "There was an error!";
                    }
                    return response.body().string();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return getResult(callable);
    }

    private String getResult(Callable<String> callable) {
        Future<String> future = executorService.submit(callable);
        try {
            return future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }
}
