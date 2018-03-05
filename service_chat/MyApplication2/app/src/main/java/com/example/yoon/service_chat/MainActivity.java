package com.example.yoon.service_chat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


//
//public class MainActivity extends AppCompatActivity {
//
//    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
//    }
//
//    /**
//     * A native method that is implemented by the 'native-lib' native library,
//     * which is packaged with this application.
//     */
//    public native String stringFromJNI();
//}







public class MainActivity extends Activity {

    //액티비티에서 선언.
    private MainService mService; //서비스 클래스
    Handler mHandler = null;
    TextView tv;
    String msg1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        startServiceMethod();//서비스 시작


        // Example of a call to a native method
         tv = (TextView) findViewById(R.id.sample_text);
      //  tv.setText(stringFromJNI());

    }




    //서비스 커넥션 선언.
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            MainService.MainServiceBinder binder = (MainService.MainServiceBinder) service;
            mService = binder.getService(); //서비스 받아옴
            mService.registerCallback(mCallback); //콜백 등록
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            Log.e("Main","끊어짐");
        }
    };

    //서비스에서 아래의 콜백 함수를 호출하며, 콜백 함수에서는 액티비티에서 처리할 내용 입력
    private MainService.ICallback mCallback = new MainService.ICallback() {
        public void recvData(String msg) {
             msg1=msg;
            Thread t = new Thread(new Runnable(){
                @Override public void run() {
                    // UI 작업 수행 X
                    mHandler.post(new Runnable(){ @Override public void run()
                    {
                        tv.setText(msg1);
                        }
                    });
                } });
            t.start();


            //처리할 일들..

        }
    };


    //서비스 시작.
//    public void startServiceMethod(View v){
    public void startServiceMethod(){
        Intent Service = new Intent(this, MainService.class);
        bindService(Service, mConnection, Context.BIND_AUTO_CREATE);
    }

//액티비티에서 서비스 함수 호출
   // mService.myServiceFunc();


    @Override
    protected void onDestroy() {
        super.onDestroy();
       // onServiceDisconnected();

    }
}


