package com.suhun.takepictures;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private String tag = MainActivity.class.getSimpleName();
    private ImageView img1, img2;
    private File saveDir;
    private ActivityResultLauncher<Intent> takePhotoResultLauncher1 =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if(result.getResultCode() == RESULT_OK){
                                Bundle bundle = result.getData().getExtras();
                                Bitmap bitmap = (Bitmap) bundle.get("data");
                                img1.setImageBitmap(bitmap);
                            }
                        }
                    });

    private ActivityResultLauncher<Intent> takePhotoResultLauncher2 =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if(result.getResultCode() == RESULT_OK){
                                String filepath = new File(saveDir, "/test1.jpg").getAbsolutePath();
                                Log.d(tag, "----filepath " + filepath + "----");
                                Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                                img2.setImageBitmap(bitmap);
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isSendRequestPermissionsAboutCamera()){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 123);

        }else{
            initCamera();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 123){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED){//使用者同意全部的相機相關隱私設定
                initCamera();
            }else{
                finish();
            }
        }
    }
    private boolean isSendRequestPermissionsAboutCamera(){
        boolean result = false;
        //只要有一項使用者不同意，就發送請求
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
           ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
        ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            result = true;
        }

        return result;
    }
    private void initCamera(){
        img1 = findViewById(R.id.lid_Image1);
        img2 = findViewById(R.id.lid_Image2);
        saveDir = Environment.getExternalStoragePublicDirectory("HiskioPhoto");
    }

    public void takePhotoNotSaveToDevice(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoResultLauncher1.launch(intent);
    }

    public void takePhotoSaveToDevice(View view){
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider",
                new File(saveDir, "/test1.jpg"));
        Log.d(tag, "----Uri " + uri + "----");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        takePhotoResultLauncher2.launch(intent);
    }
}