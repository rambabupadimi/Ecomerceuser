package c.com.ecomerceuser;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by Ramu on 25-12-2017.
 */

public class ViewCartAdapter extends RecyclerView.Adapter<ViewCartAdapter.MyViewHolder> {

    private ArrayList<ItemModel> itemModelArrayList;
    Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,price,quantity;

        ImageView removeCartItem;
        LinearLayout headerLayout;
        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.cart_title);
            price = (TextView) view.findViewById(R.id.cart_price);
            quantity = (TextView) view.findViewById(R.id.cart_quantity);
            headerLayout = (LinearLayout) view.findViewById(R.id.header_layout);
            removeCartItem = (ImageView) view.findViewById(R.id.cart_remove);
        }
    }


    public ViewCartAdapter(ArrayList<ItemModel> itemModelArrayList,Context context) {
        this.itemModelArrayList = itemModelArrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_cart_adapter_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
            if(position==0)
            {
                holder.headerLayout.setVisibility(View.VISIBLE);
            }else
            {
                holder.headerLayout.setVisibility(View.GONE);
            }

            final ItemModel  itemModel = itemModelArrayList.get(position);
            Log.i("tag","name"+itemModel.getName()+"/"+itemModel.getPrice()+"/"+itemModel.getQuantity());

            holder.title.setText(itemModel.getName());
            holder.price.setText(""+itemModel.getPrice() * itemModel.getQuantity());
            holder.quantity.setText(""+itemModel.getQuantity());

            holder.removeCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItemsFromCart(itemModel.getKey(),position);

                }
            });
    }


    public void removeAt(int position) {
        if(itemModelArrayList!=null && itemModelArrayList.size()>0) {
            itemModelArrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemModelArrayList.size());
        }
    }

    @Override
    public int getItemCount() {
        return itemModelArrayList.size();
    }

    private void removeItemsFromCart(final String itemId, final int position)
    {
       FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference   =  FirebaseDatabase.getInstance().getReference().child("Cart").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    CartModel cartModel =  ds.getValue(CartModel.class);
                    if(cartModel.getItemId()!=null) {
                        if (cartModel.getItemId().toString().equalsIgnoreCase(itemId)) {
                            databaseReference.child(ds.getKey()).removeValue();
                            removeAt(position);
                            Toast.makeText(context,"Item Removed From Cart",Toast.LENGTH_LONG).show();
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
