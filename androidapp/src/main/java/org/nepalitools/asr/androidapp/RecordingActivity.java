package org.nepalitools.asr.androidapp;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import static android.widget.Toast.*;


public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String SERVER_URL = "http://nepaliasr.us-west-2.elasticbeanstalk.com";

    EditText textContent;
    Button submitButton;
    private MediaRecorder myRecorder;

    private MediaPlayer myPlayer;

    private String outputFile = null;

    private Button startRecordBtn;

    private Button stopRecordBtn;

    private Button playBtn;

    private Button stopPlayBtn;

    private TextView notificationText;

    private Session session;//global variable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Context cntx;
        //  session = new Session(cntx); //in oncreate
        //  session.setUserName("dixya");
        //  setContentView(R.layout.login_main);
        setContentView(R.layout.activity_record);
        initObjects();
        initRecorder();
        initListeners();
    }


    private void initObjects() {
        textContent = (EditText) findViewById(R.id.textContent);
        // call server for text
        textContent.setText(getCall(SERVER_URL + "/text"));
        submitButton = (Button) findViewById(R.id.submitButton);
        startRecordBtn = (Button) findViewById(R.id.start);
        stopRecordBtn = (Button) findViewById(R.id.stop);
        stopPlayBtn = (Button) findViewById(R.id.stopPlay);
        playBtn = (Button) findViewById(R.id.play);
        notificationText = (TextView) findViewById(R.id.notificationText);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/nepaliSpeechRecording.3gpp";
    }

    private void initRecorder() {
        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myRecorder.setOutputFile(outputFile);
    }

    private void initListeners() {
        submitButton.setOnClickListener(this);
        // store it to sd card
        startRecordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                start(v);
            }
        });

        stopRecordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stop(v);
            }
        });

        playBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                play(v);
            }
        });

        stopPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay(v);
            }
        });
    }

    public void start(View view) {
        try {
            // start:it is called before prepare()
            // prepare: it is called after start() or before setOutputFormat()
            myRecorder.prepare();
            myRecorder.start();
        } catch (IOException e) {
            throw new RuntimeException("Error starting recorder", e);
        }

        notificationText.setText("Recording Point: Recording");
        startRecordBtn.setEnabled(false);
        submitButton.setEnabled(false);
        stopRecordBtn.setEnabled(true);
        makeText(getApplicationContext(), "Start recording...",LENGTH_SHORT).show();
    }


    public void stop(View view) {
        myRecorder.stop();
        myRecorder.release();
        myRecorder = null;
        stopRecordBtn.setEnabled(false);
        playBtn.setEnabled(true);
        submitButton.setEnabled(true);
        notificationText.setText("Recording Point: Stop recording");
        makeText(getApplicationContext(), "Stop recording...",LENGTH_SHORT).show();
    }

    public void play(View view) {
        try {
            myPlayer = new MediaPlayer();
            myPlayer.setDataSource(outputFile);
            myPlayer.prepare();
            myPlayer.start();
            playBtn.setEnabled(false);
            stopPlayBtn.setEnabled(true);
            submitButton.setEnabled(false);
            notificationText.setText("Recording Point: Playing");
            makeText(getApplicationContext(), "Start play the recording...",LENGTH_SHORT).show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void stopPlay(View view) {
        // we can only stop if the player is not null
        if (myPlayer != null) {
            myPlayer.stop();
            myPlayer.release();
            myPlayer = null;
            playBtn.setEnabled(true);
            stopPlayBtn.setEnabled(false);
            submitButton.setEnabled(true);
            notificationText.setText("Recording Point: Stop playing");
            makeText(getApplicationContext(), "Stop playing the recording...",LENGTH_SHORT).show();
        }
    }

    private String getCall(String urlString) {
        try {
            String response = new NetworkOperation().getMethodCall(urlString);
            return response;
        } catch (Exception e) {
            makeText(this, "Exception in calling url: " + urlString, LENGTH_LONG).show();
            e.printStackTrace();
            return "Exception in calling get";
        }
    }

    private String upload(String urlString, String fileLocation) {
        try {
            String response = new NetworkOperation().uploadFile(urlString, fileLocation);
            return response;
        } catch (Exception e) {
            makeText(getApplicationContext(), "Exception in calling url: " + urlString, LENGTH_LONG).show();
            e.printStackTrace();
            return "Exception in calling upload.";
        }
    }

    @Override
    public void onClick(View view) {
        if (view == submitButton) {
            //getCall(SERVER_URL + "/ping");
            String responseText = upload(SERVER_URL + "/data", outputFile);
            submitButton.setEnabled(false);
            startRecordBtn.setEnabled(true);
            notificationText.setText(responseText);
            makeText(getApplicationContext(), "Upload completed", LENGTH_LONG).show();
        }
    }
}
