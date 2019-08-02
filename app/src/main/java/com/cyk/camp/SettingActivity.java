package com.cyk.camp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class SettingActivity extends AppCompatActivity {

    private static final int PICKER = 100;

    private FirebaseDatabase db;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private ImageView img;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        img = findViewById(R.id.img);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        StorageReference up = storageRef.child("images/main.jpg");
        up.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("tag_path", "upload failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.d("tag_path", taskSnapshot.getMetadata().toString());

            }
        });
        GlideApp.with(context)
                .load(uri)
                .centerCrop()
                .into(img);

    }


    public void upload(View view){
        Intent picker = new Intent(Intent.ACTION_PICK);
        picker.setType("image/*");
        startActivityForResult(picker, 0);
    }


    public void next(View view){

        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        EditText et_name, et_radius, et_text, et_password;
        et_name = findViewById(R.id.et_setting_name);
        et_radius = findViewById(R.id.et_setting_radius);
        et_text = findViewById(R.id.et_setting_text);
        et_password = findViewById(R.id.et_setting_password);

        String name = et_name.getText().toString();
        String radius = et_radius.getText().toString();
        String text = et_text.getText().toString();
        String password = et_password.getText().toString();

        if(name.length() == 0)
            Toast.makeText(this, "請輸入遊戲名稱", Toast.LENGTH_SHORT).show();
        else if(text.length() == 0)
            Toast.makeText(this, "請輸入遊戲敘述", Toast.LENGTH_SHORT).show();
        else if(password.length() == 0)
            Toast.makeText(this, "請輸入遊戲密碼", Toast.LENGTH_SHORT).show();
        else{

            myRef.child("name").setValue(name);

            if(radius.length() > 0)
                myRef.child("radius").setValue(Integer.parseInt(radius));
            else
                myRef.child("radius").setValue(15);

            if(text.length() > 0)
                myRef.child("description").setValue(text);

            myRef.child("password").setValue(password);

            Intent myIntent = new Intent(this, QuestEditActivity.class);
            startActivity(myIntent);
            //myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //finish();
            //return;

        }

    }
}
