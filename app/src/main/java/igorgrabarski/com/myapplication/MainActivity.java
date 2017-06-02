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
    private long cid;
    private long lac;

    private final int ZOOM = 23;
    private final int MINIMAL_ZOOM = 1;
    private final String LOCATION_REF = "location";
    private final String LAT_REF = "lat";
    private final String LNG_REF = "lng";
    private final String CURRENT_DATE_TIME_REF = "currentDateTime";
    private final String DATE_REF = "date";
    private final String MONTH_REF = "month";
    private final String YEAR_REF = "year";
    private final String HOURS_REF = "hours";
    private final String MINUTES_REF = "minutes";
    private final String SECONDS_REF = "seconds";
    private final String LAC_REF = "lac";
    private final String CID_REF = "cid";

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
                    if (Integer.valueOf(s.toString()) < MINIMAL_ZOOM) {
                        etZoom.setText(String.valueOf(MINIMAL_ZOOM));
                        sbZoom.setProgress(1);
                    } else if (Integer.valueOf(s.toString()) > ZOOM) {
                        etZoom.setText(String.valueOf(ZOOM));
                        sbZoom.setProgress(ZOOM);
                    } else {
                        sbZoom.setProgress(Integer.valueOf(s.toString()));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final DatabaseReference myRef = database.getReference(LOCATION_REF);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(LAT_REF)){
                    lat =  dataSnapshot.getValue(Double.class);
                }
                else if(dataSnapshot.getKey().equals(LNG_REF)){
                    lng =  dataSnapshot.getValue(Double.class);
                }
                else if(dataSnapshot.getKey().equals(CURRENT_DATE_TIME_REF)){
                    dateTime = dataSnapshot.child(DATE_REF).getValue(Long.class) + "." +
                            dataSnapshot.child(MONTH_REF).getValue(Long.class) + "." +
                            dataSnapshot.child(YEAR_REF).getValue(Long.class) + " " +
                            dataSnapshot.child(HOURS_REF).getValue(Long.class) + ":" +
                            dataSnapshot.child(MINUTES_REF).getValue(Long.class) + ":" +
                            dataSnapshot.child(SECONDS_REF).getValue(Long.class);
                }
                else if(dataSnapshot.getKey().equals(LAC_REF)){
                    lac = dataSnapshot.getValue(Long.class);
                }
                else if(dataSnapshot.getKey().equals(CID_REF)){
                    cid = dataSnapshot.getValue(Long.class);
                }

                if(cid != -1 || lac != -1){
                    tvInfo.setText("CellID: " + cid + "  " + "Lac: " + lac);
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
                if (dataSnapshot.child("lat").getValue(Double.class) != -1 && dataSnapshot.child("lng").getValue(Double.class) != -1) {
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
                    btnShow.setEnabled(true);
                }
                else if(dataSnapshot.child("cid").getValue(Long.class) != -1 && dataSnapshot.child("lac").getValue(Long.class) != -1){
                    cid = (long) dataSnapshot.child("cid").getValue();
                    lac = (long) dataSnapshot.child("lac").getValue();
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
