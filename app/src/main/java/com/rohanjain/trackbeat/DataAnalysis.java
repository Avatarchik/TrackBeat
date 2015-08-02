package com.rohanjain.trackbeat;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class DataAnalysis extends ActionBarActivity {

    private String s;
    private int FLAG = 0;
    private double heartbeat = 200;
    TextView mTextview;
    String string_heartbeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analysis);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            heartbeat = extras.getDouble("MESSAGE");
            FLAG = 1;
        }
        FLAG = 1;

        setContentView(R.layout.activity_data_analysis);

        mTextview = (TextView)findViewById(R.id.final_result);
        Integer heartbeat_round = (int) heartbeat;

        string_heartbeat= Integer.toString(heartbeat_round);
        if(heartbeat_round > 130 || heartbeat_round < 40){
            string_heartbeat = "ERROR";
        }
        Log.d(DataCollection.TAG, "AB Heartbeat value found");
        mTextview.setText(string_heartbeat);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_analysis, menu);
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

    public void go_back_home(View v){
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("CAMERA_MODE","-1" );
        startActivity(intent);
    }

}
