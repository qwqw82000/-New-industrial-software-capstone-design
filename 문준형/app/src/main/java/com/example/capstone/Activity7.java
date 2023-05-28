package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity7 extends AppCompatActivity {

    private Button btn_7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_7);

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
        String userId = intent.getStringExtra("userId");
        Log.d("userId: ",userId);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("User");

        usersRef.child(userId).child("userPoint").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // 현재 userPoint 값을 가져옴
                    int userPoint = snapshot.getValue(Integer.class);

                    // userPoint에 1000을 더한 값을 newPoint로 설정
                    int newPoint = userPoint + 1000;

                    // newPoint 값을 Firebase에 업데이트
                    usersRef.child(userId).child("userPoint").setValue(newPoint)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                User user = snapshot.getValue(User.class);

                                                // Firebase에서 조회한 사용자 데이터 사용
                                                String userName = user.getUserName();
                                                int updatedUserPoint = user.getUserPoint();

                                                TextView userNameTextView = findViewById(R.id.userNameTextView);
                                                userNameTextView.setText("성명: " + userName);

                                                TextView userBirthTextView = findViewById(R.id.PointTextView);
                                                userBirthTextView.setText("적립예정마일리지: 1000");

                                                TextView userPointTextView = findViewById(R.id.userPointTextView);
                                                userPointTextView.setText("적립후 마일리지: " + updatedUserPoint);
                                            } else {
                                                // 사용자 데이터가 존재하지 않음
                                                // 처리 로직 추가
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getApplicationContext(), "서버 오류.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "서버 오류.", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "서버 오류.", Toast.LENGTH_LONG).show();
            }
        });

        btn_7 = findViewById(R.id.btn_7);
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity7.this, Activity8.class);
                startActivity(intent);
            }
        });
    }

}

//public class User {
//    private String userName;
//    private String userBirth;
//    private int userPoint;
//
//    public User() {
//
//    }
//
//    public User(String userName, String userBirth, int userPoint) {
//        this.userName = userName;
//        this.userBirth = userBirth;
//        this.userPoint = userPoint;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getUserBirth() {
//        return userBirth;
//    }
//
//    public void setUserBirth(String userBirth) {
//        this.userBirth = userBirth;
//    }
//
//    public int getUserPoint() {
//        return userPoint;
//    }
//
//    public void setUserPoint(int userPoint) {
//        this.userPoint = userPoint;
//    }
//}
