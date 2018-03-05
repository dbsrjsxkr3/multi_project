package com.example.yoon.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity {

    public native void ThresholdLib(long matAddrInput, long matAddrResult);

    public native void convertNativeLib(long matAddrInput, long matAddrResult);

    public native void convertNativeLibtoBoxFilter2(long matAddrInput, long matAddrResult);

    public native void addSaltAndPepperNoise(long matAddrInput, long matAddrResult);

    public native void gaussiannoise(long matAddrInput, long matAddrResult);

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("opencv_java3");
        // System.loadLibrary("imported-lib");
        System.loadLibrary("native-lib");
    }

    Mat img_input;
    Bitmap bit_original=null;
    ImageButton btn_original;
    ImageButton btn_grayscale;
    ImageButton btn_outline;
    ImageButton btn_salt;




    ImageButton img_insert;

    TextView text_salt;
    TextView text_outline;
    TextView text_grayscale;
    TextView text_original;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnOnClickListener onClickListener = new BtnOnClickListener() ;

         btn_original=(ImageButton)findViewById(R.id.img_original);
         btn_grayscale=(ImageButton)findViewById(R.id.img_grayscale);
         btn_outline=(ImageButton)findViewById(R.id.img_outline);
         btn_salt=(ImageButton)findViewById(R.id.img_salt);

        btn_original.setOnClickListener(onClickListener);
        btn_grayscale.setOnClickListener(onClickListener);
        btn_outline.setOnClickListener(onClickListener);
        btn_salt.setOnClickListener(onClickListener);

        img_insert = (ImageButton) findViewById(R.id.img_first);
        img_insert.setOnClickListener(onClickListener);//사진찍기



         text_salt=(TextView)findViewById(R.id.text_salt);
         text_outline=(TextView)findViewById(R.id.text_outline);
         text_grayscale=(TextView)findViewById(R.id.text_grayscale);
         text_original=(TextView)findViewById(R.id.text_original);


    }


    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {


            if(v.getId()==R.id.img_first){

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
            }else{


                Bitmap register=  bit_original.copy(bit_original.getConfig(),true);
                img_input = new Mat();
                Mat img_output = new Mat();
                Utils.bitmapToMat(register, img_input);



                switch (v.getId()) {
                    case R.id.img_original://처음으로
                        img_insert.setImageBitmap(null);
                        img_insert.setImageBitmap(register);
                        break;

                    case R.id.img_grayscale://흑백으로
                        convertNativeLib(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());
                        Utils.matToBitmap(img_output, register);
                        img_insert.setImageBitmap(register);
                        break;

                    case R.id.img_outline://아웃라인 경계만
                        ThresholdLib(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());
                        Utils.matToBitmap(img_output, register);
                        img_insert.setImageBitmap(register);
                        break;
                    case R.id.img_salt://소금
                        addSaltAndPepperNoise(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());
                        Utils.matToBitmap(img_output, register);
                        img_insert.setImageBitmap(register);
                        break;

                    case R.id.img_first://사진찍기

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,1);
                        break;
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // img_insert.setImageURI(data.getData());
       // img_result.setImageURI(data.getData());
        Bitmap profileBitmap;
        if((Bitmap) data.getExtras().get("data")!=null) {
             profileBitmap = (Bitmap) data.getExtras().get("data");}
else {
            profileBitmap=null;
        }

        Matrix rotateMatrix = new Matrix();
        rotateMatrix.postRotate(90); //-360~360
         bit_original = Bitmap.createBitmap(profileBitmap, 0, 0,
                profileBitmap.getWidth(), profileBitmap.getHeight(), rotateMatrix, false);

        img_insert.setImageBitmap(bit_original);


        btn_original.setVisibility(View.VISIBLE);
        btn_grayscale.setVisibility(View.VISIBLE);
        btn_outline.setVisibility(View.VISIBLE);
        btn_salt.setVisibility(View.VISIBLE);

        Bitmap register=  bit_original.copy(bit_original.getConfig(),true);
         img_input = new Mat();
        Mat img_output = new Mat();
        Utils.bitmapToMat(register, img_input);

        btn_original.setImageBitmap(register);//원본



        Bitmap register2=  bit_original.copy(bit_original.getConfig(),true);//흑백
         img_input = new Mat();
         img_output = new Mat();
        Utils.bitmapToMat(register2, img_input);

        convertNativeLib(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());
        Utils.matToBitmap(img_output, register2);
        btn_grayscale.setImageBitmap(register2);




        Bitmap register3=  bit_original.copy(bit_original.getConfig(),true);
        img_input = new Mat();
        img_output = new Mat();
        Utils.bitmapToMat(register3, img_input);



        ThresholdLib(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());
        Utils.matToBitmap(img_output, register3);
        btn_outline.setImageBitmap(register3);



        Bitmap register4=  bit_original.copy(bit_original.getConfig(),true);
        img_input = new Mat();
        img_output = new Mat();
        Utils.bitmapToMat(register4, img_input);

        addSaltAndPepperNoise(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());
        Utils.matToBitmap(img_output, register4);
        btn_salt.setImageBitmap(register4);





        text_salt.setVisibility(View.VISIBLE);
        text_outline.setVisibility(View.VISIBLE);
        text_grayscale.setVisibility(View.VISIBLE);
        text_original.setVisibility(View.VISIBLE);



    }
}


