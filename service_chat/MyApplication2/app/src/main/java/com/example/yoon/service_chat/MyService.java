package com.example.yoon.service_chat;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class MyService extends Service {

    public String ip = ""; // IP
    public int port = 2889; // PORT번호
    public DataInputStream networkReader;
    public DataOutputStream networkWriter;
    public Socket socket;

    public String html = "";
    public Handler mHandler;
    public JSONObject jsonreceivechat=null;

    public IBinder mBinder = new MyBinder();

    public MyService() {
    }

    // 외부로 데이터를 전달하려면 바인더 사용
    // Binder 객체는 IBinder 인터페이스 상속구현 객체입니다
    //public class Binder extends Object implements IBinder
    public class  MyBinder extends Binder {
        public MyService getService() { // 서비스 객체를 리턴
            Log.e("서비스","2");
            return MyService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // 액티비티에서 bindService() 를 실행하면 호출됨
        // 리턴한 IBinder 객체는 서비스와 클라이언트 사이의 인터페이스 정의한다
        Log.e("서비스","3");
        return mBinder; // 서비스 객체를 리턴
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //소켓 연결
        ExampleThread thread=new ExampleThread();
        Log.e("서비스","4");
        thread.start();
        Log.e("서비스","100");


        //메신저가 오면 각각 프래그먼트에 전송한다.
        sendThread  sendthread =new sendThread();
        sendthread.start();
    }

    public class ExampleThread extends Thread {
        public void run() {
            try {
                Log.e("서비스","5");
                setSocket(ip, port);
                Log.e("서비스","6");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    public class sendThread extends Thread{
        public void run(){
            while(true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //   getRunActivity();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("서비스","디스트로이");
    }



    //현재 엑티비티 이름 알아내기
    void getRunActivity()	{
        ActivityManager activity_manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task_info = activity_manager.getRunningTasks(9999);
        for(int i=0; i<task_info.size(); i++) {
            Log.d("엑티비티", "[" + i + "] activity:"+ task_info.get(i).topActivity.getPackageName() + " >> " + task_info.get(i).topActivity.getClassName());
        }
    }

    int count=0;
    public int retrunInteger(){
        return count++;
    }


    public void sendtoserver(String message) throws IOException {
        networkWriter.writeUTF(message);
    }

    public void setSocket(String ip, int port) throws IOException {
        try {
            Log.e("ip","1");
            socket = new Socket(ip, port);
            Log.e("ip","2");
            networkWriter = new DataOutputStream(socket.getOutputStream());

            Log.e("ip","3");
            networkReader = new DataInputStream(socket.getInputStream());


            JSONObject jsonmsg=new JSONObject();
           // jsonmsg.put("이름",username);
            String mid=jsonmsg.toString();
            networkWriter.writeUTF(mid);


            jsonmsg.put("case","대기실");
           // jsonmsg.put("이름",username);
            mid=jsonmsg.toString();
            networkWriter.writeUTF(mid);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();


            Log.e("ip","4");

            Log.e("ip","5");
            System.out.println(e);

            e.printStackTrace();
            Log.e("ip","6");
        }
        Log.e("ip","7");
    }



    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public String GetClassName(Context context)
    {
        ActivityManager activitymanager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> runningtaskinfo = activitymanager.getRunningTasks(1);

        return runningtaskinfo.get(0).topActivity.getClassName();
    }





}
