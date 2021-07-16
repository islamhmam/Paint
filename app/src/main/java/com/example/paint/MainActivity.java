package com.example.paint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {
    int currentColor;
   SignatureView signatureView;
    TextView tvPenSize;
    SeekBar penSize;
    ImageButton btnColor,btnEraser,btnSave;

    private static String fileName;
    File path=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/Paint");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signatureView=findViewById(R.id.signature_view);
        penSize=findViewById(R.id.seek_pensize);
        tvPenSize=findViewById(R.id.tv_pensize_text);
        btnColor=findViewById(R.id.btn_color);
        btnEraser=findViewById(R.id.btn_eraser);
        btnSave=findViewById(R.id.btn_save);
        currentColor= ContextCompat.getColor(this,R.color.black);

        setPermission();

        SimpleDateFormat sdf=new SimpleDateFormat("yymmdd_hhmmss",
                Locale.getDefault());

        String date= sdf.format(new Date());

        fileName=path+"/"+date+".png";
        if (!path.exists()){

            path.mkdirs();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
            }
        });

        penSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                tvPenSize.setText(i+" dp");
                signatureView.setPenSize(i);
                penSize.setMax(50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setpentcolor();
            }
        });


    }

    private void saveImage() throws IOException {
File file=new File(fileName);

Bitmap bitmap=signatureView.getSignatureBitmap();

        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0,bos);
        byte[] bitmabe=bos.toByteArray();

        FileOutputStream fos=new FileOutputStream(file);
        fos.write(bitmabe);
        fos.flush();
        fos.close();
        Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();

    }

    private void setpentcolor() {
        AmbilWarnaDialog dialog=new AmbilWarnaDialog(this, currentColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
            currentColor=color;
            signatureView.setPenColor(color);
            }
        });
        dialog.show();

    }

    private void setPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                     permissionToken.continuePermissionRequest();
                    }
                }).check();

}}