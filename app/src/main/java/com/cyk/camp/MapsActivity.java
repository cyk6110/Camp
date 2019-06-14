package com.cyk.camp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, PopDialog.MyDialogFragmentListener {

    private GoogleMap mMap;

    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FirebaseDatabase db;
    private FusedLocationProviderClient fusedLocationClient;
    private Team team = new Team();
    private ArrayList<Quest> quests = new ArrayList<>();

    private double latitude, longitude;
    private boolean get_team_key = false;
    private String team_key;
    private List<Integer> order = new ArrayList<>();
    private int total_quests;
    private int status;
    private Context context = this;
    private long complete_time, start_time = -1;

    /*
    排名＆計時:

    db:
    start_time
    teams_complete 存完成的隊伍的完成時間(撈下來再排序

    admin monitor:
    開始時把start_time存到db & shared preference
    ***改到wait admin?

    admin end:
    撈db

    player:V
    完成時
    1. shared preference存名次＆所花時間
    2. 把隊伍資料push上去

    player end:V
    shared preference complete time & rank

    */

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

        start_time = getSharedPreferences("data", MODE_PRIVATE)
                .getLong("start_time", -1);

        myRef.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                status = snapshot.getValue(Integer.class);
                if(status == 4){
                    //end of the game
                    SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                    pref.edit().putBoolean("end", true).apply();

                    Intent myIntent = new Intent(context, EndPlayerActivity.class);
                    startActivity(myIntent);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    return;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        myRef.child("start_time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                start_time = snapshot.getValue(long.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //initialize team & quest data
        //if haven't get the key (map activity just launched)
        if(!get_team_key){
            //get team key & order from shared preferences
            SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);

            team_key = pref.getString("team_key", "");

            total_quests = pref.getInt("total_quests", 0);

            for(int i = 0;i < total_quests;i++)
                order.add(pref.getInt("order" + i, 0));

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
                quests.get(team.current_quest).latitude, quests.get(team.current_quest).longitude,
                result);

        Log.d("tag_distance_check", String.valueOf(latitude));
        Log.d("tag_distance_check", String.valueOf(longitude));
        Log.d("tag_distance_check", String.valueOf(quests.get(team.current_quest).latitude));
        Log.d("tag_distance_check", String.valueOf(quests.get(team.current_quest).longitude));
        Log.d("tag_distance_check", String.valueOf(result[0]));

        //if close enough, pop out quest window

        if(result[0] <= 15 && quests.get(team.current_quest).question.equals("走到就過關")){
            //走到就過關
            Toast toast = Toast.makeText(this, "你過關了！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 400);
            toast.show();
            //update local team info
            team.quest_number++;
            team.current_quest = order.get(team.quest_number);

            //update db team info
            DatabaseReference myRef = db.getReference();
            myRef.child("teams").child(team_key).child("quest_number").setValue(team.quest_number);
            myRef.child("teams").child(team_key).child("current_quest").setValue(team.current_quest);
        }
        else if(result[0] <= 15) {

            Log.d("tag_dialog", "pop dialog");

            Bundle args = new Bundle();
            args.putString("question", quests.get(team.current_quest).question);

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

    public void hint(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(quests.get(team.current_quest).hint)
                .setTitle("提示");
        builder.setPositiveButton("好", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //answer button pressed
    @Override
    public void onReturnValue(String foo) {

        Log.d("tag_return", foo);
        Log.d("tag_correct", quests.get(team.current_quest).answer);

        //if the answer is correct, update team info
        //切關鍵字&檢查
        String[] keywords = quests.get(team.current_quest).answer.split(" ");
        boolean match = false;
        for(String s:keywords){
            if(foo.equals(s)){
                match = true;
                break;
            }
        }
        if(match){
            Log.d("tag_answer_check", "The answer is correct!");
            Toast toast = Toast.makeText(this, "你答對了！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 400);
            toast.show();
            //update local team info
            team.quest_number++;

            //team.current_quest = order.get(team.quest_number);

            //update db team info
            DatabaseReference myRef = db.getReference();
            myRef.child("teams").child(team_key).child("quest_number").setValue(team.quest_number);
            //myRef.child("teams").child(team_key).child("current_quest").setValue(team.current_quest);
        }
        else{
            Toast toast = Toast.makeText(this, "答錯囉", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 400);
            toast.show();
        }


        //complete all quests
        if(team.quest_number >= quests.size()){

            complete_time = System.currentTimeMillis();

            DatabaseReference myRef = db.getReference();

            String key = getSharedPreferences("data", MODE_PRIVATE).getString("team_key", "");
            String name = getSharedPreferences("data", MODE_PRIVATE).getString("team_name", "");
            Rank rank = new Rank(name, complete_time - start_time);
            myRef.child("teams_complete").child(key).setValue(rank);

            //shared reference存名次和所花時間
            myRef.child("teams_complete").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    int n = (int)snapshot.getChildrenCount();
                    Log.d("teams_complete", String.valueOf(n));
                    SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                    pref.edit()
                            .putBoolean("end", true)
                            .putLong("time_spent", complete_time - start_time)
                            .putInt("rank", n)
                            .apply();

                    Intent myIntent = new Intent(context, EndPlayerActivity.class);
                    startActivity(myIntent);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    return;
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //把所花時間push到db的teams_complete(隊名當index)




        }
        else {
            team.current_quest = order.get(team.quest_number);
            DatabaseReference myRef = db.getReference();
            myRef.child("teams").child(team_key).child("current_quest").setValue(team.current_quest);
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
