package c.com.ecomerceuser;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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

public class OrderPage extends AppCompatActivity {


    Toolbar orderToolbar;
    TextView phoneNumber,address,doorNumber;
    Button confirmOrder;

    ArrayList<ItemModel> itemModelArrayList=null;
    ArrayList<CartModel> cartModelArrayList =null;

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    TextView  setLocation;

    int PLACE_PICKER_REQUEST = 1;
    PlacePicker.IntentBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);
        orderToolbar = (Toolbar) findViewById(R.id.order_toolbar);
        setSupportActionBar(orderToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phoneNumber = (TextView) findViewById(R.id.order_phone);
        address     = (TextView) findViewById(R.id.order_address);
        doorNumber = (TextView) findViewById(R.id.order_door_no);
        confirmOrder = (Button) findViewById(R.id.confirm_order);
        setLocation = (TextView) findViewById(R.id.set_location);
        builder = new PlacePicker.IntentBuilder();

         itemModelArrayList = (ArrayList<ItemModel>) getIntent().getSerializableExtra("itemModelArrayList");
         cartModelArrayList = (ArrayList<CartModel>) getIntent().getSerializableExtra("cartModelArrayList");

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumber.getText().toString().length()<9)
                {
                    Toast.makeText(OrderPage.this,"Enter correct number",Toast.LENGTH_LONG).show();
                }
                else if(doorNumber.getText().toString().length()<3)
                {
                    Toast.makeText(OrderPage.this,"Enter correct door number",Toast.LENGTH_LONG).show();
                }
                else if(address.getText().toString().length()<3)
                {
                    Toast.makeText(OrderPage.this,"Enter correct address",Toast.LENGTH_LONG).show();
                }
                else {
                    showInfoDialog();
                }
            }
        });

        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivityForResult(builder.build(OrderPage.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                address.setText(""+place.getAddress());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showInfoDialog() {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog alertDialog = builder.create();
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_layout, null);
            Button confirm = (Button) view.findViewById(R.id.confirm);
            Button cancel = (Button) view.findViewById(R.id.cancel);

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference   =  FirebaseDatabase.getInstance().getReference().child("Orders").child(firebaseUser.getUid());

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemModelArrayList!=null)
                    {
                        for(int i=0;i<itemModelArrayList.size();i++)
                        {
                            ItemModel itemModel = itemModelArrayList.get(i);
                            OrderModel orderModel = new OrderModel();
                            orderModel.setItemId(itemModel.getKey());
                            orderModel.setGroupId(itemModel.getParentKey());
                            orderModel.setPrice(itemModel.getQuantity() * itemModel.getPrice());
                            orderModel.setName(itemModel.getName());
                            orderModel.setQuantity(itemModel.getQuantity());
                            orderModel.setAddress(address.getText().toString());
                            orderModel.setDoornumber(doorNumber.getText().toString());
                            orderModel.setPhonenumber(phoneNumber.getText().toString());
                            orderModel.setOrderStatus(0);
                            databaseReference.push().setValue(orderModel);
                            removeItemsFromCart(itemModel.getKey());
                            if(i==itemModelArrayList.size()-1)
                            {
                                alertDialog.dismiss();
                                Intent intent = new Intent(OrderPage.this,ViewOrdersPageActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                        }


                    }


                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setView(view);
            alertDialog.show();
            alertDialog.setCancelable(false);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void removeItemsFromCart(final String itemId)
    {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference   =  FirebaseDatabase.getInstance().getReference().child("Cart").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    CartModel cartModel =  ds.getValue(CartModel.class);
                    if(cartModel.getItemId()!=null) {
                        if (cartModel.getItemId().toString().equalsIgnoreCase(itemId)) {
                            databaseReference.child(ds.getKey()).removeValue();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
