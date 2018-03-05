package com.example.yoon.service_chat;

import android.app.Service;
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

public class MainService extends Service {
//서비스에서 선언.

    public String ip = "13.124.126.234"; // IP
    public int port = 2889; // PORT번호
    public DataInputStream networkReader;
    public DataOutputStream networkWriter;
    public Socket socket;

    public String html = "";
    public Handler mHandler;
    public JSONObject jsonreceivechat=null;




    //서비스 바인더 내부 클래스 선언
    public class MainServiceBinder extends Binder {
        MainService getService() {
            return MainService.this; //현재 서비스를 반환.
        }
    }

    private final IBinder mBinder = new MainServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        //소켓 연결
        MainService.ExampleThread thread=new MainService.ExampleThread();
        Log.e("서비스","4");
        thread.start();
        Log.e("서비스","100");

//        receive thread2 = new receive();
//        thread2.start();
//        //메신저가 오면 각각 프래그먼트에 전송한다.
//        MyService.sendThread sendthread =new MyService.sendThread();
//        sendthread.start();
    }

    public class ExampleThread extends Thread {
        public void run() {
            try {
                //서버와 해당 포트로 연결
                setSocket(ip, port);
                //연결이 완료되면 서버와 통신한다 // 메시지 받을 수 있음
                receive thread2 = new receive();
                thread2.start();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }


    //서버에서 오는 데이터를 받고 해당하는 엑티비티로 메시지 보내줌
    public class receive extends Thread {
        public void run() {
            String mes;
            while (true) {
            try {
                    mes = networkReader.readUTF().toString();
                    mCallback.recvData(mes);
                } catch(IOException e1){
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
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





    //콜백 인터페이스 선언
    public interface ICallback {
        public void recvData(String msg); //액티비티에서 선언한 콜백 함수.
    }

    private ICallback mCallback;

    //액티비티에서 콜백 함수를 등록하기 위함.
    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }

    //액티비티에서 서비스 함수를 호출하기 위한 함수 생성
    public void myServiceFunc(){
        //서비스에서 처리할 내용
    }


//서비스에서 액티비티 함수 호출은..
   //mCallback.recvData();

}

