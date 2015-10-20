package com.msarangal.travelkhaanademo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mandeep on 19/10/2015.
 */
public class OrderConfirmation extends AppCompatActivity {

    private TextView email, stationN, arrTime;
    private Button place_another;
    private ImageView logout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.orderconfirmation);

        email = (TextView) findViewById(R.id.email);
        stationN = (TextView) findViewById(R.id.StationName);
        arrTime = (TextView) findViewById(R.id.arrivalTime);
        place_another = (Button) findViewById(R.id.place_order);
        logout = (ImageView) findViewById(R.id.imgLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sign out
                SignOut();

                sharedPreferences = OrderConfirmation.this.getSharedPreferences("tkhaana", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
//change the shared prefs
                editor.putBoolean("isConnected", false);
                editor.commit();

            }
        });

        place_another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent i = getIntent();

        email.setText(i.getStringExtra("email"));
        stationN.setText(i.getStringExtra("stationName"));
        arrTime.setText(i.getStringExtra("arr_halt"));

    }

    public void SignOut() {

        MainActivity.signOutFromGplus();

        finish();

    }


}
