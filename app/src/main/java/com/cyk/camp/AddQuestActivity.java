package com.cyk.camp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AddQuestActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {


    private FirebaseDatabase db;
    private int n = 0; //number of quests
    private String key;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude, longitude;
    private String q = "", a = "", h = "";
    private Marker marker;
    private NumberFormat formatter = new DecimalFormat("#0.00");

    private DatabaseReference myRef;
    private EditText et_q;
    private EditText et_a;
    private EditText et_a1, et_a2, et_a3, et_a4;
    private EditText et_h;
    private Spinner spinner, spinner_answer;
    private int spinner_choice = 0, correct_answer = 0;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quest);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = FirebaseDatabase.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        myRef = db.getReference("quests");
        et_q = findViewById(R.id.et_question);
        et_a = findViewById(R.id.et_answer);
        et_h = findViewById(R.id.et_hint);
        et_a1 = findViewById(R.id.et_choice_1);
        et_a2 = findViewById(R.id.et_choice_2);
        et_a3 = findViewById(R.id.et_choice_3);
        et_a4 = findViewById(R.id.et_choice_4);

        spinner = findViewById(R.id.spinner_question_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.question_type,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner_answer = findViewById(R.id.spinner_answer);
        ArrayAdapter<CharSequence> adapter_answer = ArrayAdapter.createFromResource(this, R.array.spinner_answer,
                android.R.layout.simple_spinner_item);
        adapter_answer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_answer.setAdapter(adapter_answer);
        //spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_choice = position;
                //改變layout
                if(spinner_choice == 0 || spinner_choice == 2) {
                    et_q.setVisibility(View.VISIBLE);
                    if(spinner_choice == 0) {
                        //問答題
                        et_a.setVisibility(View.VISIBLE);
                        et_a1.setVisibility(View.GONE);
                        et_a2.setVisibility(View.GONE);
                        et_a3.setVisibility(View.GONE);
                        et_a4.setVisibility(View.GONE);
                    }
                    else {
                        //選擇題
                        et_a.setVisibility(View.GONE);
                        et_a1.setVisibility(View.VISIBLE);
                        et_a2.setVisibility(View.VISIBLE);
                        et_a3.setVisibility(View.VISIBLE);
                        et_a4.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    //走到就過關
                    et_a.setVisibility(View.GONE);
                    et_a1.setVisibility(View.GONE);
                    et_a2.setVisibility(View.GONE);
                    et_a3.setVisibility(View.GONE);
                    et_a4.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_answer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                correct_answer = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
        mMap.setMapStyle(style);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        //mMap.setOnMyLocationButtonClickListener(this);
        //mMap.setOnMyLocationClickListener(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng clickCoords) {
                if(marker != null) marker.remove();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clickCoords, 22), 800, null);
                marker = mMap.addMarker(new MarkerOptions()
                        .position(clickCoords));
                latitude = clickCoords.latitude;
                longitude = clickCoords.longitude;
                Toast toast = Toast.makeText(AddQuestActivity.this, formatter.format(latitude) + ", " + formatter.format(longitude), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 50);
                toast.show();
            }
        });

        getLocation(findViewById(R.id.btn_locate));
        //findViewById(R.id.btn_get_location).performClick();

    }
    public void newQuest(View view){

        if(spinner_choice == 0){
            //問答題
            if(et_a.getText().toString().length() == 0 || et_q.getText().toString().length() == 0 ||
                    et_h.getText().toString().length() == 0) {
                //有欄位是空的
                Toast.makeText(AddQuestActivity.this, "欄位不可留空", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "已新增", Toast.LENGTH_SHORT).show();

                q = et_q.getText().toString();
                a = et_a.getText().toString();
                h = et_h.getText().toString();

                et_a.getText().clear();
                et_q.getText().clear();
                et_h.getText().clear();

                key = myRef.push().getKey();

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        n = (int) snapshot.getChildrenCount();
                        Log.d("tag_children_count", String.valueOf(n));

                        Quest quest = new Quest(n, q, a, latitude, longitude, h);
                        myRef.child(key).setValue(quest);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
        else if(spinner_choice == 1){
            //走到就過關

            if(et_h.getText().toString().length() == 0) {
                //沒輸入提示
                Toast.makeText(AddQuestActivity.this, "欄位不可留空", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "已新增", Toast.LENGTH_SHORT).show();

                q = "走到就過關";
                a = "走到就過關";
                h = et_h.getText().toString();

                et_a.getText().clear();
                et_q.getText().clear();
                et_h.getText().clear();

                key = myRef.push().getKey();
            }
        }
        else if(spinner_choice == 2){
            //選擇題
            if(et_q.getText().toString().length() == 0 ||
                    et_a1.getText().toString().length() == 0 ||
                    et_a2.getText().toString().length() == 0 ||
                    et_a3.getText().toString().length() == 0 ||
                    et_a4.getText().toString().length() == 0 ||
                    et_h.getText().toString().length() == 0){
                //有欄位是空的
                Toast.makeText(AddQuestActivity.this, "欄位不可留空", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "已新增", Toast.LENGTH_SHORT).show();

                String a1, a2, a3, a4;
                a1 = et_a1.getText().toString();
                a2 = et_a2.getText().toString();
                a3 = et_a3.getText().toString();
                a4 = et_a4.getText().toString();

                q = "multiple_choice " + et_q.getText().toString()  + " " + a1 + " " + a2 + " " + a3 + " " + a4;
                a = String.valueOf(correct_answer);
                h = et_h.getText().toString();

                key = myRef.push().getKey();

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        n = (int) snapshot.getChildrenCount();

                        Quest quest = new Quest(n, q, a, latitude, longitude, h);
                        myRef.child(key).setValue(quest);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }

    }

    public void getLocation(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.

                            if (location != null) {
                                // Logic to handle location object
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                if(marker != null) marker.remove();
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitude, longitude)));

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 22), 1000, null);
                                Toast toast = Toast.makeText(AddQuestActivity.this, formatter.format(latitude) + ", " + formatter.format(longitude), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 50);
                                toast.show();
                            }
                        }
                    });
        }
    }
    public void back(View view){
        finish();
    }

    public void help(View view){
        Log.d("tag_help", "pressed");
        View v = LayoutInflater.from(context).inflate(R.layout.popupwindow_add_quest_help, null, false);
        PopupWindow pop = new PopupWindow(v);

        pop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //pop.setBackgroundDrawable(new ColorDrawable(0x00000000));
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);

        pop.showAsDropDown(view);

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(this, "Current location:\n" + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG).show();
        Log.d("tag_location", location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        Log.d("tag_location", "location button clicked");
        return false;
    }

}
