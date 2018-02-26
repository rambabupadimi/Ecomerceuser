package c.com.ecomerceuser;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import c.com.ecomerceuser.helper.ViewImageCircle;

public class ProfileViewActivity extends AppCompatActivity {


    de.hdodenhof.circleimageview.CircleImageView imageView;
    TextView name,email,phone;
    ViewImageCircle viewImageCircle;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        imageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.view_profile_image);
        name        = (TextView) findViewById(R.id.view_profile_name);
        email       = (TextView) findViewById(R.id.view_profile_email);
        phone       = (TextView) findViewById(R.id.view_profile_phone);
        viewImageCircle = new ViewImageCircle();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        final FirebaseAuth mAuth   =   FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference().child("Customers").child(uid);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap hashMap = (HashMap) dataSnapshot.getValue();
                name.setText(hashMap.get("name").toString());
                phone.setText(hashMap.get("phone").toString());
                email.setText(mAuth.getCurrentUser().getEmail());
                final String url = hashMap.get("imgurl").toString();

                if (url != null) {
                    if (url != null && url.length() > 0) {
                        Picasso.with(ProfileViewActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(ProfileViewActivity.this).load(url).into(imageView);
                                    }
                                });
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
/*
            Intent intent = new Intent(ProfileViewActivity.this,MainActivity.class);
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View,String>(imageView,"profile_image");
            pairs[1] = new Pair<View,String>(name,"profile_name");
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(ProfileViewActivity.this,pairs);
            startActivity(intent,activityOptions.toBundle());
*/
        finish();

        }

        return super.onOptionsItemSelected(item);
    }
}
