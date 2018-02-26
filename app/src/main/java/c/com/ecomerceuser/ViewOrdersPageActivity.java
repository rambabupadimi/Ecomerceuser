package c.com.ecomerceuser;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import c.com.ecomerceuser.models.CartModel;
import c.com.ecomerceuser.models.ItemModel;
import c.com.ecomerceuser.models.OrderModel;

public class ViewOrdersPageActivity extends AppCompatActivity {

    RecyclerView viewOrderRecyclerView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    Toolbar toolbar;

    RelativeLayout notFountLayout;
    TextView notFoundMessage;
    ImageView notFoundIcon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders_page);
        viewOrderRecyclerView = (RecyclerView) findViewById(R.id.view_order_recyclerview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        notFountLayout = (RelativeLayout) findViewById(R.id.not_found_layout);
        notFoundIcon   = (ImageView) findViewById(R.id.not_found_icon);
        notFoundMessage = (TextView) findViewById(R.id.not_found_text);
        notFoundMessage.setText("Your order list is empty");



        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference   =  FirebaseDatabase.getInstance().getReference().child("Orders").child(firebaseUser.getUid());

         databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.getChildrenCount()>0)
                {
                    notFountLayout.setVisibility(View.GONE);
                    viewOrderRecyclerView.setVisibility(View.VISIBLE);
                }
                else
                {
                    notFountLayout.setVisibility(View.VISIBLE);
                    viewOrderRecyclerView.setVisibility(View.GONE);
                }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
        searchRecyclerview("");

    }


    private void searchRecyclerview(String s)
    {

        final FirebaseRecyclerAdapter<OrderModel,OrderViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<OrderModel,OrderViewHolder>(
                        OrderModel.class,
                        R.layout.order_adapter_layout,
                        OrderViewHolder.class,
                        databaseReference
                ) {
                    @Override
                    protected void populateViewHolder(final OrderViewHolder viewHolder, final OrderModel model, final int position) {
                        viewHolder.setName(model.getName());
                        viewHolder.setQuantity(""+model.getQuantity());
                        viewHolder.setCost(""+model.getPrice());
                        viewHolder.setOrderStatus(model.getOrderStatus());
                        viewHolder.setPhone(""+model.getPhonenumber());
                        viewHolder.setAddress(""+model.getAddress());
                        viewHolder.setDoorNumber(""+model.getDoornumber());
                    }
                };
        viewOrderRecyclerView.setAdapter(firebaseRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        viewOrderRecyclerView.setLayoutManager(linearLayoutManager);
    }


    public static class OrderViewHolder extends RecyclerView.ViewHolder
    {

        View mview;
        public OrderViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setDoorNumber(String doorNumber)
        {
            TextView doorNumberTextView =  (TextView) mview.findViewById(R.id.adapter_order_door_number);
            doorNumberTextView.setText(""+doorNumber);

        }


        public void setAddress(String address)
        {
            TextView titleTextView =  (TextView) mview.findViewById(R.id.adapter_order_address);
            titleTextView.setText(""+address);

        }
        public void setPhone(String phone)
        {
            TextView titleTextView =  (TextView) mview.findViewById(R.id.adapter_order_phone);
            titleTextView.setText(""+phone);

        }

        public void setName(String title)
        {
            TextView titleTextView =  (TextView) mview.findViewById(R.id.adapter_order_title);
            titleTextView.setText(""+title);
        }

        public void setCost(String cost)
        {
            TextView costTextView =  (TextView) mview.findViewById(R.id.adapter_order_price);
            costTextView.setText("Rs : "+cost);
        }

        public void setQuantity(String quantity)
        {
            TextView quantityTextView =  (TextView) mview.findViewById(R.id.adapter_order_quantity);
            quantityTextView.setText(""+quantity);
        }

        public void setOrderStatus(int status)
        {
            View orderStatus = mview.findViewById(R.id.adapter_order_status);
            LinearLayout linearLayout = (LinearLayout) mview.findViewById(R.id.layout_bg);
            TextView layoutText = (TextView) mview.findViewById(R.id.layout_text);
            if(status==0) {
                orderStatus.setBackgroundColor(Color.parseColor("#64B5F6"));
                linearLayout.setBackgroundColor(Color.parseColor("#64B5F6"));
                layoutText.setText("Your order is pending");
            }
            else if(status==1)
            {
                orderStatus.setBackgroundColor(Color.parseColor("#FF8A65"));
                linearLayout.setBackgroundColor(Color.parseColor("#FF8A65"));
                layoutText.setText("Your order is inprogress");

            }
            else if(status==2)
            {
                orderStatus.setBackgroundColor(Color.parseColor("#81C784"));
                linearLayout.setBackgroundColor(Color.parseColor("#81C784"));
                layoutText.setText("Your order is completed");

            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
