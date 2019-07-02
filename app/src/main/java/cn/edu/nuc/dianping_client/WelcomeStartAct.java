package cn.edu.nuc.dianping_client;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import cn.edu.nuc.dianping_client.utils.SharedUtils;


//启动APP界面
//3秒暂停

public class WelcomeStartAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_start);


        Timer timer=new Timer();
        timer.schedule(new Task(),3000);//延时执行任务,3000毫秒
    }
    //
    class Task extends TimerTask{

        @Override
        public void run() {
            //页面跳转
            //不是第一次启动
            if(SharedUtils.getWelcomeBoolean(getBaseContext())){
                startActivity(new Intent(getBaseContext(),MainActivity.class));
            }else{
                startActivity(new Intent(WelcomeStartAct.this,WelcomeGuideAct.class));
                //保存访问记录
                SharedUtils.putWelcomeBoolean(getBaseContext(),true);
            }
            finish();
        }
    }
}
