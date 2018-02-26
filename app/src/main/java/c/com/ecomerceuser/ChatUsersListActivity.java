package c.com.ecomerceuser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatUsersListActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    List userList;
    FirebaseRecyclerAdapter<Users,ExpensesViewHolder> firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users_list);

        userNotExist();

        setRecyclerView();
    }

    private void setRecyclerView()
    {
        firebaseDatabase   =    FirebaseDatabase.getInstance();
        if(firebaseDatabase==null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }

        databaseReference   = FirebaseDatabase.getInstance().getReference().child("Admin");
        databaseReference.keepSynced(true);

        recyclerView        =   (RecyclerView) findViewById(R.id.chat_user_list_recylerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    private void userNotExist()
    {
        mAuth   =   FirebaseAuth.getInstance();
        mAuthListener   =   new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() ==null)
                {
                    Intent intent = new Intent(ChatUsersListActivity.this,SignupOrLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

    }

    @Override
    protected void onStop() {
        super.onStop();
/*
        DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference().child("Customers").child(mAuth.getCurrentUser().getUid());
        databaseReference.child("online").setValue(false);
        databaseReference.child("lastseen").setValue(ServerValue.TIMESTAMP);
*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, ExpensesViewHolder>(
                Users.class,
                R.layout.chat_user_list_layout,
                ExpensesViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(final ExpensesViewHolder viewHolder, final Users model, final int position) {


                viewHolder.setName(model.getName());
                viewHolder.setPhone(model.getPhone());
                viewHolder.setOnline(model.getOnline());


                Log.i("tag", "online status" + model.getOnline());
                Log.i("tag", "name" + model.getName());
                Log.i("tag", "phone" + model.getPhone());
                Log.i("tag", "userid" + model.getUserid());
                Log.i("tag", "url" + model.getImgurl());


                if (model.getImgurl() != null) {
                    // viewHolder.setImage(model.getImgurl());
            /*
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference(model.getImgurl());
            */

                    if (model.getImgurl() != null) {
                        if (model.getImgurl() != null && model.getImgurl().length() > 0) {
                            Picasso.with(ChatUsersListActivity.this).load(model.getImgurl()).networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(viewHolder.imageView, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(ChatUsersListActivity.this).load(model.getImgurl()).into(viewHolder.imageView);
                                        }
                                    });
                        }
                    }

                }
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatUsersListActivity.this, ChatActivity.class);
                        intent.putExtra("name", model.getName());
                        intent.putExtra("phone", model.getPhone());
                        intent.putExtra("userid", model.getUserid());
                        startActivity(intent);

                    }
                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
/*

        DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference().child("Customers").child(mAuth.getCurrentUser().getUid());
        databaseReference.child("online").setValue(true);
        databaseReference.child("lastseen").setValue(true);
*/
    }



    public static class ExpensesViewHolder extends RecyclerView.ViewHolder
    {

        View mview;
        ImageView imageView;
        LinearLayout itemLayout;
        public ExpensesViewHolder(View itemView) {
            super(itemView);
            mview       =   itemView;
            imageView   =   (ImageView) mview.findViewById(R.id.pic);
            itemLayout  =   (LinearLayout) mview.findViewById(R.id.item_layout);
        }

        public void setName(String name)
        {
            TextView amountTextViw =  (TextView) mview.findViewById(R.id.user_list_name);
            amountTextViw.setText(""+name);
        }

        public void setPhone(String phone)
        {
            TextView amountTextViw =  (TextView) mview.findViewById(R.id.user_list_email);
            amountTextViw.setText(""+phone);
        }

        public void setImage(String image)
        {

        }
        public void setOnline(boolean status)
        {
            ImageView imageView = (ImageView) mview.findViewById(R.id.online_status);
            if(status)
            {
                imageView.setVisibility(View.VISIBLE);
            }
            else
            {
                imageView.setVisibility(View.GONE);
            }
        }

    }
}
