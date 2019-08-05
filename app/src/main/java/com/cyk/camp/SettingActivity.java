package com.cyk.camp;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingActivity extends AppCompatActivity {

    //private static final int PICKER = 100;

    private FirebaseDatabase db;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private ImageView img;
    private TextView tv_img;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        img = findViewById(R.id.img);
        tv_img = findViewById(R.id.tv_pic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Uri uri = data.getData();
        if(uri != null) {
            tv_img.setText("上傳中...");
            final StorageReference up = storageRef.child("images/main.jpg");
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
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    tv_img.setText(uri.getPath());
                    GlideApp.with(context)
                            .load(uri)
                            .into(img);
                }
            });


        }
    }
    
    public void upload(View view){
        Intent picker = new Intent(Intent.ACTION_PICK);
        picker.setType("image/*");
        startActivityForResult(picker, 0);
    }

    public void next(View view){

        db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference();

        EditText et_name, et_radius, et_text, et_password, et_video;
        et_name = findViewById(R.id.et_setting_name);
        et_radius = findViewById(R.id.et_setting_radius);
        et_text = findViewById(R.id.et_setting_text);
        et_password = findViewById(R.id.et_setting_password);
        et_video = findViewById(R.id.et_setting_video);

        String name = et_name.getText().toString();
        String radius = et_radius.getText().toString();
        String text = et_text.getText().toString();
        String password = et_password.getText().toString();
        String video = et_video.getText().toString();

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

            myRef.child("description").setValue(text);

            myRef.child("password").setValue(password);


            if(video.length() != 0){
                String videoid = extractYTId(video);
                if(videoid != null)
                    myRef.child("videoid").setValue(videoid);
            }

            Intent myIntent = new Intent(this, QuestEditActivity.class);
            startActivity(myIntent);
            //myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //finish();
            //return;

        }
    }

    public static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                ".*(?:youtu.be\\/|v\\/|u\\/\\w\\/|embed\\/|watch\\?v=)([^#\\&\\?]*).*",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if(matcher.find()){
            vId = matcher.group(1);
            Log.d("tag_id", vId);
        }
        return vId;
    }
}
