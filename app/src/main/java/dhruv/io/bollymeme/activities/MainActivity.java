package dhruv.io.bollymeme.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import dhruv.io.bollymeme.R;
import dhruv.io.bollymeme.adapter.BollyVideosAdapter;
import dhruv.io.bollymeme.model.BollyVideos;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Firebase mFirebaseRef;
    private ArrayList<BollyVideos> bollyVideosArrayList = new ArrayList<>();
    private BollyVideosAdapter bollyVideosAdapter;

    @BindView(R.id.rvVideos)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Firebase.setAndroidContext(this);

        mFirebaseRef = new Firebase("https://bolly-memes.firebaseio.com/").child("Videos");

        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    BollyVideos bollyVideos = postSnapshot.getValue(BollyVideos.class);

                    bollyVideosArrayList.add(bollyVideos);
                }

                bollyVideosAdapter = new BollyVideosAdapter(getApplicationContext(), bollyVideosArrayList, new BollyVideosAdapter.BollyVideosOnClickInterface() {
                    @Override
                    public void onClick(BollyVideos bollyVideos, RecyclerView.ViewHolder vh) {
                        //Log.e(TAG, bollyVideos.getDescription());
                        Intent intent = new Intent(MainActivity.this, VideoPlayActivity.class);
                        intent.putExtra("bollyVideos", bollyVideos);
                        MainActivity.this.startActivity(intent);
                    }
                });
                mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                mRecyclerView.setAdapter(bollyVideosAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void pushVideo(){

        BollyVideos bollyVideos = new BollyVideos();
        bollyVideos.setId("1");
        bollyVideos.setName("Bhaag Dhanno");
        bollyVideos.setCategory("Entertainment");
        bollyVideos.setSubCategory("Movie");
        bollyVideos.setVideoURL("");
        bollyVideos.setActors("hema malini");
        bollyVideos.setTimestamp("");
        bollyVideos.setThumbnailURL("");
        bollyVideos.setMetadata("");
        bollyVideos.setVideoDuration("20");
        bollyVideos.setDescription("Basanti trying to save her izzat");
        bollyVideos.setLikes("5");
        bollyVideos.setRating("4.5");
        bollyVideos.setVideoSize("1");

        mFirebaseRef.push().setValue(bollyVideos, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null){
                    Log.e(TAG, firebaseError.getMessage());
                }else {
                    Log.e(TAG, "DATA PUSHED");
                }
            }
        });
    }
}
