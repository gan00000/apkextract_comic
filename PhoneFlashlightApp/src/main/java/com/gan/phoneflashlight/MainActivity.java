package com.gan.phoneflashlight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    Button lightBtn;
    TextView lightTipsTextView;

    CameraManager manager;
    Camera camera;

    boolean isOpening = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightBtn = findViewById(R.id.open_flight_btn);
        lightTipsTextView = findViewById(R.id.flight_on_off_tips);
        if (!isOpening){
            lightTipsTextView.setText(R.string.flight_off);
        }else {
            lightTipsTextView.setText(R.string.flight_on);
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "你的手机没有闪光灯!\n  启用屏幕手电模式!", Toast.LENGTH_SHORT).show();
//            switchBtn.setVisibility(View.INVISIBLE);
//            screenLight();
        }

        lightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOpening){
                    openFlash();
                    lightTipsTextView.setText(R.string.flight_on);
                    lightBtn.setSelected(true);

                }else {
                    closeFlash();
                    lightTipsTextView.setText(R.string.flight_off);
                    lightBtn.setSelected(false);
                }
                isOpening = !isOpening;
            }
        });



        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "mainactivity_create");
        mFirebaseAnalytics.logEvent("MainActivity_Create",bundle);
    }


    @SuppressLint("NewApi")
    private void openFlash() {

        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("openFlash",bundle);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
                if (manager != null) {
                    manager.setTorchMode("0", true);
                }
            } else {
                camera = Camera.open();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void closeFlash() {


        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("closeFlash",bundle);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (manager == null) {
                    return;
                }
                manager.setTorchMode("0", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (camera == null) {
                return;
            }
            camera.stopPreview();
            camera.release();
        }
    }
}
