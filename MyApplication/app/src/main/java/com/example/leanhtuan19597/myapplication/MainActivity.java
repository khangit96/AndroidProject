package com.example.leanhtuan19597.myapplication;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.Locale;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnVoice) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 10);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void queryFromAPIAI(String query) {
        /*api ai*/
        final AIConfiguration config = new AIConfiguration("5da52399d5b14bcda14f3f091e0f5f13",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(query);

        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {
                    final Result result = aiResponse.getResult();
                    String action = result.getAction().toString();

                    if (action.equals("BatDen1")) {
                        switchRelay1.setChecked(true);
                        mDatabase.child("MinhTrung/Relay1/TurnOn").setValue(true);
                    } else if (action.equals("TatDen1")) {
                        switchRelay1.setChecked(false);
                        mDatabase.child("MinhTrung/Relay1/TurnOn").setValue(false);
                    } else if (action.equals("BatDen2")) {
                        switchRelay2.setChecked(true);
                        mDatabase.child("MinhTrung/Relay2/TurnOn").setValue(true);
                    } else if (action.equals("TatDen2")) {
                        switchRelay2.setChecked(false);
                        mDatabase.child("MinhTrung/Relay2/TurnOn").setValue(false);
                    } else if (action.equals("BatDen3")) {
                        switchRelay3.setChecked(true);
                        mDatabase.child("MinhTrung/Relay3/TurnOn").setValue(true);
                    } else if (action.equals("TatDen3")) {
                        switchRelay3.setChecked(false);
                        mDatabase.child("MinhTrung/Relay3/TurnOn").setValue(false);
                    } else if (action.equals("BatDen4")) {
                        switchRelay4.setChecked(true);
                        mDatabase.child("MinhTrung/Relay4/TurnOn").setValue(true);
                    } else if (action.equals("TatDen4")) {
                        switchRelay4.setChecked(false);
                        mDatabase.child("MinhTrung/Relay4/TurnOn").setValue(false);
                    } else if (action.equals("BatDen5")) {
                        switchRelay5.setChecked(true);
                        mDatabase.child("MinhTrung/Relay5/TurnOn").setValue(true);
                    } else if (action.equals("TatDen5")) {
                        switchRelay5.setChecked(false);
                        mDatabase.child("MinhTrung/Relay5/TurnOn").setValue(false);
                    } else if (action.equals("BatDen6")) {
                        switchRelay6.setChecked(true);
                        mDatabase.child("MinhTrung/Relay6/TurnOn").setValue(true);
                    } else if (action.equals("TatDen6")) {
                        switchRelay6.setChecked(false);
                        mDatabase.child("MinhTrung/Relay6/TurnOn").setValue(false);
                    }
                }
            }
        }.execute(aiRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String textRespone = result.get(0).toString();
                queryFromAPIAI(textRespone);

            }
        }
    }
}
