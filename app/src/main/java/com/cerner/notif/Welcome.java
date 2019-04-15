package com.cerner.notif;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Welcome extends AppCompatActivity {
    Button logoutButton,notifButton;
    FirebaseAuth firebaseAuth;
    TextView welcomeTextView ;
    private final String CHANNEL_ID = "mychannel";
    private final int not_id = 123;
    private static final int REQUEST_ID = 12345;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        logoutButton = findViewById(R.id.logoutButton);
        welcomeTextView = findViewById(R.id.welcomeText);
        firebaseAuth = FirebaseAuth.getInstance();
        welcomeTextView.setText(firebaseAuth.getCurrentUser().getEmail());
       // Toast.makeText(this, "Welcome class", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent (this, SH.class);
        startService(intent);
        notifButton = findViewById(R.id.NotifButton);
        notifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(Welcome.this,CHANNEL_ID);
                builder.setSmallIcon(R.drawable.ic_priority_high_black_24dp);
                builder.setContentTitle("Generated Notification");
                builder.setContentText("Hi Akkiei");
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                builder.setChannelId(CHANNEL_ID);
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(Welcome.this);
                notificationManagerCompat.notify(not_id,builder.build());
              //  Notification notification = new Notification();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(Welcome.this,MainActivity.class));
            }
        });

    }


    public void onBackPressed(){
            moveTaskToBack(true);
    }


}
