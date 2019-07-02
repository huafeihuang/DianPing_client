package cn.edu.nuc.dianping_client.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.List;

import cn.edu.nuc.dianping_client.MainActivity;
import cn.edu.nuc.dianping_client.MyAdressActivity;
import cn.edu.nuc.dianping_client.MyOrdersActivity;
import cn.edu.nuc.dianping_client.MyReviewActivity;
import cn.edu.nuc.dianping_client.MyloginActivity;
import cn.edu.nuc.dianping_client.R;
import cn.edu.nuc.dianping_client.consts.CONSTS;
import cn.edu.nuc.dianping_client.entity.ResponseObject;
import cn.edu.nuc.dianping_client.utils.MyUtils;
import cn.edu.nuc.dianping_client.utils.SharedUtils;


public class FragmentMy extends Fragment implements View.OnClickListener {


    @ViewInject(R.id.my_index_login_text)
    TextView loginText;

    @ViewInject(R.id.my_index_login_image)
    ImageView loginImage;
    //订单
    @ViewInject(R.id.my_index_item_order)
    TextView my_index_item_order;

    //消费总额
    @ViewInject(R.id.my_index_item_wallet)
    TextView my_index_item_wallet;

    //我的评论
    @ViewInject(R.id.my_index_item_review)
    TextView my_index_item_review;

    @ViewInject(R.id.my_refresh)
    Button refreshMy;

    public String TAG = "TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.my_index,null);
        ViewUtils.inject(this,view);
        // Inflate the layout for this fragment
        if(SharedUtils.getUserState(getContext()).equals("1")){
            MainActivity.ONLINE=1;
            String userName = SharedUtils.getUserName(getContext());
            Log.i("TAG", "onCreateView: "+userName);
            loginText.setText(userName+"");
            loginImage.setImageResource(R.drawable.profile_default);
        }else{
//            loginImage.setImageResource(R.drawable.pro);
        }
        if(MainActivity.ONLINE==1){
            showAble(true);
        }else{
            showAble(false);
        }

        refreshMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"点击了这个刷新按钮");
                if(MainActivity.ONLINE==1){
                    showAble(true);
                }else{
                    Toast.makeText(getContext(),"请先登陆",Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }
    /*
    刷新一下三个数据是否可见
     */
    public void showAble(boolean flag){
        showOrdersCount(flag);//分别统计的数字
        showReviewConut(flag);//分别统计的数字
        showWalletCount(flag);//分别统计的数字
    }

    @OnClick({R.id.my_index_login_image,R.id.my_index_login_text,R.id.my_index_item_order,R.id.my_index_item_wallet,R.id.my_index_item_review})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.my_index_login_image://登陆图
            case R.id.my_index_login_text://登陆编辑框
            case R.id.my_index_nav_dianping://我的信息
                if(MainActivity.ONLINE==0){
                    login();//登录
                }else{
                    showAddress();//查看个人信息
                }

                break;
                //显示订单
            case R.id.my_index_item_order:
                if(MainActivity.ONLINE==1){
                    goToOrders();
//                    showOrdersCount(true);
                }else {
                    Toast.makeText(getContext(),"请先登陆",Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.my_index_item_wallet://消费总额
                if(MainActivity.ONLINE==1){
//                    Toast.makeText(getContext(),"我刷新了金额",Toast.LENGTH_SHORT).show();
//                    showWalletCount(true);//发送信息
                }else{
                    Toast.makeText(getContext(),"请先登陆",Toast.LENGTH_SHORT).show();
                }
//
               break;

            case R.id.my_index_item_review://我的评论详细
                if(MainActivity.ONLINE==1){
//                    Toast.makeText(getContext(),"我刷新了评论",Toast.LENGTH_SHORT).show();
                    goToReview();
                }else{

                    Toast.makeText(getContext(),"请先登陆",Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                break;
        }
    }
    /*
    展示订单详细信息
     */
    private void goToOrders() {
        Intent intent = new Intent(getContext(),MyOrdersActivity.class);
        startActivity(intent);
    }
    /*
    展示评论详细信息
     */
    private void goToReview() {
        Intent intent = new Intent(getContext(),MyReviewActivity.class);
        startActivity(intent);
    }


    //展示评论数
    private void showReviewConut(boolean flag) {
        if(flag) {
            String uName = loginText.getText().toString();
            RequestParams params = new RequestParams();
            params.addQueryStringParameter("username", uName);
            new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_ReView_Count_URI, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    //服务器端返回了一段JSON串
                    ResponseObject<String> object = new Gson().fromJson(responseInfo.result, new TypeToken<ResponseObject<String>>() {
                    }.getType());
                    //拿到对象后,判断是否为空
                    if (object.getState() == 1) {
                        String CommentCount = object.getDatas();
                        my_index_item_review.setText("我的点评：" + CommentCount);
                        Log.i("TAG", "成功获得消费笔数显示"+CommentCount);
                    } else {
                        my_index_item_review.setText("我的点评");
                        Log.i("TAG", "未能获得点评显示");
                    }
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "不能连接消费信息:" + msg, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            my_index_item_review.setText("我的点评");
        }
    }

    //展示消费金额
    private void showWalletCount(boolean flag) {
        if(flag){
            String uName = loginText.getText().toString();
            //当前显示即可
            RequestParams params = new RequestParams();
            params.addQueryStringParameter("username",uName);
            new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_Wallet_Count_URI, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    //服务器端返回了一段JSON串
                    ResponseObject<String> object = new Gson().fromJson(responseInfo.result,new TypeToken<ResponseObject<String>>(){}.getType());
                    //拿到对象后,判断是否为空
                    if(object.getState()==1){
                        String money = object.getDatas();
                        my_index_item_wallet.setText("我的消费："+money);
                        Log.i("TAG","成功获得消费显示"+money);
                    }else{
                        my_index_item_wallet.setText("我的消费");
                        Log.i("TAG","未能获得消费显示");
                    }
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    error.printStackTrace();
                    Toast.makeText(getContext(),"不能连接消费信息:"+msg,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            my_index_item_wallet.setText("我的消费");
        }

    }

    //展示订单信息量
    private void showOrdersCount(boolean flag) {
        if(flag){
            String uName = loginText.getText().toString();
            RequestParams params = new RequestParams();
            params.addQueryStringParameter("username",uName);
            new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_Order_Count_URI, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    //服务器端返回了一段JSON串
                    ResponseObject<String> object = new Gson().fromJson(responseInfo.result,new TypeToken<ResponseObject<String>>(){}.getType());
                    //拿到对象后,判断是否为空
                    if(object.getState()==1){
                        String order = object.getDatas();
                        my_index_item_order.setText("我的订单："+order);
                        Log.i("TAG","成功获得订单显示"+order);
                    }else{
                        my_index_item_order.setText("我的订单");
                        Log.i("TAG","未能获得订单显示");
                    }
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    error.printStackTrace();
                    Toast.makeText(getContext(),"不能连接消费信息:"+msg,Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            my_index_item_order.setText("我的订单");
        }

    }

    //查看个人信息，需要先进行登陆验证
    private void showAddress() {
        String uName = loginText.getText().toString();
        Intent intent = new Intent(getActivity(), MyAdressActivity.class);
        intent.putExtra("userName",uName);
        startActivityForResult(intent,MyUtils.RequestLogoutCode);
    }

    /*
    登陆操作
     */
    private void login()
    {
        Intent intent = new Intent(getActivity(), MyloginActivity.class);
        startActivityForResult(intent, MyUtils.RequestLoginCode);
    }


    /*
    处理返回值/
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==MyUtils.RequestLoginCode&&resultCode==MyUtils.RequestLoginCode)
        {
            MainActivity.ONLINE=1;
            loginText.setText(data.getStringExtra("loginName"));
            loginImage.setImageResource(R.drawable.profile_default);
            showAble(true);
        }
        if(requestCode==MyUtils.RequestLogoutCode&&resultCode==MyUtils.RequestLogoutCode){
            MainActivity.ONLINE=0;
            loginText.setText("");
            loginText.setHint("点击登录");
            loginText.setTextColor(Color.GRAY);
            loginImage.setImageResource(R.drawable.portrait_def);
            showAble(false);
        }
    }
}
