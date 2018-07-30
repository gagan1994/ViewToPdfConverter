package com.example.gagan.viewtopdf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.generatepdfwithview.ViewToPdfConverter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_image;
    View mRootView;
    private String fileName = "resume.pdf";
    private File filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkPermissions()) {
            initData();
        }
        mRootView = findViewById(R.id.root_view);
    }

    private void initData() {
        iv_image = (ImageView) findViewById(R.id.iv_image);
        findViewById(R.id.btn_generate).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(iv_image);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_generate:
                generatePdf();
                break;
            case R.id.btn_show:
                showPdf();
                break;
        }

    }

    private void generatePdf() {
        ViewToPdfConverter viewToPdfConverter = new ViewToPdfConverter
                .ViewToPdfBuilder().setPathToStore("Resume")
                .setFileNameWithoutExt("resume").build();
        if(viewToPdfConverter.generatePdf(mRootView)){
             filePath = viewToPdfConverter.getFilePath();
             fileName = viewToPdfConverter.getFileName();
        }
    }

    private void showPdf() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(filePath, fileName));
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }


    private boolean checkPermissions() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat
                    .requestPermissions(
                            this,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            101);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    initData();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
