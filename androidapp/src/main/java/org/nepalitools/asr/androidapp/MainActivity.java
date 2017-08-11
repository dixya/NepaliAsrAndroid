package org.nepalitools.asr.androidapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static android.widget.Toast.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText textContent;
    Button submitButton;
    private MediaRecorder myRecorder;

    private MediaPlayer myPlayer;

    private String outputFile = null;

    private Button startBtn;

    private Button stopBtn;

    private Button playBtn;

    private Button stopPlayBtn;

    private TextView text;
    private TextView messageText;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    /**********  File Path *************/
    final String uploadFilePath = "/mnt/sdcard/";
    final String uploadFileName = outputFile;
    String upLoadServerUri = "http://nepaliasr.us-west-2.elasticbeanstalk.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textContent = (EditText) findViewById( R.id.textContent);
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        startBtn= (Button) findViewById(R.id.start);
        stopBtn= (Button) findViewById(R.id.stop);
        playBtn=(Button) findViewById(R.id.play);
        stopPlayBtn= (Button) findViewById(R.id.stopPlay);





        text = (TextView) findViewById(R.id.text1);

        // store it to sd card

        outputFile = Environment.getExternalStorageDirectory().

        getAbsolutePath() + "/nepaliSpeechRecording.3gpp";



        myRecorder = new MediaRecorder();

        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        myRecorder.setOutputFile(outputFile);



        startBtn = (Button)findViewById(R.id.start);

        startBtn.setOnClickListener(new OnClickListener() {



            @Override

            public void onClick(View v) {

                // TODO Auto-generated method stub

                start(v);

            }

        });


        stopBtn = (Button)findViewById(R.id.stop);

        stopBtn.setOnClickListener(new OnClickListener() {



            @Override

            public void onClick(View v) {

                // TODO Auto-generated method stub

                stop(v);

            }

        });



        playBtn = (Button)findViewById(R.id.play);

        playBtn.setOnClickListener(new OnClickListener() {



            @Override

            public void onClick(View v) {

                // TODO Auto-generated method stub

                play(v);

            }

        });



        stopPlayBtn = (Button)findViewById(R.id.stopPlay);

        stopPlayBtn.setOnClickListener(new OnClickListener() {



            @Override

            public void onClick(View v) {

                // TODO Auto-generated method stub

                stopPlay(v);

            }

        });

    }



    public void start(View view){

        try {

            myRecorder.prepare();

            myRecorder.start();

        } catch (IllegalStateException e) {

            // start:it is called before prepare()

            // prepare: it is called after start() or before setOutputFormat()

            e.printStackTrace();

        } catch (IOException e) {

            // prepare() fails

            e.printStackTrace();

        }



        text.setText("Recording Point: Recording");

        startBtn.setEnabled(false);

        stopBtn.setEnabled(true);



        makeText(getApplicationContext(), "Start recording...",
                LENGTH_SHORT).show();

    }


    public void stop(View view){

        try {

            myRecorder.stop();

            myRecorder.release();

            myRecorder  = null;



            stopBtn.setEnabled(false);
            playBtn.setEnabled(true);

            text.setText("Recording Point: Stop recording");


            makeText(getApplicationContext(), "Stop recording...",
                    LENGTH_SHORT).show();
        } catch (IllegalStateException e) {

            //  it is called before start()

            e.printStackTrace();
        } catch (RuntimeException e) {
            // no valid audio/video data has been received

            e.printStackTrace();

        }

    }

    public void play(View view) {

        try{

            myPlayer = new MediaPlayer();

            myPlayer.setDataSource(outputFile);

            myPlayer.prepare();

            myPlayer.start();



            playBtn.setEnabled(false);

            stopPlayBtn.setEnabled(true);

            text.setText("Recording Point: Playing");



            makeText(getApplicationContext(), "Start play the recording...",
                    LENGTH_SHORT).show();

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

    }



    public void stopPlay(View view) {

        try {

            if (myPlayer != null) {

                myPlayer.stop();

                myPlayer.release();

                myPlayer = null;

                playBtn.setEnabled(true);

                stopPlayBtn.setEnabled(false);

                text.setText("Recording Point: Stop playing");



                makeText(getApplicationContext(), "Stop playing the recording...",
                        LENGTH_SHORT).show();

            }

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

    }







    @Override
    public void onClick(View view) {
        if (view == submitButton) {

            submitButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                   // dialog = ProgressDialog.show(this, "", "Uploading file...", true);

                    new Thread(new Runnable() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    messageText.setText("uploading started.....");
                                }
                            });

                            uploadFile(uploadFilePath + "" + uploadFileName);

                        }
                    }).start();
                }
            });
        }
    }

    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("Source File not exist :"
                            +uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; filename="
                        + fileName + "" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" http://www.androidexample.com/media/uploads/"
                                    +uploadFileName;

                            messageText.setText(msg);
                           // Toast.makeText(UploadToServer.this, "File Upload Complete.",
                                  //  Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("MalformedURLException Exception : check script url.");
                       // Toast.makeText(UploadToServer.this, "MalformedURLException",
                             //   Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                       // Toast.makeText(UploadToServer.this, "Got Exception : see logcat ",
                               // Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

//public void sendMessage(View view){
//                Intent intent = new Intent(this, UploadToServer.class);
//                Bundle b=new Bundle();
//                b.putString("uploadFileName",outputFile);
//                Editable editText = textContent.getText();
//                String message = editText.toString();
//                b.putString("textValue",message);
//
//                intent.putExtras(b);
//                startActivity(intent);

           // }



    private void makeServerCall() {
        String urlString = "http://nepaliasr.us-west-2.elasticbeanstalk.com/ping";
        try {
            String response = new NetworkOperation().getMethodCall(urlString);
            textContent.setText(response.substring(0, 20));
        } catch (Exception e) {
            makeText(this, "Exception in calling url: " + urlString, LENGTH_LONG);
            e.printStackTrace();
        }
    }
}
