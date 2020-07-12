package com.shubh.mygita;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ShlokContentActivity extends AppCompatActivity {

    TextView sansShlok;
    TextView hindiShlok;
    NestedScrollView scrollView;
    TextView shlokCount;
    ImageButton share;
    ImageButton nextBtn;
    ImageButton prevBtn;
    Integer adhyay_selected;
    Integer shlok_selected;
    Integer versesNumber;
    RelativeLayout layout;
    File imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shlokcontent);
        layout =(RelativeLayout)findViewById(R.id.rellayout);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        shlok_selected = intent.getIntExtra("SHLOK_COUNT",1);
        adhyay_selected = intent.getIntExtra("ADHYAY_COUNT",1);
        versesNumber = Config.shlokList.get(adhyay_selected);

        sansShlok=(TextView)findViewById(R.id.sansShlok);
        hindiShlok=(TextView)findViewById(R.id.hindiShlok);
        scrollView=(NestedScrollView)findViewById(R.id.hindiShlokScroll);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/AGRA.TTF");
        hindiShlok.setTypeface(typeface);

        hindiShlok.setMovementMethod(new ScrollingMovementMethod());

        share = (ImageButton)findViewById(R.id.shareButton);
        nextBtn = (ImageButton)findViewById(R.id.nextButton);
        prevBtn = (ImageButton)findViewById(R.id.prevButton);

        // Capture the layout's TextView and set the string as its text
        shlokCount = (TextView)findViewById(R.id.shlokCount);
        shlokCount.setText(getString(R.string.shlok)+" "+shlok_selected);

        String id= "sans"+adhyay_selected+"_"+shlok_selected;
        String hin_id="shlok"+adhyay_selected+"_"+shlok_selected;

        int stringId=getResources().getIdentifier(id,"string",getPackageName());
        String san_shlok=getResources().getString(stringId);
        stringId = getResources().getIdentifier(hin_id,"string",getPackageName());
        final String hin_shlok=getResources().getString(stringId);

        sansShlok.setText(san_shlok);
        hindiShlok.setText(hin_shlok);

        StrictMode.VmPolicy.Builder builder=new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    openActivity();
                } else {
                    if (checkPermission()) {
                        requestPermissionAndContinue();
                    } else {
                        openActivity();
                    }
                }
            }
        });

        nextBtn.setOnClickListener((v)->{
            nextShlok();
        });

        prevBtn.setOnClickListener((v)->{
            prevShlok();
        });


    }

    public void nextShlok(){
        if (shlok_selected<versesNumber) {
            shlok_selected = shlok_selected + 1;
            shlokCount.setText(getString(R.string.shlok) + " " + shlok_selected);
            String id = "sans" + adhyay_selected + "_" + shlok_selected;
            String hin_id = "shlok" + adhyay_selected + "_" + shlok_selected;
            int stringId = getResources().getIdentifier(id, "string", getPackageName());
            String san_shlok = getResources().getString(stringId);
            stringId = getResources().getIdentifier(hin_id, "string", getPackageName());
            String hin_shlok = getResources().getString(stringId);
            sansShlok.setText(san_shlok);
            hindiShlok.setText(hin_shlok);
        }else {
            Toast.makeText(ShlokContentActivity.this, R.string.chapterFinish, Toast.LENGTH_SHORT).show();
        }
    }

    public void prevShlok() {
        if (shlok_selected > 1) {
            shlok_selected = shlok_selected - 1;
            shlokCount.setText(getString(R.string.shlok) + " " + shlok_selected);
            String id = "sans" + adhyay_selected + "_" + shlok_selected;
            String hindi_id = "shlok" + adhyay_selected + "_" + shlok_selected;
            int stringId = getResources().getIdentifier(id, "string", getPackageName());
            String san_shlok = getResources().getString(stringId);
            stringId = getResources().getIdentifier(hindi_id, "string", getPackageName());
            String hin_shlok = getResources().getString(stringId);
            sansShlok.setText(san_shlok);
            hindiShlok.setText(hin_shlok);
        }
    }

    public Bitmap takeScreenshot(View view) {
        //View screenView = view.getRootView();
        View screenView = getWindow().getDecorView().getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public  void store(Bitmap bm, String fileName){
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File file = new File(path, fileName +".jpg");
        try {
            // Make sure the Pictures directory exists.
            if(!path.exists())
            path.mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            shareImage(file);
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void shareImage(File file){
        Uri photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Gita");
        String shareMessage= "My Gita - The Eternal Song , Adhyay "+adhyay_selected +"\nDownload link\n\n";
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
        intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        intent.putExtra(Intent.EXTRA_STREAM, photoURI);
        try {
            startActivity(Intent.createChooser(intent, "Share Shlok"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int PERMISSION_REQUEST_CODE = 200;
    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.permission_necessary));
                alertBuilder.setMessage(R.string.storage_permission_is_encessary_to_wrote_event);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ShlokContentActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(ShlokContentActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            openActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    openActivity();
                } else {
                    finish();
                }

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openActivity() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        Bitmap bitmap = takeScreenshot(rootView);
        store(bitmap,"Shlok"+adhyay_selected+"_"+shlok_selected);
        //add your further process after giving permission or to download images from remote server.
    }


}
