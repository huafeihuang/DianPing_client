package cn.edu.nuc.dianping_client;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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
import com.mob.MobSDK;
import com.mob.commons.SHARESDK;

import java.util.HashMap;

import javax.security.auth.login.LoginException;

import cn.edu.nuc.dianping_client.consts.CONSTS;
import cn.edu.nuc.dianping_client.entity.ResponseObject;
import cn.edu.nuc.dianping_client.entity.User;
import cn.edu.nuc.dianping_client.utils.MyUtils;
import cn.edu.nuc.dianping_client.utils.SharedUtils;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

/*
/登录注册
 */

public class MyloginActivity extends AppCompatActivity implements PlatformActionListener{

    public static int REGISTERSUCCESS=1;
    @ViewInject(R.id.login_check_random)
    private TextView checkRandom;
    @ViewInject(R.id.login_btn)
    private Button loginBtn;
    @ViewInject(R.id.login_pass)
    private EditText loginPass;
    @ViewInject(R.id.login_uname)
    private EditText loginName;
    @ViewInject(R.id.login_register)
    private TextView register;
    @ViewInject(R.id.login_by_qq)
    private TextView loginByQQ;
    @ViewInject(R.id.login_by_weixin)
    private TextView loginByWeiXin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_login_act);
        ViewUtils.inject(this);
        String uname = SharedUtils.getUserName(MyloginActivity.this);

        loginName.setText(uname);
        MobSDK.init(this);//初始化SDK

//        checkRandom.setOnClickListener((View.OnClickListener)this);
//        loginBtn.setOnClickListener((View.OnClickListener)this);
//        register.setOnClickListener((View.OnClickListener)this);
//        loginByQQ.setOnClickListener((View.OnClickListener)this);
//        loginByWeiXin.setOnClickListener((View.OnClickListener) this);
        //初始化验证码
        setRandomView(checkRandom);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobSDK.clearUser();
    }

    private void setRandomView(TextView textView) {
        textView.setText(MyUtils.getRandom(5));
    }


    @OnClick({R.id.login_check_random,R.id.login_btn,R.id.login_register,R.id.login_by_qq,R.id.login_by_weixin,R.id.login_back})
    private void onClick(View view){
        switch (view.getId()){
            case R.id.login_check_random://验证码
                checkRandom.setText(MyUtils.getRandom(4));//设置验证码
                break;
            case R.id.login_btn://登陆
                handleLogin();
                break;
            case R.id.login_register://注册
                startActivity(new Intent(this, MyRegisterActivity.class));
                break;
            case R.id.login_by_qq:
                loginByQQ();
                break;
            case R.id.login_by_weixin:
                loginByWeixin();
                break;
            case R.id.login_back:
                finish();
                break;
            default:
                break;
        }
    }




    /**
     * 处理登陆
     */
    private void handleLogin()
    {
        final String uName = loginName.getText().toString();
        final String password = loginPass.getText().toString();
        //使用GET请求访问服务器
        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_LOGIN + "&username=" + uName + "&password=" + password, new RequestCallBack<String>() {


            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Gson gson = new GsonBuilder().create();
                ResponseObject<User> object = gson.fromJson(responseInfo.result,
                        new TypeToken<ResponseObject<User>>(){}.getType());
                if(object.getState()==1){//登陆成功
                    SharedUtils.putUserName(MyloginActivity.this,object.getDatas().getName());
                    SharedUtils.putUserPwd(MyloginActivity.this,object.getDatas().getLoginPwd());
                    SharedUtils.putUserID(getApplicationContext(),object.getDatas().getId());

                    loginSuccss(loginName.getText().toString());//出来登录信息
                    Toast.makeText(MyloginActivity.this,object.getMsg(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(MyloginActivity.this,"登陆失败",Toast.LENGTH_LONG).show();
            }
        });

//        Intent intent = new Intent(this,MainActivity.class);
//        intent.putExtra("loginName",uName);
//        setResult(MyUtils.RequestLoginCode,intent);
//        finish();

    }

    //登陆成功的时候的执行方法
    private void loginSuccss(String uname){
//        String uname = loginName.getText().toString();
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("loginName",uname);
        setResult(MyUtils.RequestLoginCode,intent);
        SharedUtils.putUserState(getApplication(),"1");

        finish();

    }

    /*
    三方登陆作废，之后的所有函数都是三方登陆的所用
     */
    /**QQ的第三方登陆*/
    private void loginByQQ()
    {
        Log.i("TAG","QQ登录");
        //得到QQ的平台
        Platform plat = ShareSDK.getPlatform(QQ.NAME);
//        MobSDK.setActivity(this);//抖音登录适配安卓9.0

        plat.setPlatformActionListener( this);
//        plat.showUser(null);
//        plat.SSOSetting(true);
        //判断是否通过
        if (plat.isAuthValid())
        {
            String uname = plat.getDb().getUserName();//获取三方的显示名称
            System.out.println("验证通过。。。。。。"+uname);
            Log.i("TAG","QQ登录成功");
            //返回我的页面
            loginSuccss(uname);
        }
        else
        {
            //如果没有授权登录
            plat.showUser(null);
            Log.i("TAG","QQ登录失败");
        }

    }
    /**微信第三方登陆*/
    private void loginByWeixin()
    {

    }
    /*
    implements PlatformActionListener之后的三个需要实现的方法
     */
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        String uname = platform.getDb().getUserName();//获取第三方平台显示的名称
        System.out.println("uname====="+uname);
        //返回我的页面
        loginSuccss(uname);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Toast.makeText(this, platform.getName()+"授权已失败，请重试", Toast.LENGTH_SHORT).show();
        Log.i("TAG","错误： "+throwable.getMessage());
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(this, platform.getName()+"授权已取消", Toast.LENGTH_SHORT).show();
    }
}
