package c.com.ecomerceuser;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import c.com.ecomerceuser.models.ItemModel;
import c.com.ecomerceuser.models.StoreOrder;

public class Item extends AppCompatActivity {

    @InjectView(R.id.item_recyclerview)
    RecyclerView itemRecyclerView;


    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    int count=0;


     SearchView searchView;
     Toolbar itemToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        itemToolbar = (Toolbar) findViewById(R.id.item_toolbar);
        setSupportActionBar(itemToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ButterKnife.inject(this);
        initialiseIDS();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation


        itemRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
searchRecyclerview("");

    }
    private void initialiseIDS()
    {

        String id = getIntent().getStringExtra("id");
        firebaseDatabase    =   FirebaseDatabase.getInstance();
        databaseReference   =  firebaseDatabase.getReference().child("Item").child(id);

    }



    @Override
    public void onStart() {
        super.onStart();
    }



    private void searchRecyclerview(String s)
    {
        Query databaseReference2;
        if(s.length()>0)
            databaseReference2=   databaseReference.orderByChild("name").startAt(s).endAt(s+"\uF8FF");
        else
            databaseReference2=   databaseReference;

        final FirebaseRecyclerAdapter<ItemModel,ItemViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ItemModel,ItemViewHolder>(
                ItemModel.class,
                R.layout.item_adapter_layout,
                ItemViewHolder.class,
                databaseReference2
        ) {
            @Override
            protected void populateViewHolder(final ItemViewHolder viewHolder, final ItemModel model, final int position) {

                count=0;
                viewHolder.setName(model.getName());
                viewHolder.setCost(""+model.getPrice());
                String keyis = getRef(position).getKey();
                DatabaseReference parent =   getRef(position).getParent();
                String parentKeyIs =  parent.getKey();
                model.setParentKey(parentKeyIs);
                model.setKey(keyis);
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Item.this,ViewProductActivity.class);

                        Pair[] pairs = new Pair[3];
                        pairs[0] = new Pair<View,String>(viewHolder.mview.findViewById(R.id.adapter_item_title),"product_name");
                        pairs[1] = new Pair<View,String>(viewHolder.mview.findViewById(R.id.adapter_item_cost),"product_price");
                        pairs[2] = new Pair<View,String>(viewHolder.mview.findViewById(R.id.adapter_item_icon),"product_image");
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(Item.this,pairs);
                        intent.putExtra("item",model);
                        startActivity(intent,activityOptions.toBundle());
                    }
                });

            }
        };
        itemRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder
    {

        View mview;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setName(String title)
        {
            TextView titleTextView =  (TextView) mview.findViewById(R.id.adapter_item_title);
            titleTextView.setText(""+title);
        }

        public void setCost(String cost)
        {
            TextView costTextView =  (TextView) mview.findViewById(R.id.adapter_item_cost);
            costTextView.setText("Rs : "+cost);
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
                return false;
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
