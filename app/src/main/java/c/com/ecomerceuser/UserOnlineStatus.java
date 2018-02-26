package c.com.ecomerceuser;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UserOnlineStatus extends Service {
    private Context context ;
    private String TAG = "UserOnlineStatus";
    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            process();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service Started Time " + new Date());
        this.context = getApplicationContext();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 0,  10*1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "On StartCommand Started");
        process();
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void process() {
        Log.i(TAG, "In Process User is Online");

        AppPreferences chatPreferences = new AppPreferences(context);

        Log.i(TAG,""+chatPreferences.getIsChatActive());
        if(chatPreferences.getIsChatActive().toString().equalsIgnoreCase("true") ){
            Log.i(TAG,"User is Online");

            Intent intent = new Intent("chat-user-status-received");
            intent.putExtra("message", "This is my message!");

            // sending broadcast user is online with status  "chat-user-status-received"
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            // sending status to server
            sendStatus(chatPreferences.getUserId().toString());
        }
        else{
            Log.i(TAG,"User is Away");
            if(mTimer!=null)
                mTimer.cancel();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service is going to destroy.");
        AppPreferences chatPreferences = new AppPreferences(context);
        Log.i(TAG,""+chatPreferences.getIsChatActive());
        if (mTimer!= null)
            mTimer.cancel();
        stopSelf();
        super.onDestroy();
    }

    private void sendStatus(String userId){
        DatabaseReference currentuserdb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        currentuserdb.child("userStatus").setValue("true");
    }


}