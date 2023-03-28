package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Activity4 extends AppCompatActivity {

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

        try {
            JSONObject jsonObject = new JSONObject(barcodeResult);
            String userName = jsonObject.getString("userName");
            String userBirth = jsonObject.getString("userBirth");
            String userDrug = jsonObject.getString("userDrug");

            // 값 출력
            TextView userNameTextView = findViewById(R.id.userNameTextView);
            userNameTextView.setText("성명: " + userName);

            TextView userBirthTextView = findViewById(R.id.userBirthTextView);
            userBirthTextView.setText("생년월일: " + userBirth);

            TextView userDrugTextView = findViewById(R.id.userDrugTextView);
            userDrugTextView.setText("폐기대상약: " + userDrug);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}