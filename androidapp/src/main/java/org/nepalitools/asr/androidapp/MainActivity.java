package org.nepalitools.asr.androidapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText textContent;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textContent = (EditText) findViewById( R.id.textContent);
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == submitButton) {
            makeServerCall();
        }
    }

    private void makeServerCall() {
        String urlString = "http://www.google.com/";
        try {
            String response = new NetworkOperation().getMethodCall(urlString);
            textContent.setText(response.substring(0, 20));
        } catch (Exception e) {
            Toast.makeText(this, "Exception in calling url: " + urlString, Toast.LENGTH_LONG);
            e.printStackTrace();
        }
    }
}
