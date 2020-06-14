package com.example.yadavm.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.yadavm.Adapters.NotiAd;
import com.example.yadavm.Models.NotiMo;
import com.example.yadavm.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Notification extends AppCompatActivity {
    private Toolbar toolbar;
    FirebaseDatabase database;
    DatabaseReference reference;

    private RecyclerView recyclerView;
    private NotiAd NotiAd;
    private List<NotiMo> mNotiList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        FirebaseApp.initializeApp(this);
        toolbar = (Toolbar)findViewById(R.id.toolbar_noti_notifi);
        setSupportActionBar(toolbar);
        (Notification.this).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear);

//        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear);
//
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Notification");
        reference.keepSynced(true);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_noti);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mNotiList = new ArrayList<>();

        NotiAd = new NotiAd(this,mNotiList);
        recyclerView.setAdapter(NotiAd);
        readPost();
    }

    private void readPost(){
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNotiList.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    NotiMo shopmodal = dataSnapshot1.getValue(NotiMo.class);
                    mNotiList.add(shopmodal);
                    NotiAd.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Notification.this, databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.notification){
            Intent intent = new Intent(this,Notification.class);

            startActivity(intent );
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
