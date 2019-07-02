package cn.edu.nuc.dianping_client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import cn.edu.nuc.dianping_client.consts.CONSTS;
import cn.edu.nuc.dianping_client.entity.Address;
import cn.edu.nuc.dianping_client.entity.ResponseObject;
import cn.edu.nuc.dianping_client.entity.User;
import cn.edu.nuc.dianping_client.utils.MyUtils;
import cn.edu.nuc.dianping_client.utils.SharedUtils;


/*
我的详细地址/
 */
public class MyAdressActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    static int ISEDIBLE = 0;
//    @ViewInject(R.id.address_name)
//    EditText nameText;
    @ViewInject(R.id.address_usermane)
    EditText userText;
    @ViewInject(R.id.address_province)
    EditText privinceText;
    @ViewInject(R.id.address_tel)
    EditText telText;
    @ViewInject(R.id.address_aname)
    EditText addressText;

    @ViewInject(R.id.old_pwd)
    EditText oldText;
    @ViewInject(R.id.new_pwd)
    EditText newText;

    @ViewInject(R.id.logout_btn)
    Button logoutBtn;
    @ViewInject(R.id.change_pwd_btn)
    Button changePwdBtn;
    @ViewInject(R.id.save_info_btn)
    Button saveBtn;
    @ViewInject(R.id.save_change_pwd_btn)
    Button savePwdBtn;

    @ViewInject(R.id.change_layout)
    LinearLayout show_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_adress);
        ViewUtils.inject(this);
        initView();

    }
    @OnClick({R.id.logout_btn,R.id.change_pwd_btn,R.id.save_info_btn,R.id.address_back,R.id.save_change_pwd_btn})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.change_pwd_btn://修改密码
                if(show_layout.getVisibility()==View.VISIBLE){
                    show_layout.setVisibility(View.INVISIBLE);
                }else{
                    show_layout.setVisibility(View.VISIBLE);
                }

//                finish();
                break;
            case R.id.save_info_btn:
                if(ISEDIBLE==1){//修改完准备插入了
                    changeEdibleFalse();
                    ISEDIBLE=0;
                    sendChangeInfo();
                }else{//准备编辑
                    changeEdibleTrue();
                    ISEDIBLE=1;
                }
                break;
            case R.id.address_back://返回键
                finish();
                break;
            case R.id.save_change_pwd_btn://隐藏的修改密码按钮
                changePwd();
            case R.id.logout_btn://退出登录；状态，清除
                SharedUtils.putUserState(getApplicationContext(),"0");
                Intent intent = new Intent(this,MainActivity.class);
                setResult(MyUtils.RequestLogoutCode,intent);
                finish();
                break;



        }
    }
    /*
    修改密码/
     */
    private void changePwd() {
        String uName = userText.getText().toString().trim();
        String oldPwd=oldText.getText().toString().trim();
        final String newPwd=newText.getText().toString().trim();
        Log.i(TAG, "sendChangeInfo: "+CONSTS.USER_Changge_Info_URI + "?flag=pwd&username=" + uName + "&oldpwd=" + oldPwd +
                "&newPwd=" + newPwd);
        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_Changge_Info_URI + "?flag=pwd&username=" + uName + "&oldpwd=" + oldPwd +
                "&newPwd=" + newPwd, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                ResponseObject<User> object = new GsonBuilder().create().fromJson(responseInfo.result, new TypeToken<ResponseObject<User>>(){}.getType());
                if(object.getState()==1){
                    User user= (User) object.getDatas();
                    if(user!=null&&user.getLoginPwd().equals(newPwd))
                        Toast.makeText(MyAdressActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MyAdressActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(MyAdressActivity.this,"连接失败",Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*
    将改变的信息插入数据表/
    完成
     */
    private void sendChangeInfo() {//发送数据，GET请求返回一个状态即可
        String uName = userText.getText().toString().trim();
        String province=privinceText.getText().toString().trim();
        String tel=telText.getText().toString().trim();
        String address_=addressText.getText().toString().trim();

        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_Changge_Info_URI + "?flag=info"+"&username=" + uName + "&province=" + province +
                "&tel=" + tel + "&address=" + address_, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                ResponseObject<Address> object = new GsonBuilder().create().fromJson(responseInfo.result, new TypeToken<ResponseObject<Address>>(){}.getType());
                if(object.getState()==1){
                    Address address= (Address)object.getDatas();
                    String uname=address.getUserName()==null?"":address.getUserName();
                    String province=address.getProvince()==null?"":address.getProvince();
                    String tel=address.getTel()==null?"":address.getTel();
                    String address_=address.getAddressName()==null?"":address.getAddressName();
                    privinceText.setText(province);
                    telText.setText(tel);
                    addressText.setText(address_);
                }else{
                    Toast.makeText(MyAdressActivity.this,"未改变有效信息",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(MyAdressActivity.this,"修改信息失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    显示个人信息
    完成/
     */
    private void initView() {//
        Intent intent = getIntent();
        String uName = intent.getStringExtra("userName").trim();
        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_Detail_URI + "?username=" + uName,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        ResponseObject<Address> object = new GsonBuilder().create().fromJson(responseInfo.result, new TypeToken<ResponseObject<Address>>(){}.getType());
                        if(object.getState()==1){
                            Address address= (Address)object.getDatas();
                            String uname=address.getUserName()==null?"":address.getUserName();
                            String province=address.getProvince()==null?"":address.getProvince();
                            String tel=address.getTel()==null?"":address.getTel();
                            String address_=address.getAddressName()==null?"":address.getAddressName();
                            userText.setText(uname);
                            privinceText.setText(province);
                            telText.setText(tel);
                            addressText.setText(address_);
                        }else{
                            Toast.makeText(MyAdressActivity.this,"未返回有效信息",Toast.LENGTH_SHORT).show();
                        }


                }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(MyAdressActivity.this,"获取信息失败",Toast.LENGTH_SHORT).show();
                    }

        });
        changeEdibleFalse();
    }

    /*
    更改编辑性，可编辑
     */
    private void changeEdibleTrue(){
        privinceText.setEnabled(true);
        addressText.setEnabled(true);
        telText.setEnabled(true);
    }

    /*
    更改编辑性，不可编辑
     */
    private void changeEdibleFalse(){
        userText.setEnabled(false);
        privinceText.setEnabled(false);
        addressText.setEnabled(false);
        telText.setEnabled(false);
    }
}
