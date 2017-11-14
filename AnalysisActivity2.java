package com.example.jojie.raspicom;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class AnalysisActivity2 extends AppCompatActivity {

    String ip = "";
    int port = 0;
    TextView textView_sensor1,textView_sensor2,textView_sensor3;
    private android.view.View sensorView, sensorView2, sensorView3;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_analysis2 );
        this.sensorView =   findViewById( R.id.sensorView );
        this.sensorView2 =  findViewById( R.id.sensorView2 );
        this.sensorView3 =  findViewById( R.id.sensorView3 );

        Intent intent = getIntent();
        String message = intent.getStringExtra( MainActivity.EXTRA_MESSAGE );
        String[] parts = message.split( "," );

        ip = parts[0];
        port = Integer.parseInt( parts[1] );

        System.out.println( "IP: " + ip );
        System.out.println( "Port: " + port );

        MyClientTask clientTask = new MyClientTask( ip, port );
        clientTask.execute();

        textView_sensor1 = findViewById( R.id.editText_sensor1);
        textView_sensor2 = findViewById( R.id.editText_sensor2);
        textView_sensor3 = findViewById( R.id.editText_sensor3);

    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {


        String dstAddress;
        int dstPort;
        String response = "";

        MyClientTask(String addr, int port) {
            dstAddress = addr;
            dstPort = port;

            Log.e( "init", "MyClientTask: " );
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                Socket socket = new Socket( dstAddress, dstPort );

                PrintWriter send = new PrintWriter( socket.getOutputStream(), true );

                BufferedReader receive = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

                Log.e( "background", "MyClientTask: " );

                while (true) {


                    String receiveData = receive.readLine();
                    System.out.println( "receiveData:" + receiveData );
                    if (receiveData == null) {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                Log.e( "Receive Data", "Null: " );
//                                checkForBackGround();
                            }
                        } );
                        continue;
                    }
                    receiveData = receiveData.replaceAll( "(\\r|\\n)", "" );

                    response = response + receiveData;

                    if (receiveData.equalsIgnoreCase( "10" )) {

                        Log.e( "inside blue", "MyClientTask: " );

                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                sensorView.setBackground( getResources().getDrawable( R.drawable.background_round_red ) );
                                sensorView2.setBackground( getResources().getDrawable( R.drawable.background_round_red ) );
                                sensorView3.setBackground( getResources().getDrawable( R.drawable.background_round_red ) );
                                textView_sensor1.setText( "Error Occured");
                            }
                        } );


                    } else if (receiveData.equalsIgnoreCase( "20" )) {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                sensorView.setBackground( getResources().getDrawable( R.drawable.background_round_green ) );
                                sensorView2.setBackground( getResources().getDrawable( R.drawable.background_round_green ) );
                                sensorView3.setBackground( getResources().getDrawable( R.drawable.background_round_green ) );
                                textView_sensor1.setText( "Working Fine");
                            }
                        } );

                        Log.e( "inside blue", "MyClientTask: " );
                    } else {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                sensorView.setBackground( getResources().getDrawable( R.drawable.background_round_green ) );
                                sensorView2.setBackground( getResources().getDrawable( R.drawable.background_round_green ) );
                                sensorView3.setBackground( getResources().getDrawable( R.drawable.background_round_green ) );
                            }
                        } );
                    }


                    Log.e( "Under While", "MyClientTask: " );

                    if (receiveData.equalsIgnoreCase( "EXIT" )) {

                        socket.close();
                        Log.e( "Exit", "MyClientTask: " );
                        break;
                    }


                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e( "Unknown Exception", e.getMessage() );
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e( "IO Exception", e.getMessage() );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute( aVoid );
            Log.e( "PostExecute", "MyClientTask: " );
            sensorView.setBackground( getResources().getDrawable( R.drawable.background_round_blue ) ); //on post execute final color
            sensorView2.setBackground( getResources().getDrawable( R.drawable.background_round_blue ) ); //on post execute final color
            sensorView3.setBackground( getResources().getDrawable( R.drawable.background_round_blue ) ); //on post execute final color
            //textView1.setText( response );
        }
    }


}
