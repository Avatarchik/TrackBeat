package com.rohanjain.trackbeat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends ActionBarActivity {
    final Context context = this;
    private Button button_about;
    private Button button_instr;
    private ImageView im;
    private Animation anm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_about = (Button) findViewById(R.id.button3);
        // add button listener
        button_about.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("Trackbeat-About");

                // set dialog message
                alertDialogBuilder
                        .setMessage("We present `TrackBeat', an Android app to extract the Human Pulse information just from a video of the Head, by measuring subtle head motion caused by the Newtonian reaction to the influx of blood at each beat. This app was created as the Final term project for the CS290I course, UCSB, Winter 2015, under the guidance of Prof. Matthew Turk. \n Team: \n Praveen Nayak \n Rohan Jain \n Samson Svendsen")
                        .setCancelable(false)
                        .setPositiveButton("Close app!",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton("Ok! Got it!",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        button_instr = (Button) findViewById(R.id.button2);
        // add button listener
        button_instr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("Instructions");

                // set dialog message
                alertDialogBuilder
                        .setMessage("1. On the Homepage, Select the Front Camera if you wish to obtain your own pulse rate or the Back Camera if you wish to obtain a friend's pulse rate \n 2. A Camera preview opens. In a while, the Face Detection results are displayed. Tilt your head left-right to get face detect results if they are not immediately displayed \n 3. After around 20 seconds, press 'Get my heart Rate!' to get a readout of your heart rate on a new screen!\n NOTE: Please don't move your head during step 3(After your face is detected)")
                        .setNegativeButton("Ok! Got it!",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
        im = (ImageView) findViewById(R.id.heart_home);
        ImageView logo = (ImageView) findViewById(R.id.logo);
        anm = AnimationUtils.loadAnimation(context,R.anim.heart);
        im.startAnimation(anm);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void openDataCollection_back(View v){
        Intent intent = new Intent(this, DataCollection.class);
        intent.putExtra("CAMERA_MODE","-1" );
        startActivity(intent);
    }
    public void openDataCollection_front(View v){
        Intent intent = new Intent(this, DataCollection.class);
        intent.putExtra("CAMERA_MODE","1" );
        startActivity(intent);
    }
}
