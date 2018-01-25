package com.example.leanhtuan19597.myapplication;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    SwitchCompat switchRelay1, switchRelay2, switchRelay3, switchRelay4, switchRelay5, switchRelay6;
    CheckBox cbRelay1, cbRelay2, cbRelay3, cbRelay4, cbRelay5, cbRelay6;
    DatabaseReference mDatabase;
    ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isOnline()) {
            initControls();
            initEvents();
        } else {
            new MaterialDialog.Builder(this)
                    .title("Thông báo")
                    .content("Vui lòng bật kết nối mạng !")
                    .negativeText("Đóng")
                    .canceledOnTouchOutside(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Toast.makeText(getApplicationContext(), "On", Toast.LENGTH_LONG).show();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            System.exit(1);
                        }
                    }).show();
        }
    }

    /**/
    public void initControls() {
        switchRelay1 = findViewById(R.id.switchRelay1);
        switchRelay2 = findViewById(R.id.switchRelay2);
        switchRelay3 = findViewById(R.id.switchRelay3);
        switchRelay4 = findViewById(R.id.switchRelay4);
        switchRelay5 = findViewById(R.id.switchRelay5);
        switchRelay6 = findViewById(R.id.switchRelay6);

        cbRelay1 = findViewById(R.id.cbRelay1);
        cbRelay2 = findViewById(R.id.cbRelay2);
        cbRelay3 = findViewById(R.id.cbRelay3);
        cbRelay4 = findViewById(R.id.cbRelay4);
        cbRelay5 = findViewById(R.id.cbRelay5);
        cbRelay6 = findViewById(R.id.cbRelay6);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        pg = new ProgressDialog(MainActivity.this);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**/
    public void initEvents() {
        processing(switchRelay1, cbRelay1, "Relay1");
        processing(switchRelay2, cbRelay2, "Relay2");
        processing(switchRelay3, cbRelay3, "Relay3");
        processing(switchRelay4, cbRelay4, "Relay4");
        processing(switchRelay5, cbRelay5, "Relay5");
        processing(switchRelay6, cbRelay6, "Relay6");

        pg.setMessage("Đang khởi động hệ thống...");
        pg.setCancelable(false);
        pg.show();

        mDatabase.child("MinhTrung").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dt : dataSnapshot.getChildren()) {
                    restoreData(dt);
                }
                pg.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**/
    public void restoreData(DataSnapshot dt) {
        String key = dt.getKey();
        Boolean blink = (Boolean) dt.child("Blink").getValue();
        Boolean turnOn = (Boolean) dt.child("TurnOn").getValue();


        if (key.equals("Relay1")) {
            if (turnOn) {
                switchRelay1.setChecked(true);
                cbRelay1.setEnabled(true);
            }

            if (blink) {
                cbRelay1.setChecked(true);
                switchRelay1.setEnabled(false);
            }
        } else if (key.equals("Relay2")) {
            if (turnOn) {
                switchRelay2.setChecked(true);
                cbRelay2.setEnabled(true);
            }

            if (blink) {
                cbRelay2.setChecked(true);
                switchRelay2.setEnabled(false);
            }
        } else if (key.equals("Relay3")) {
            if (turnOn) {
                switchRelay3.setChecked(true);
                cbRelay3.setEnabled(true);
            }

            if (blink) {
                cbRelay3.setChecked(true);
                switchRelay3.setEnabled(false);
            }
        } else if (key.equals("Relay4")) {
            if (turnOn) {
                switchRelay4.setChecked(true);
                cbRelay4.setEnabled(true);
            }

            if (blink) {
                cbRelay4.setChecked(true);
                switchRelay4.setEnabled(false);
            }
        } else if (key.equals("Relay5")) {
            if (turnOn) {
                switchRelay5.setChecked(true);
                cbRelay5.setEnabled(true);
            }

            if (blink) {
                cbRelay5.setChecked(true);
                switchRelay5.setEnabled(false);
            }
        } else if (key.equals("Relay6")) {
            if (turnOn) {
                switchRelay6.setChecked(true);
                cbRelay6.setEnabled(true);
            }

            if (blink) {
                cbRelay6.setChecked(true);
                switchRelay6.setEnabled(false);
            }
        }

    }

    /**/
    public void processing(final SwitchCompat switchRelay, final CheckBox cbRelay, final String relay) {

        switchRelay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cbRelay.setEnabled(true);
                    mDatabase.child("MinhTrung/" + relay + "/TurnOn").setValue(true);
                    // Toast.makeText(getApplicationContext(), "Bat den 1", Toast.LENGTH_LONG).show();
                } else {
                    mDatabase.child("MinhTrung/" + relay + "/TurnOn").setValue(false);
                    cbRelay.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Tat den 1", Toast.LENGTH_LONG).show();
                }
            }
        });

        cbRelay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    switchRelay.setEnabled(false);
                    mDatabase.child("MinhTrung/" + relay + "/Blink").setValue(true);
                    Toast.makeText(getApplicationContext(), "Bat nhap nhay", Toast.LENGTH_LONG).show();
                } else {
                    switchRelay.setEnabled(true);
                    mDatabase.child("MinhTrung/" + relay + "/Blink").setValue(false);
                    Toast.makeText(getApplicationContext(), "Tat nhap nhay", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
}