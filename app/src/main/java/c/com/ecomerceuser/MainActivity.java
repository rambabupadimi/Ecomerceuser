package c.com.ecomerceuser;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import c.com.ecomerceuser.models.GroupModel;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.events.OnBannerClickListener;
import ss.com.bannerslider.views.BannerSlider;


public class MainActivity extends AppCompatActivity {


    SearchView searchView;

    NavigationView navigationView;
    private DrawerLayout drawerLayout;
    ImageView profileImage;
    TextView profileName;
    RecyclerView groupRecyclerView,categoryRecyclerView;

    DatabaseReference databaseReference1;
    FirebaseDatabase firebaseDatabase1;

    Toolbar toolbar;
    RelativeLayout viewCartLayout;
    TextView cartCount;


    DatabaseReference databaseReference2;
    FirebaseUser firebaseUser2;

    BannerSlider bannerSlider;
    ScrollView scrollView;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        profileImage    = (ImageView) header.findViewById(R.id.profile_image);
        profileName    = (TextView) header.findViewById(R.id.profileName);
        groupRecyclerView = (RecyclerView) findViewById(R.id.group_recyclerview);
        categoryRecyclerView = (RecyclerView)findViewById(R.id.category_recyclerview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewCartLayout = (RelativeLayout) findViewById(R.id.view_cart_layout);
        bannerSlider = (BannerSlider) findViewById(R.id.banner_slider1);

        categoryRecyclerView.setVisibility(View.GONE);

        cartCount  = (TextView) findViewById(R.id.cart_count);
        scrollView = (ScrollView) findViewById(R.id.scrollview_parent);


        viewCartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ViewCartActivity.class);
                startActivity(intent);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ProfileViewActivity.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View,String>(profileImage,"profile_image");
                pairs[1] = new Pair<View,String>(profileName,"profile_name");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                startActivity(intent,activityOptions.toBundle());
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        FirebaseAuth   mAuth   =   FirebaseAuth.getInstance();


        String uid = mAuth.getUid();
        if(uid==null)
        {
            Intent intent = new Intent(this,SignupOrLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {


            firebaseUser2 = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference2   =  FirebaseDatabase.getInstance().getReference().child("Cart").child(firebaseUser2.getUid());

            databaseReference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("tag","item data snapchat"+dataSnapshot.getValue());
                    if(dataSnapshot!=null)
                    {
                        cartCount.setText(""+dataSnapshot.getChildrenCount());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                    getReference().child("Customers").child(uid);
            databaseReference.keepSynced(true);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("tag", "datasnapshot" + dataSnapshot.getValue());

                    HashMap hashMap = (HashMap) dataSnapshot.getValue();
                    Log.i("tag", "url is" + hashMap.get("imgurl").toString());

                    profileName.setText(hashMap.get("name").toString());

                    final String url = hashMap.get("imgurl").toString();
                    if (url != null) {
                        if (url != null && url.length() > 0) {
                            Picasso.with(MainActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(profileImage, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(MainActivity.this).load(url).into(profileImage);
                                        }
                                    });
                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            initNavigationDrawer();


            firebaseDatabase1    =   FirebaseDatabase.getInstance();
            databaseReference1   =  firebaseDatabase1.getReference().child("Group");

            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            searchRecyclerview("");
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
            groupRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView

        }


        setupBannerSlider();
        showCategories();
    }


    private void showCategories()
    {
            CategoriesAdapter categoriesAdapter = new CategoriesAdapter();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            categoryRecyclerView.setLayoutManager(linearLayoutManager);
            categoryRecyclerView.setAdapter(categoriesAdapter);
    }

    private void setupBannerSlider(){
        addBanners();

        bannerSlider.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(MainActivity.this, "Banner with position " + String.valueOf(position) + " clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addBanners(){
        List<Banner> remoteBanners=new ArrayList<>();
        //Add banners using image urls
        remoteBanners.add(new RemoteBanner(
                "https://assets.materialup.com/uploads/dcc07ea4-845a-463b-b5f0-4696574da5ed/preview.jpg"
        ));
        remoteBanners.add(new RemoteBanner(
                "https://assets.materialup.com/uploads/4b88d2c1-9f95-4c51-867b-bf977b0caa8c/preview.gif"
        ));
        remoteBanners.add(new RemoteBanner(
                "https://assets.materialup.com/uploads/76d63bbc-54a1-450a-a462-d90056be881b/preview.png"
        ));
        remoteBanners.add(new RemoteBanner(
                "https://assets.materialup.com/uploads/05e9b7d9-ade2-4aed-9cb4-9e24e5a3530d/preview.jpg"
        ));
        bannerSlider.setBanners(remoteBanners);
        bannerSlider.setFocusable(true);

    }



    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();
                Intent intent=null;
                switch (id){
                    case R.id.users:
                        Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_SHORT).show();

                        intent = new Intent(MainActivity.this,ChatUsersListActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.cart:
                        intent = new Intent(MainActivity.this,ViewCartActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();

                        break;
                    case R.id.orders:
                        intent = new Intent(MainActivity.this,ViewOrdersPageActivity.class);
                        startActivity(intent);

                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(MainActivity.this, SignupOrLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;

                }
                return true;
            }
        });
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkUserExists();

    }


    private void checkUserExists()
    {
        FirebaseAuth   mAuth   =   FirebaseAuth.getInstance();

        String uid = mAuth.getUid();

        if(uid==null)
        {
            Intent intent = new Intent(this,SignupOrLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }


    public static class GroupViewHolder extends RecyclerView.ViewHolder
    {

        View mview;
        public GroupViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setName(String title)
        {
            TextView titleTextView =  (TextView) mview.findViewById(R.id.adapter_group_title);
            titleTextView.setText(""+title);
        }

        public void setDescription(String description)
        {
            TextView descriptionTextView  = (TextView) mview.findViewById(R.id.adapter_group_description);
            descriptionTextView.setText(description);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate( R.menu.search_menu, menu);

        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }

                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);

                searchRecyclerview(s);
                return true;
            }
        });
        return true;

    }

    private void searchRecyclerview(String s)
    {
        Query databaseReference2;
        if(s.length()>0)
            databaseReference2=   FirebaseDatabase.getInstance().getReference().child("Group").orderByChild("name").startAt(s).endAt(s+"\uF8FF");
        else
            databaseReference2=   FirebaseDatabase.getInstance().getReference().child("Group");
        final FirebaseRecyclerAdapter<GroupModel,GroupViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GroupModel, GroupViewHolder>(
                GroupModel.class,
                R.layout.group_adapter_layout,
                GroupViewHolder.class,
                databaseReference2
        ) {
            @Override
            protected void populateViewHolder(GroupViewHolder viewHolder, final GroupModel model, final int position) {
                viewHolder.setName(model.getName());
                viewHolder.setDescription(model.getDescription());
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String keyis = getRef(position).getKey();
                        Log.i("tag","key is"+keyis);
                        Intent intent = new Intent(MainActivity.this,Item.class);
                        intent.putExtra("id",keyis);
                        startActivity(intent);

                    }
                });
            }
        };
        groupRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

}



