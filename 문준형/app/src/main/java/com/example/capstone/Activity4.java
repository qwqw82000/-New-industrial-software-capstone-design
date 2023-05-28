package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Activity4 extends AppCompatActivity {

    private Button btn_4;
    private Button btn_4_2;

    private ImageButton home_4;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4);

        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("Is on?", "Turning immersive mode mode off. ");
        } else {
            Log.i("Is on?", "Turning immersive mode mode on.");
        }
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

        Intent intent = getIntent();
        String barcodeResult = intent.getStringExtra("barcode_result");
        Log.d(barcodeResult, "barcodeResult: ");


        // QR 코드 스캔 결과로부터 JSON 형식의 사용자 ID 값을 얻음
        String qrCodeData = "JSON 형식의 사용자 ID 값";

        try {
            JSONObject json = new JSONObject(barcodeResult);
            userId = json.getString("userId");
            Log.d("userId: ", userId);

            // Firebase에서 사용자 데이터 조회
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("User");



            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);

                        // Firebase에서 조회한 사용자 데이터 사용
                        String userName = user.getUserName();
                        String userBirth = user.getUserBirth();
                        int userPoint = user.getUserPoint();
                        String userDrug = user.getUserDrug();

                        TextView userNameTextView = findViewById(R.id.userNameTextView);
                        userNameTextView.setText("성명: " + userName);

                        TextView userBirthTextView = findViewById(R.id.userBirthTextView);
                        userBirthTextView.setText("생년월일: " + userBirth);

                        TextView userDrugTextView = findViewById(R.id.userDrugTextView);
                        userDrugTextView.setText("수거대상조제약: " + userDrug);


                    } else {
                        Toast.makeText(getApplicationContext(), "건강마일리지 어플리케이션에서 회원가입을 해주세요.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "서버오류. 잠시후 다시시도해주세요.", Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            // JSON 파싱 오류 처리 로직 추가
        }

        btn_4 = findViewById(R.id.btn_4);
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity4.this, Activity2.class);
                finishAffinity();
                startActivity(intent);
            }
        });

        btn_4_2 = findViewById(R.id.btn_4_2);
        btn_4_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity4.this, Activity5.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        home_4 = (ImageButton) findViewById(R.id.home_4);
        home_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity4.this, MainActivity.class);
                finishAffinity();
                startActivity(intent);
            }
        });

    }

}
class User implements Serializable {
    private String userName;
    private String userBirth;
    private String userDrug;

    private int userPoint;


    public User() {

    }

    public User(String userName, String userBirth, String userDrug, int userPoint) {
        this.userName = userName;
        this.userBirth = userBirth;
        this.userDrug = userDrug;
        this.userPoint = userPoint;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserBirth() {
        return userBirth;
    }

    public void setUserBirth(String userBirth) {
        this.userBirth = userBirth;
    }

    public String getUserDrug() {
        return userDrug;
    }

    public void setUserDrug(String userDrug) {
        this.userDrug = userDrug;
    }

    public int getUserPoint() {
        return userPoint;
    }

    public void setUserPoint(int userPoint) {
        this.userPoint = userPoint;
    }
}
