package c.com.ecomerceuser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import c.com.ecomerceuser.models.CartModel;
import c.com.ecomerceuser.models.ItemModel;

import static c.com.ecomerceuser.FirebaseApplication.cartModelList;

public class ViewProductActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView productTitle,productDescription,productCost;
    Button addToCart,viewCart;
    DatabaseReference cartDatabase;
    Button productPlus,productMinus;
    EditText productQuantity;
    int count=0;
    TextView toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        toolbar = (Toolbar) findViewById(R.id.view_product_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addToCart = (Button) findViewById(R.id.add_to_cart);
        viewCart  = (Button) findViewById(R.id.view_cart);
        final ItemModel itemModel = (ItemModel) getIntent().getSerializableExtra("item");

        productTitle = (TextView) findViewById(R.id.product_title);
        productDescription = (TextView) findViewById(R.id.product_description);
        productCost = (TextView) findViewById(R.id.product_cost);

        toolbar_title = (TextView) findViewById(R.id.toolbar_title);

        productPlus = (Button) findViewById(R.id.add_product_plus);
        productMinus = (Button) findViewById(R.id.add_product_minus);
        productQuantity = (EditText) findViewById(R.id.add_product_quantity);

        count = Integer.parseInt(productQuantity.getText().toString());

        productTitle.setText(itemModel.getName());
        productCost.setText(""+itemModel.getPrice());
        productDescription.setText(itemModel.getDescription());

        toolbar_title.setText(itemModel.getName());


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        cartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart").child(firebaseUser.getUid());

        cartDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("tag","data snapchat value"+dataSnapshot.getValue());
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    CartModel cartModel = ds.getValue(CartModel.class);
                   Log.i("tag","data get item id"+ cartModel.getItemId());

                  if(cartModel.getItemId()!=null) {
                      if (cartModel.getItemId().toString().equalsIgnoreCase(itemModel.getKey())) {
                          productQuantity.setText(""+cartModel.getQuantity());
                          addToCart.setVisibility(View.GONE);
                          viewCart.setVisibility(View.VISIBLE);
                          return;
                      } else {
                          addToCart.setVisibility(View.VISIBLE);
                          viewCart.setVisibility(View.GONE);
                      }
                  }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartModel cartModel = new CartModel();
                cartModel.setItemId(itemModel.getKey());
                cartModel.setGroupId(itemModel.getParentKey());

                cartModel.setQuantity(Integer.parseInt(productQuantity.getText().toString()));
                cartDatabase.push().setValue(cartModel);
            }
        });

        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProductActivity.this,ViewCartActivity.class);
                startActivity(intent);
            }
        });


        productPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(count>0) {
                        count++;
                        productQuantity.setText(""+count);
                }
            }
        });
        productMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(count>1) {
                    count--;
                    productQuantity.setText(""+count);
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
