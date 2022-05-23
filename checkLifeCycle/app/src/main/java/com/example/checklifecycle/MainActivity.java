package com.example.checklifecycle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btnDial, btnEnd, rotationButton;
    ImageView rotationImg;
    float degree = 10;
    /*activity생명주기*/
    private int currentScore, currentLevel;
    static final String STATE_SCORE = "playerScore";
    static final String STATE_LEVEL = "playerLevel";
    /*service생명주기*/
    EditText editText;
    Button serviceBtn;

    /*Activity 생명주기?? 자원이 제한된 환경에서 다양한 앱이 실행됨 => 자원 나눠씀(효과적으로 나눠야 함)
     * 생명주기 안에서 서로 다른 상태 전환을 위해 콜백(특정 작업)제공 (onCreate() 등..)
     * 액티비티의 생명주기에 따라 콜백 함수 실행됨 => 로그 찍어보면 실행시점 확인 가능!
     * 앱 전환시 비정상적인 종료 or 리소스 계속 소비 or 진행 상태가 저장 안 됨 등의 문제 예방.*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*시스템이 먼저 활동을 생성할 때 실행(필수 구현), 활동의 전체 수명 주기 동안 한 번만 발생해야 하는 기본 로직
         * 뷰 계층과 같은 활동 생성을 위해 super class 호출.
         * 매개변수 savedInstanceState는 onCreate안에서 수신됨. 활동의 이전 저장 상태가 포함된 Bundle객체
         * 참고로 처음 생성된 활동이면 Bundle 객체 값 null*/
        super.onCreate(savedInstanceState);
        /*액티비티가 생성되면서 onCreate가 바로 호출되는데 인터페이스 초기화 해 줌.*/

        if(savedInstanceState != null){
            currentScore = savedInstanceState.getInt(STATE_SCORE);
            currentLevel = savedInstanceState.getInt(STATE_LEVEL);
        }

        /*활동은 생성됨 상태에 머무르지 않음. 메서드 실행 완료 -> 시작 상태 -> 연달아 onStart()와 onResume()호출*/
        setContentView(R.layout.activity_main);

        setTitle("activity 생명주기 확인");

        Log.i(TAG, " onCreate()");

        init();
        initLr();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STATE_SCORE, currentScore);
        savedInstanceState.putInt(STATE_LEVEL, currentLevel);

        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, " onSaveInstanceState()");
    }

    /*onCreate다음 메소드는 onStart()
    * onRestart()의 경우에도 다음 메소드가 onStart()가 됨.
    * 액티비티가 사용자에게 보여지기 바로 직전에 호출되는 부분(활동이 시작됨 상태.)*/
    @Override
    protected void onStart(){
        super.onStart();
        /*활동이 사용자에게 표시되고 앱은 활동을 포그라운드에 보내 상호작용 할 수 있게 준비, 코드 초기화.*/
        Log.i(TAG, " onStart()");
        /*create와 마찬가지로 이 상태에서 머무르지 않음. 바로 다음 메서드 호출함.
        * 다음 메서드는 resume인데 문제 있을 때만 stop호출*/
    }

    /*onStop하고 onDestroy는 호출되지 않을 수도 있다.*/

    /*액티비티가 더 이상 사용자에게 보여지지 않을 때.
    * 메모리가 부족할 경우에 onStop()이 호출 안 될 수도 있음(onStop은은 보통부하가 큰 작업 처리하니까)
    * onPuase랑 비슷한데 더 오래걸리는 작업들 처리.*/
    @Override
    protected void onStop(){
        /*필요하지 않은 리소스 해제하거나 조정함. onPuase에서도 가능한 작업이긴 한데 차이점은
        * 1. onPuase에서는 할 수 있지만 굳이 이 작업 안 할 수도 있음. 왜냐하면 메서드 실행이 끝나기 전에 완료되지 않을 수도 있어서.
        * 2. 사용자가 멀티 윈도우 모드라고 해도 UI관련 작업 계속 진행
        * 3. CPU를 비교적 많이 소모하는 종료 작업 실행 (EX_DB에 저장)*/
        super.onStop();
        /*onStop()호출 하고, save작업이나 Update하는 쿼리문 같은 거 이 아래로 작성해주면 됨.
        * 호출되면 액티비티가 메모리 안에 머무르게 되고 활동이 재개 되면 그 정보들 다시 호출.
        * 모든 상태 및 멤버 정보 뿐만 아니라 View객체의 현재 상태도 기록.*/
        Log.i(TAG, " onStop()");
        /*onRestart()나 onDestroy호출*/
    }

    /*액티비티 소멸. 이게 호출되는 경우는 세 가지
    * 1. finish()호출 : 사용자가 활동을 아예 닫은 경우(활동 종료되는 경우)
    * 2. 구성 변경으로 인해 시스템이 활동을 소멸시키는 경우(기기 회전이나 멀티 윈도우 모드 의미)
    * 3. 시스템이 메모리 확보를 위해 액티비티 제거해야 할 때.*/
    @Override
    protected void onDestroy(){
        /*활동이 소멸되기 전에 필요한 것들 정리*/
        super.onDestroy();
        /*onDestroy()가 호출되면 진행되는 작업은 이전의 콜백에서 아직 해제되지 않은 모든 리소스 해제하는 작업.*/
        Log.i(TAG, " onDestroy()");
        /*1,3의 경우 이게 생명주기 마지막 콜백이 됨.
        * 2번의 경우 onDestroy()가 호출되면서 동시에 새 활동 인스턴스 생성이 되고 그 새로운 인스턴스에 관해 onCreate호출됨.*/
    }

    /*다른 액티비티가 보여질 때(활동 중지됐을 때) 호출됨 => 사용자가 활동을 떠나는 것을 나타내는 첫 신호.
     사용자와 상호작용을 멈췄다는 게 해당 활동이 꼭 소멸된다는 의미는 아님
        =>활동이 포그라운드에 있지 않는다는 것 뿐.
        *혹시 사용자가 멀티 윈도우 모드? 해당 activity에 대한 활동이 끝났다고 해도 계속 표시가 되니까 onResume에 있을 수도 있음(Android7.0이상)
     데이터 저장, 스레드 중지 등의 처리등을 함.*/
    @Override
    protected void onPause(){
        super.onPause();
        /*여기서 포그라운드에 있지 않을 때 실행할 필요가 없는 기능도 모두 정지 가능
        * 시스템 리소스, 센서 핸들(gps같은 거라고 함) 또는 사용자가 안 쓰는데 배터리 수명에 영향을 미치는 모든 리소스 해제.
        * onPuase는 항상 사용에 주의해야 함!! 왜냐하면
        * 1. 멀티 윈도우 모드일 경우 여전히 보이는 상태일 수도 있음.
        *   => UI 관련 리소스와 작업을 완전히 해제or조정하는 건 onStop()에서 처리 해 주는 게 좋음.
        *       how?? public void releaseCamera() { if(camera!= null) ... 이런 식으로 처리할 함수 작성
        * 2. 아주 잠깐 실행되는 메서드임
        *   => 어플 또는 사용자 저장 or 네트워크 호출 or db 트랜잭션 실행 절대 하면 안 됨
        *       how?? 부하가 큰 종료 작업전부 onStop()에 넘겨서 작성해주면 됨.*/
        Log.i(TAG, " onPause()");
    }

    /*create->start하면 곧장 resume까지 쭈욱 호출됨.
    * 액티비티가 사용자와 상호작용하기 바로 직전(포그라운드에 표시_start되고 나서 호출됨.)*/
    @Override
    protected void onResume(){
        /*onResume에 들어왔을 때 앱이 사용자와 상호작용 시작->어떤 이벤트 발생->앱에서 포커스가 떠날 때까지 이 상태에 머뭄.
        * 앱에서 포커스가 떠난다는 건 사용자가 다른 활동을 하거나... 기기 화면이 꺼지거나.. 전화가 와서 앱이 멈추거나..*/
        super.onResume();
        /*사용자에게 보이는 동안 실행해야 하는 모든 기능이 활성화 됨.*/
        Log.i(TAG, " onResume");
        /*다음 메서드인 onPause()를 호출해야 하는데... 활동중(resume중)에 onPause가 호출되었다는 건
        * 방해되는 이벤트가 발생해서 일시중지가 된 상태라는 뜻.
        *       -> 활동이 재개된다면 onResume이 다시 호출되고 onPause중에 해제하는 구성요소 초기화.
        *       -> 다시 onResume으로 전활될 때는 필요한 초기화 작업 다시 수행.*/
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.i(TAG, " onRestart()");
        /*onRestart() -> onStart() -> onResume*/
    }

    public void init(){
        Log.i(TAG, " init()");
        btnDial = findViewById(R.id.btnDial);
        btnEnd = findViewById(R.id.btnEnd);
        rotationButton = findViewById(R.id.rotationButton);
        rotationImg = findViewById(R.id.rotationImg);
        editText = findViewById(R.id.editText);
        serviceBtn = findViewById(R.id.serviceBtn);
    }

    private void initLr() {
        Log.i(TAG, " initLr()");

        btnDial.setOnClickListener(v -> {
            Uri uri = Uri.parse("tel:01012345678");
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(intent);
        });

        btnEnd.setOnClickListener(v -> {
            finish();
        });

        rotationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                degree = degree + 10;
                rotationImg.setRotation(degree);
            }
        });

        serviceBtn.setOnClickListener(v -> {
            String name = editText.getText().toString();

            //인텐트 객체만들고 부가데이터 넣기
            Intent intent = new Intent(getApplicationContext(), checkServiceCycle.class);

            /*intent 안에 두 개의 부가데이터를 추가. command라는 키와 name이라는 키 사용. */
            intent.putExtra("command", "show");
            intent.putExtra("name", name);

            /* 데이터를 Activity -> Service로 전달
            startService메서드 호출시 인텐트 객체를 파라미터로 전달함.
            이 매개 인텐트 객체 안에 어떤 서비스를 실행할 것인지 정보가 담겨있음.
            startService메서드에 담긴 intent객체는 checkServiceCycle클래스의 onStatcommand()로 전달됨.*/
            startService(intent);
            /*startService해 준 위치에서 stopService를 해주어야 함.*/
        });

        Intent passedIntent = getIntent();
        processIntent(passedIntent);
    }

    /*서비스가 이미 실행되고 있으면 onNewIntent가 호출됨.*/
    @Override
    protected void onNewIntent(Intent intent){
        processIntent(intent);
        super.onNewIntent(intent);
        Log.i(TAG, " onNewIntent()");
    }

    /*onNewIntent() 호출되었을 때 실행할 메서드
    * 별 거 없고 Intent로 전달받은 데이터를 토스트 메시지로 띄워서 확인해보기.*/
    private void processIntent(Intent intent){
        if(intent != null){
            String command = intent.getStringExtra("command");
            String name = intent.getStringExtra("name");
            for(int i = 0; i<5; i++){
                Toast.makeText(this, "command : " + command + ",name : " + name, Toast.LENGTH_SHORT).show();
            }
        }
    }
}