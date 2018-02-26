package c.com.ecomerceuser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiClickedListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import c.com.ecomerceuser.helper.DateTimeUtilities;
import c.com.ecomerceuser.models.ChatMessage;

import static android.view.View.VISIBLE;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.ChatListAdapterListener {

    private RecyclerView chatRecyclerView;
    private EmojiEditText message;
    private ImageView send;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String senderId,receiverId;
    private FirebaseAuth mAuth;


    List<ChatMessage> chatMessageList;

    ChatAdapter chatAdapter;
    ImageView emojiButton;
    EmojiPopup emojiPopup;
    ViewGroup rootView;
    TextView onlineStatus;

    ImageView attachIcon;
    LinearLayout bottomLayout;
    RelativeLayout bottomMainLayout;

    LinearLayout chatDateTimeLayout;
    TextView chatDateTimeTextView;
    private String TAG ="chatactivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatMessageList = new ArrayList<ChatMessage>();

        onlineStatus    = (TextView) findViewById(R.id.chat_toolbar_online);

        chatRecyclerView = (RecyclerView) findViewById(R.id.chat_recylerview);
        message = (EmojiEditText) findViewById(R.id.emojiEditText);
        send = (ImageView) findViewById(R.id.send);
        mAuth = FirebaseAuth.getInstance();


        chatDateTimeLayout   = (LinearLayout)findViewById(R.id.date_and_time_chat_layout);
        chatDateTimeTextView = (TextView) findViewById(R.id.date_and_time_bubble);
        chatDateTimeLayout.setEnabled(false);
        chatDateTimeLayout.setClickable(false);



        emojiButton = (ImageView) findViewById(R.id.main_activity_emoji);
        emojiButton.setColorFilter(ContextCompat.getColor(this, R.color.emoji_icons), PorterDuff.Mode.SRC_IN);
        rootView = (ViewGroup) findViewById(R.id.main_activity_root_view);

        attachIcon = (ImageView) findViewById(R.id.attach_icon);
        attachIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this,"test",Toast.LENGTH_LONG).show();

                bottomLayout.setVisibility(VISIBLE);
                bottomMainLayout.setVisibility(VISIBLE);
            }
        });


        try {
            senderId = mAuth.getCurrentUser().getUid();
            receiverId = getIntent().getStringExtra("userid");

        } catch (Exception e) {
            e.printStackTrace();

        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Messages");
        databaseReference.keepSynced(true);



        chatAdapter = new ChatAdapter(chatMessageList, senderId,this);
        chatRecyclerView = (RecyclerView) findViewById(R.id.chat_recylerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatRecyclerView.setAdapter(null);
        chatRecyclerView.setAdapter(chatAdapter);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setSenderId(senderId);
                chatMessage.setReceiverId(receiverId);
                chatMessage.setMessage(message.getText().toString());
                Log.i("tag","time is"+getCurrentDateAndTime());

                chatMessage.setSent_time(getCurrentDateAndTime());
                chatMessage.setSenderId_receiverId(senderId+"_"+receiverId);
                databaseReference.push().setValue(chatMessage);
                databaseReference.keepSynced(true);


                message.setText("");
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
            }
        });



        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("val", "data snapchat" + dataSnapshot.getValue());
                Log.i("val", "children" + dataSnapshot.getChildren());
                Log.i("tag", "listener for single event called");
                //   for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                Log.i("tag", "chatmessage" + chatMessage);
                if (chatMessage.getSenderId_receiverId().toString().equalsIgnoreCase(senderId + "_" + receiverId)
                        || chatMessage.getSenderId_receiverId().toString().equalsIgnoreCase(receiverId + "_" + senderId)) {
                    chatMessageList.add(chatMessage);

                    chatRecyclerView.scrollToPosition(chatMessageList.size() - 1);
                    chatAdapter.notifyItemInserted(chatMessageList.size() - 1);

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


        if (Build.VERSION.SDK_INT >= 11) {
            chatRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (bottom < oldBottom) {
                        chatRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    chatRecyclerView.smoothScrollToPosition(chatMessageList.size() - 1);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }, 100);
                    }
                }
            });

        }

        //Add to Activity
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View v) {
                emojiPopup.toggle();
            }
        });


        setUpEmojiPopup();

        chatRecyclerView.addOnScrollListener(new CustomScrollListener());

    }


    public String getCurrentDateAndTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    @Override
    protected void onStart() {
        super.onStart();

  /*      DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference().child("Customers").child(mAuth.getCurrentUser().getUid());
        databaseReference.child("online").setValue(true);
        databaseReference.child("lastseen").setValue(true);
 */   }

    @Override public void onBackPressed() {
        if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override protected void onStop() {
        if (emojiPopup != null) {
            emojiPopup.dismiss();
        }
        super.onStop();
   /*     DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference().child("Customers").child(mAuth.getCurrentUser().getUid());
        databaseReference.child("online").setValue(false);
        databaseReference.child("lastseen").setValue(ServerValue.TIMESTAMP);
*/
    }


    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
                    @Override public void onEmojiBackspaceClicked(final View v) {
                        Log.d(TAG, "Clicked on Backspace");
                    }
                })
                .setOnEmojiClickedListener(new OnEmojiClickedListener() {
                    @Override public void onEmojiClicked(final Emoji emoji) {
                        Log.d(TAG, "Clicked on emoji");
                    }
                })
                .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                    @Override public void onEmojiPopupShown() {
                        emojiButton.setImageResource(R.drawable.ic_keyboard);
                    }
                })
                .setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
                    @Override public void onKeyboardOpen(final int keyBoardHeight) {
                        Log.d(TAG, "Opened soft keyboard");
                    }
                })
                .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                    @Override public void onEmojiPopupDismiss() {
                        emojiButton.setImageResource(R.drawable.emoji_ios_category_people);
                    }
                })
                .setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
                    @Override public void onKeyboardClose() {
                        Log.d(TAG, "Closed soft keyboard");
                    }
                })
                .build(message);
    }




    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(statusReceiver);
        super.onPause();
    }


    @Override
    protected void onResume() {

        //   LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("chat-message-received"));
        LocalBroadcastManager.getInstance(this).registerReceiver(statusReceiver, new IntentFilter("chat-user-status-received"));
        super.onResume();
        AppPreferences chatPreferences = new AppPreferences(ChatActivity.this);
        chatPreferences.setUserId(mAuth.getCurrentUser().getUid());
        chatPreferences.setIsChatActive("true");
        Intent intent = new Intent(this, UserOnlineStatus.class);
        startService(intent);
    }



    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("chat-user-status-received"))
            {
                //     setUserStatus();
            }
        }
    };


    @Override
    public void onIconClicked(int position) {

    }

    @Override
    public void onIconImportantClicked(int position) {

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onRowLongClicked(int position) {

    }


    public class CustomScrollListener extends RecyclerView.OnScrollListener {
        public CustomScrollListener() {

        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    Runnable mRunnable;
                    Handler mHandler = new Handler();
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            chatDateTimeLayout.setVisibility(View.GONE); //This will remove the View. and free s the space occupied by the View
                        }
                    };
                    mHandler.postDelayed(mRunnable, 2 * 1000);
                    break;

                case RecyclerView.SCROLL_STATE_DRAGGING:
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    callScrollDateTime();
                    break;

            }

        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            if (dx > 0) {
                System.out.println("Scrolled Right");
            } else if (dx < 0) {
                System.out.println("Scrolled Left");
            } else {
                System.out.println("No Horizontal Scrolled");
            }
            if (dy > 0) {
                if (chatAdapter.itemPositionInAdapter < 3) {
                    chatDateTimeLayout.setVisibility(View.GONE);
                } else if (chatMessageList != null && (chatMessageList.size() - 2 <= chatAdapter.itemPositionInAdapter)) {
                    chatDateTimeLayout.setVisibility(View.GONE);
                } else {
                    callScrollDateTime();
                }
            } else if (dy < 0) {
                if (chatAdapter.itemPositionInAdapter < 3) {
                    chatDateTimeLayout.setVisibility(View.GONE);
                } else if (chatMessageList != null && (chatMessageList.size() - 2 <= chatAdapter.itemPositionInAdapter)) {
                    chatDateTimeLayout.setVisibility(View.GONE);
                } else {
                    callScrollDateTime();
                }
            } else {
                System.out.println("No Vertical Scrolled");
                chatDateTimeLayout.setVisibility(View.GONE);
            }
        }
    }


    private void callScrollDateTime() {
        chatDateTimeLayout.setVisibility(VISIBLE);
        ChatMessage chatMessageEntity = chatAdapter.getItemScrolledPosition();
        if (chatMessageEntity != null) {

            String currentDate = chatMessageEntity.getSent_time();

            String dateIs = currentDate.split(" ")[0];


            Log.i("tag","current date only is"+DateTimeUtilities.getCurrentOnlyDate());

            if (dateIs.equalsIgnoreCase(DateTimeUtilities.getCurrentOnlyDate())) {
                chatDateTimeTextView.setText("  TODAY");
            }
/*
 else if (currentDate.equalsIgnoreCase(DateTimeUtilities.getYesterdayDate())) {

                chatDateTimeTextView.setText(" YESTERDAY ");
            } */else {
                chatDateTimeTextView.setText(DateTimeUtilities.getConvertedTime(currentDate));
            }
        }

    }




}
