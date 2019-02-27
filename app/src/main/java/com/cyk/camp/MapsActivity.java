package com.cyk.camp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, PopDialog.MyDialogFragmentListener {

    private GoogleMap mMap;

    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FirebaseDatabase db;
    private FusedLocationProviderClient fusedLocationClient;
    private Team team = new Team();
    private ArrayList<Quest> quests = new ArrayList();

    private double latitude, longitude;
    private boolean get_team_key = false;
    private String team_key;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        db = FirebaseDatabase.getInstance();



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        DatabaseReference myRef = db.getReference();

        //initialize team & quest data
        //if haven't get the key (map activity just launched)
        if(!get_team_key){
            //get team key from shared preferences
            team_key = getSharedPreferences("data", MODE_PRIVATE)
                    .getString("team_key", "");
            get_team_key = true;
            Log.d("tag_team_key", team_key);

            Query query_team = myRef.child("teams").child(team_key);

            //get team info
            query_team.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    team = snapshot.getValue(Team.class);
                    Log.d("tag_team_name", team.name);
                    Log.d("tag_team_quest_number", String.valueOf(team.quest_number));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //get quests
            Query query_quest = myRef.child("quests");

            query_quest.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot questSnapshot: snapshot.getChildren()) {
                        Quest q = questSnapshot.getValue(Quest.class);
                        quests.add(q);
                        //Log.d("tag_get_answer", q.answer);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
        mMap.setMapStyle(style);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        getLocation();

    }

    public void scan(View view){

        getLocation();

        //check distance

        float[] result = new float[1];
        Location.distanceBetween(latitude, longitude,
                quests.get(team.quest_number).latitude, quests.get(team.quest_number).longitude,
                result);

        Log.d("tag_distance_check", String.valueOf(latitude));
        Log.d("tag_distance_check", String.valueOf(longitude));
        Log.d("tag_distance_check", String.valueOf(quests.get(team.quest_number).latitude));
        Log.d("tag_distance_check", String.valueOf(quests.get(team.quest_number).longitude));
        Log.d("tag_distance_check", String.valueOf(result[0]));

        //if close enough, pop out quest window

        if(result[0] <= 10) {

            Log.d("tag_dialog", "pop dialog");

            Bundle args = new Bundle();
            args.putString("question", quests.get(team.quest_number).question);

            DialogFragment dialog = new PopDialog();
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "PopDialog");

        }
        else {
            Toast toast = Toast.makeText(this, "不在附近喔！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 400);
            toast.show();
        }


    }

    public void getLocation(){
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



                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 18), 1000, null);
                                //Toast.makeText(MapsActivity.this, latitude + ", " + longitude, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onReturnValue(String foo) {

        Log.d("tag_return", foo);
        Log.d("tag_correct", quests.get(team.quest_number).answer);

        //if the answer is correct, update team info

        if(foo.equals(quests.get(team.quest_number).answer)){

            Log.d("tag_answer_check", "The answer is correct!");
            Toast toast = Toast.makeText(this, "你答對了！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 400);
            toast.show();
            //update local team info
            team.quest_number++;

            //update db team info
            DatabaseReference myRef = db.getReference();
            myRef.child("teams").child(team_key).child("quest_number").setValue(team.quest_number);
        }
        else{
            Toast toast = Toast.makeText(this, "答錯囉", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 400);
            toast.show();
        }


        //complete all quests
        if(team.quest_number >= quests.size()){
            SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
            pref.edit().putBoolean("complete", true).apply();

            Intent myIntent = new Intent(this, CompleteActivity.class);
            startActivity(myIntent);
        }

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
