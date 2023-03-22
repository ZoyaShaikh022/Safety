package com.safety.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationSettingsRequest;

import java.util.ArrayList;

import static android.Manifest.permission.CALL_PHONE;

public class MainActivity2 extends AppCompatActivity {

    Button b1,b2;
    private  FusedLocationProviderClient client;
    DatabaseHandler myDB;
    private final int REQUEST_CHECK_CODE = 8989;
    private LocationSettingsRequest.Builder builder;
    String x ="",y="", message="" ;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        b1 = findViewById(R.id.button);
        b2 = findViewById(R.id.button2);
        myDB = new DatabaseHandler(this);

        locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
        boolean isGPSEnabled = false;
        boolean isNetworkEnabled = false;
        onGPS();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Register.class);
                startActivity(i);
            }
        });

       b2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              // loadData();
               sendSms("+918149333474", "Help me", true);
           }
       });

    }

    private void loadData() {
        ArrayList<String> thelist = new ArrayList<>(  );
        Cursor data = myDB.getListContents();
        if (data.getCount()==0){
            Toast.makeText(this,"NO CONTENT TO SHOW",Toast.LENGTH_SHORT).show();
        }
        else {
            String msg = "PLEASE HELP ME LATITUDE:"+x+"LONGITUDE:"+y;
            String number = "";

            while (data.moveToNext()) {
                thelist.add(data.getString(1));
                number = number + data.getString(1)+(data.isLast()?"":";");
                call();

            }

            if (!thelist.isEmpty()) {
            sendSms(number,msg,true);
            }

        }
    }

    private void sendSms(String number, String msg, boolean b) {
        /*Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        Uri.parse("sms to:"+number);
        smsIntent.putExtra("smsbody",msg);
        startActivity(smsIntent);*/
        SmsManager manager = SmsManager.getDefault();
        startTrack();
        //manager.sendTextMessage(number , null, message, null, null);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void call(){
        Intent i = new Intent( Intent.ACTION_CALL );
        i.setData(Uri.parse("tel:100"));
        if (ContextCompat.checkSelfPermission(getApplicationContext(),CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            startActivity(i);
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
                requestPermissions(new String[]{CALL_PHONE},1);
            }
        }
        }



    private void startTrack() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else{
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null){
                double lat = locationGPS.getLatitude();
                double lon = locationGPS.getLongitude();
                x = String.valueOf( lat );
                y = String.valueOf( lon );
                message="Help me, the location is Latitude="+x+"Longitude="+y;
            }
            else {
                Toast.makeText(this,"UNABLE TO DETECT LOCATION", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onGPS() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("ENABLE GPS").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
         startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    });
    final AlertDialog alertDialog = builder.create();
    alertDialog.show();

    }
}