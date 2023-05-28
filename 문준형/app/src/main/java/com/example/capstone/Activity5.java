package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Activity5 extends AppCompatActivity {

    private Button btnCapture;
    private ImageView imgCapture;
    private static final int Image_Capture_Code = 1;

    private Button btn_5;

    private String userId;

    private ImageButton home_5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_5);

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
        userId = intent.getStringExtra("userId");
        Log.d("userId: ",userId);

        btnCapture =(Button)findViewById(R.id.btnTakePicture);
        imgCapture = (ImageView) findViewById(R.id.capturedImage);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt,Image_Capture_Code);
            }
        });

        btn_5 = findViewById(R.id.btn_5);
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity5.this, Activity2.class);
                startActivity(intent);
            }
        });

        home_5 = (ImageButton) findViewById(R.id.home_5);
        home_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity5.this, MainActivity.class);
                finishAffinity();
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                bp = RotateBitmap(bp, 90);
                int cx = 128, cy = 128;
                bp = Bitmap.createScaledBitmap(bp, cx, cy, false);
                int[] pixels = new int[cx * cy];
                bp.getPixels(pixels, 0, cx, 0, 0, cx, cy);

                ByteBuffer input_img = getInputImage_2(pixels, cx, cy);
                Interpreter tf_lite = getTfliteInterpreter("densnet.tflite");

                float[][] pred = new float[1][1];
                tf_lite.run(input_img, pred);

                float predictionThreshold = 0.5f; // 임계값 설정
                float prediction = pred[0][0];
                Log.d("prediction", String.valueOf(prediction));
                if (prediction <= predictionThreshold) {
                    Toast.makeText(getApplicationContext(), "약입니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Activity5.this, Activity7.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "약이아닙니다.", Toast.LENGTH_LONG).show();
                    String text = "약을 재배치하시거나 회수해주십시오";
                    TextView textView1 = findViewById(R.id.textView1);
                    textView1.setText(text);
                }


                imgCapture.setImageBitmap(bp);


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap temp = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        temp = ThumbnailUtils.extractThumbnail(temp, 1080, 1080);

        return temp;
    }
    //  1차원 배열 사용
    private ByteBuffer getInputImage_2(int[] pixels, int cx, int cy) {
        ByteBuffer input_img = ByteBuffer.allocateDirect(cx * cy * 3 * 4);
        input_img.order(ByteOrder.nativeOrder());

        for (int i = 0; i < cx * cy; i++) {
            int pixel = pixels[i];        // ARGB : ff4e2a2a

            input_img.putFloat(((pixel >> 16) & 0xff) / (float) 255);
            input_img.putFloat(((pixel >> 8) & 0xff) / (float) 255);
            input_img.putFloat(((pixel >> 0) & 0xff) / (float) 255);
        }

        return input_img;
    }


    // 모델 파일 인터프리터를 생성하는 공통 함수
    // loadModelFile 함수에 예외가 포함되어 있기 때문에 반드시 try, catch 블록이 필요하다.
    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(Activity5.this, modelPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 모델을 읽어오는 함수
    // MappedByteBuffer 바이트 버퍼를 Interpreter 객체에 전달하면 모델 해석을 할 수 있다.
    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }



}