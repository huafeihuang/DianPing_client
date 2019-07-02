package cn.edu.nuc.dianping_client;
import cn.edu.nuc.dianping_client.consts.CONSTS;
import cn.edu.nuc.dianping_client.entity.ResponseObject;
import cn.edu.nuc.dianping_client.entity.User;
import cn.smssdk.EventHandler;
//import android.net.http.EventHandler;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.http.Headers;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.lang.reflect.Type;

import cn.smssdk.SMSSDK;

/*
注册功能，使用Mob
 */


public class MyRegisterActivity extends AppCompatActivity {

    @ViewInject(R.id.register_get_check_pass)
    private Button checkPassBtn;
    @ViewInject(R.id.register_back)
    private ImageView backImage;

    @ViewInject(R.id.register_phone)
    private EditText phone;
    @ViewInject(R.id.register_check_upass)
    private EditText phoneRandom;
    @ViewInject(R.id.register_upass)
    private EditText password;

    private CountTimer countTimer;

    private EventHandler eventHandler;

    public static int REGISTER=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_register_act);
        ViewUtils.inject(this);

        countTimer = new CountTimer(60000,1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    //发送短信的方法
    public void sendSMSRandom(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE);
            int checkCallPhonePermission2 = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED
                    && checkCallPhonePermission2 != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},1);
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
            }else{
                Toast.makeText(this,"打开权限失败",Toast.LENGTH_LONG).show();
            }

        }

//        SMSS
        SMSSDK.setAskPermisionOnReadContact(true);
        eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                super.afterEvent(event, result, data);
                if (result == SMSSDK.RESULT_COMPLETE)
                {
                    // 回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE)
                    {
                        // 提交验证码成功
//                        System.out.println("验证码校验成功");
                        Log.i("TAG","验证码校验成功");
                        REGISTER=0;
                        //验证成功，开始发送注册信息
                        registerUser();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE)
                    {
                        // 获取验证码成功
                        Log.i("TAG","验证码发送成功");
//                        System.out.println("验证码发送成功");
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES)
                    {
                        // 返回支持发送验证码的国家列表
                    }
                } else
                {
                    ((Throwable) data).printStackTrace();
                }

            }
        };
        //无GUI接口
        SMSSDK.registerEventHandler(eventHandler);//注册短信回调

        String phoneName = phone.getText().toString();
        SMSSDK.getVerificationCode("+86",phoneName.toString());
    }

    @OnClick({R.id.register_get_check_pass,R.id.register_back,R.id.register_check_upass,R.id.register_phone,R.id.register_btn})
    private void onClick(View view){
        switch (view.getId()){
            case R.id.register_get_check_pass://点击获取验证码
                //开启倒计时
                countTimer.start();
                sendSMSRandom();
                break;
            case R.id.register_back://点击返回按钮
                finish();
                break;

            case R.id.register_btn://点击返回按钮
                SMSSDK.submitVerificationCode("+86",phone.getText().toString(),phoneRandom.getText().toString());
                break;
            default:
                break;
        }
    }

    //每一分钟可点击验证一次
    public class CountTimer extends CountDownTimer{
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //间隔内执行的方法
        @Override
        public void onTick(long millisUntilFinished) {//参数；时间间隔
            checkPassBtn.setText(millisUntilFinished/1000+"s后发送");
            checkPassBtn.setBackgroundResource(R.drawable.btn_light_press);
            checkPassBtn.setClickable(false);
        }

        @Override
        public void onFinish() {
            checkPassBtn.setText(R.string.register_get_check_num);//点击获取资源
            checkPassBtn.setBackgroundResource(R.drawable.btn_light_normal);
            checkPassBtn.setClickable(true);
        }
    }

    //注册方法，发送注册信息
    private void registerUser()
    {
        if (phone.getText().toString().trim().length() <= 0)
        {
            phone.setError(Html.fromHtml("<font color=red>用户名不能为空！</font>"));
            return;
        }
        if (password.getText().toString().trim().length() <= 0)
        {
            password.setError(Html.fromHtml("<font color=red>密码不能为空！</font>"));
            return;
        }

        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_REGISTER+"&username="
                +phone.getText().toString().trim()+"&password="+password.getText().toString().trim(), new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                ResponseObject<User> object = new GsonBuilder().create().fromJson(responseInfo.result, new TypeToken<ResponseObject<User>>(){}.getType());
                if(object.getState()==1){
                    Toast.makeText(MyRegisterActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MyRegisterActivity.this,MyloginActivity.class);
                    intent.putExtra("loginName",phone.getText().toString());
                    //标记状态
                    REGISTER=1;
                    finish();
                }else{
                    Toast.makeText(MyRegisterActivity.this,"注册失败",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(MyRegisterActivity.this,"访问失败："+msg,Toast.LENGTH_LONG).show();
            }
        });
    }
}
