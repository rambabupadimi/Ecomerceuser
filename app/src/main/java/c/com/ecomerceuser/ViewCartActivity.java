package c.com.ecomerceuser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import c.com.ecomerceuser.models.CartModel;
import c.com.ecomerceuser.models.ItemModel;

public class ViewCartActivity extends AppCompatActivity {

    RecyclerView cartRecyclerView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<CartModel> cartModelArrayList = new ArrayList<>();
    ArrayList<ItemModel> itemModelArrayList = new ArrayList<>();
    ViewCartAdapter viewCartAdapter;
    Toolbar cartToolbar;
    Button placeOrder;
    RelativeLayout cartNotFound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);

        cartRecyclerView = (RecyclerView) findViewById(R.id.cart_recyclerview);

        placeOrder  = (Button) findViewById(R.id.place_order);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference   =  FirebaseDatabase.getInstance().getReference().child("Cart").child(firebaseUser.getUid());

        cartNotFound    = (RelativeLayout) findViewById(R.id.not_found_layout);

        cartToolbar  = (Toolbar) findViewById(R.id.cart_toolbar);
        setSupportActionBar(cartToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchRecyclerview("");


        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewCartActivity.this,OrderPage.class);
                intent.putExtra("itemModelArrayList",itemModelArrayList);
                intent.putExtra("cartModelArrayList",cartModelArrayList);
                startActivity(intent);
            }
        });


    }


    private void searchRecyclerview(String s)
    {
                databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("tag","item data snapchat"+dataSnapshot.getValue());

                    if(dataSnapshot!=null && dataSnapshot.getChildrenCount()>0) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            CartModel cartModel = ds.getValue(CartModel.class);
                            Log.i("tag", "item id is" + cartModel.getItemId());
                            cartModelArrayList.add(cartModel);
                        }
                        showItemsList(cartModelArrayList);

                        cartRecyclerView.setVisibility(View.VISIBLE);
                        cartNotFound.setVisibility(View.GONE);
                        placeOrder.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        cartRecyclerView.setVisibility(View.GONE);
                        cartNotFound.setVisibility(View.VISIBLE);
                        placeOrder.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

    private void showItemsList(ArrayList<CartModel> cartModelArrayList)
    {

        if(cartModelArrayList!=null && cartModelArrayList.size()>0) {
            for (int m = 0; m < cartModelArrayList.size(); m++) {
                final CartModel cartModel = cartModelArrayList.get(m);

                Log.i("tag","cart user id"+firebaseUser.getUid());
                Log.i("tag","cart item id"+cartModel.getItemId());
                if(cartModel.getItemId()!=null) {
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Item").child(cartModel.getGroupId()).child(cartModel.getItemId());
                    final int finalM = m;
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i("tag", "cart items are" + dataSnapshot.getValue());

                            ItemModel itemModel = dataSnapshot.getValue(ItemModel.class);
                            itemModel.setKey(databaseReference.getKey());
                            itemModel.setParentKey(databaseReference.getParent().getKey());
                            itemModel.setQuantity(cartModel.getQuantity());
                            itemModelArrayList.add(itemModel);

                            if (finalM == itemModelArrayList.size() - 1) {
                                showItemCartRecyclerView(itemModelArrayList);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }
    }


    private void showItemCartRecyclerView(ArrayList<ItemModel> itemModelArrayList)
    {
        viewCartAdapter = new ViewCartAdapter(itemModelArrayList,ViewCartActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartRecyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        cartRecyclerView.setAdapter(viewCartAdapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
