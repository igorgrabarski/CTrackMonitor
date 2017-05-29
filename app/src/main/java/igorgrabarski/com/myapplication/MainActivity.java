package igorgrabarski.com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnShow;
    Button btnReset;
    SeekBar sbZoom;
    EditText etZoom;
    ProgressBar pbLoad;
    TextView tvInfo;

    FirebaseDatabase database;

    private double lat;
    private double lng;
    private Date date;
    private String dateTime;
    private int cid;
    private int lac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pbLoad = (ProgressBar) findViewById(R.id.pbLoad);
        pbLoad.setVisibility(View.VISIBLE);
        database = FirebaseDatabase.getInstance();


        btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);
        btnShow = (Button) findViewById(R.id.btnShow);
        btnShow.setOnClickListener(this);
        tvInfo = (TextView) findViewById(R.id.tvInfo);

        sbZoom = (SeekBar) findViewById(R.id.sbZoom);
        sbZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    sbZoom.setProgress(1);
                }
                etZoom.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        etZoom = (EditText) findViewById(R.id.etZoom);
        etZoom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (Integer.valueOf(s.toString()) < 1) {
                        etZoom.setText("1");
                        sbZoom.setProgress(1);
                    } else if (Integer.valueOf(s.toString()) > 23) {
                        etZoom.setText("23");
                        sbZoom.setProgress(23);
                    } else {
                        sbZoom.setProgress(Integer.valueOf(s.toString()));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final DatabaseReference myRef = database.getReference("location");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals("lat")){
                    lat = (double) dataSnapshot.getValue();
                }
                else if(dataSnapshot.getKey().equals("lng")){
                    lng = (double) dataSnapshot.getValue();
                }
                else if(dataSnapshot.getKey().equals("currentDateTime")){
                    dateTime = dataSnapshot.child("date").getValue(Long.class) + "." +
                            dataSnapshot.child("month").getValue(Long.class) + "." +
                            dataSnapshot.child("year").getValue(Long.class) + " " +
                            dataSnapshot.child("hours").getValue(Long.class) + ":" +
                            dataSnapshot.child("minutes").getValue(Long.class) + ":" +
                            dataSnapshot.child("seconds").getValue(Long.class);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("lat") != null && dataSnapshot.child("lng") != null) {
                    lat = (double) dataSnapshot.child("lat").getValue();
                    lng = (double) dataSnapshot.child("lng").getValue();
                    date = dataSnapshot.child("currentDateTime").getValue(Date.class);
                    dateTime = date.getDate() + "." +
                            date.getMonth() + "." +
                            date.getYear() + " " +
                            date.getHours() + ":" +
                            date.getMinutes() + ":" +
                            date.getSeconds();
                    pbLoad.setVisibility(View.INVISIBLE);
                }
                else if(dataSnapshot.child("cid") != null && dataSnapshot.child("lac") != null){
                    cid = (int) dataSnapshot.child("cid").getValue();
                    lac = (int) dataSnapshot.child("lac").getValue();
                    tvInfo.setText("CellID: " + cid + "  " + "Lac: " + lac);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pbLoad.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnShow) {

            pbLoad.setVisibility(View.VISIBLE);
            if (lat != 0 && lng != 0) {
                Intent mapIntent = new Intent(this, MapsActivity.class);
                mapIntent.putExtra("zoom", sbZoom.getProgress());
                mapIntent.putExtra("lat", lat);
                mapIntent.putExtra("lng", lng);
                mapIntent.putExtra("dateTime", dateTime);
                startActivity(mapIntent);
                pbLoad.setVisibility(View.INVISIBLE);

            } else {
                pbLoad.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Still loading data... Please try again later.", Toast.LENGTH_SHORT).show();
            }

        } else if (v.getId() == R.id.btnReset) {
            etZoom.setText("13");
            sbZoom.setProgress(13);
        }
    }
}
