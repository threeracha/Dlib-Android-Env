package com.example.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "android_camera_example";

    public native void Detect();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("dlib");
        System.loadLibrary("native-lib");
    }


    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator +filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d(TAG, "copyFile :: 다음 경로로 파일복사 " + pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 " + e.toString());
        }

    }


    private class Task extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void Void) {
            super.onPostExecute(Void);

            // 갤러리에 반영
            File a = new File("/storage/emulated/0/camtest/output.bmp");
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(a));
            sendBroadcast(mediaScanIntent);

            ImageView imageView = (ImageView)findViewById(R.id.result_img);
            File imgFile = new File("/storage/emulated/0/camtest/output.bmp");
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();

            Bitmap resized = null;

            float dpi = getResources().getDisplayMetrics().density;
            int px = (int) (350 * dpi);

            if (width > px) {
                resized = Bitmap.createScaledBitmap(bitmap, (width * px) / height, px, true);//http://javalove.egloos.com/m/67828
                height = resized.getHeight();
                width = resized.getWidth();
                imageView.setImageBitmap(resized);
            } else imageView.setImageBitmap(bitmap);


            Log.d("@@@", px + " " + width + " " + height);

        }

        @Override
        protected Void doInBackground(Void... voids) {


            Detect();


            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        copyFile("shape_predictor_68_face_landmarks.dat");

        Log.v("android external storage path",android.os.Environment.getExternalStorageDirectory().getAbsolutePath());


        Button button2 = findViewById(R.id.btn_detect);
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new Task().execute();
            }
        });

    }
}