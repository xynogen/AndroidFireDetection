package com.polsri.firedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jsibbold.zoomage.ZoomageView;

public class Camera extends AppCompatActivity {
    ZoomageView zoomageView;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    SwipeRefreshLayout swipeRefreshLayout;
    DatabaseReference database;
    String urlDatabase = "https://firedetection-970d0-default-rtdb.asia-southeast1.firebasedatabase.app/";
    String urlStorage = "gs://firedetection-970d0.appspot.com";
    String filename = "Ruangan.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        database = FirebaseDatabase.getInstance(urlDatabase).getReference("/");

        zoomageView = (ZoomageView) findViewById(R.id.ZoomageView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayoutNode1);

        database.child("event").setValue("0");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("event").getValue(String.class).toString().equals("0")) {
                    ShowImage(urlStorage, filename);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Data Check Failed", Toast.LENGTH_LONG).show();
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {@Override
        public void onRefresh() {
            database.child("event").setValue("1");
            ShowImage(urlStorage, filename);
            swipeRefreshLayout.setRefreshing(false);
        }
        });

    }

    private void ShowImage(String url, String file) {
        final long ONE_MEGABYTE = 1024 * 1024;
        FirebaseStorage storage = FirebaseStorage.getInstance(url);
        StorageReference gsReference = storage.getReference(file);
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                zoomageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}