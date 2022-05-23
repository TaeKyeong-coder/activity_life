package com.example.checklifecycle;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class checkServiceCycle extends Service {
    private static final String TAG = "MainActivity2";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, " service의 onCreate()");
    }

    /*또 다른 구성요소(ex_액티비티)가 서비스를 시작하도록 요청하는 경우 호출됨.
    * 해당 서비스의 작업이 완료되었을 때 해당 서비스를 중단하는 것은 개발자 본인의 책임.
    * Activity에서 Service로 Data이동.*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*서비스가 시작되고 백그라운드에서 무한히 실행될 수 있음.*/
        Log.i(TAG, " service의 onStartCommand");

        if(intent == null)
            /*받아온 intent(어떤 서비스를 실행할지 정보를 담은 매개체)가 null이면
            * 서비스 비정상 종료 => 서비스 재시작(START_STICKY)*/
            return Service.START_STICKY;
        else
            /*서비스 실행. 서비스 내용이 짧으면 바로 여기서 구현하기도 함.*/
            processCommand(intent, flags, startId);

        return super.onStartCommand(intent, flags, startId);
    }

    /*실행될 실제 서비스*/
    private void processCommand(Intent intent, int flags, int startId) {
        //intent에서 부가데이터 가져오기(getStringExtra메서드 사용)
        String command = intent.getStringExtra("command");
        String name = intent.getStringExtra("name");

        Log.i(TAG, "command: " + command + " / name: " + name);

        /*서비스 실행중이라는 것을 볼 수 있게 하려고 5초동안 log출력하게 한 것. 큰 의미는 x*/
        for(int i=0; i<5; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            Log.i(TAG, " Waiting:" + i + " sec");
        }

        /*Activity를 띄우기 위해 Activity객체 만들어주기
        * 1. Intent 객체를 new 연산자로 생성
        * 2. 첫 번째 parameter는 getApplicationContext()메서드를 호출하여 Context객체가 전달되도록 함.
        * 3. 두 번재 parameter는 MainActivity.class 객체*/
        Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);

        /*intent 안에 flag추가하기.
        * 서비스에서 메서드를 호출할 때는 새로운 Task를 생성하도록 FLAG_ACTIVITY_NEW_TASK 플래그 인텐트에 추가
        *   why?? 서비스는 화면이 없는데 Activity는 화면이 있으니까 새로운 태스크 생성이 필요.
        *          Activity객체가 이미 메모리에 만들어져 있는 경우(이 코드의 경우 MainActivity)는 재사용하면 된다.
        *          즉, 새로운 화면을 생성하거나 있던 거 재사용(SINGLE_TOP, CLEAR_TOP)할 수 있는 플래그를 추가해주면 됨.*/
        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        showIntent.putExtra("command", "show");
        showIntent.putExtra("name ", name + " first service");
        /*재사용이든 새로 만든 것이든 showIntent가 준비되었으니까 Activity로 넘겨준다.
        * startActivity에 담긴 인텐트 객체는 checkServiceCycle클래스의 onStartCommand 메서드로 전달되어 서비스 시작*/
        startActivity(showIntent);
        stopService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, " onDestroy()");
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public checkServiceCycle() {
    }

    @Override
    public boolean stopService(Intent name) {
        Log.i(TAG, " stopService()");
        return super.stopService(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}