package cn.edu.nuc.dianping_client;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

import cn.edu.nuc.dianping_client.consts.CONSTS;
import cn.edu.nuc.dianping_client.dialogs.PayPasswordDialog;
import cn.edu.nuc.dianping_client.entity.Goods;
import cn.edu.nuc.dianping_client.entity.Order;
import cn.edu.nuc.dianping_client.entity.ResponseObject;
import cn.edu.nuc.dianping_client.entity.Shop;
import cn.edu.nuc.dianping_client.fragment.FragmentMy;
import cn.edu.nuc.dianping_client.utils.SharedUtils;

/*
单一商品的详细信息
 */
public class GoodsDetailActivity extends AppCompatActivity {


    @ViewInject(R.id.goods_image)
    private ImageView goods_image;

    @ViewInject(R.id.goods_title)
    private TextView goods_title;

    @ViewInject(R.id.goods_desc)
    private TextView goods_desc;

    @ViewInject(R.id.shop_title)
    private TextView shop_title;

    @ViewInject(R.id.shop_phone)
    private TextView shop_phone;

    @ViewInject(R.id.goods_price)
    private TextView goods_price;

    @ViewInject(R.id.goods_old_price)
    private TextView goods_old_price;

    @ViewInject(R.id.tv_more_details_web_view)
    private WebView tv_more_details_web_view;

    @ViewInject(R.id.wv_gn_warm_prompt)
    private WebView wv_gn_warm_prompt;

    @ViewInject(R.id.btn_buy_now)
    private Button buyBtn;

    private Goods goods;


    @ViewInject(R.id.tv_plts)
    EditText commentEdt;

    @ViewInject(R.id.commit_btn)
    Button commitBtn;

    @ViewInject(R.id.last_comment)
    TextView lastComment;

    public static String goodsId;
    public static String price;
    public static String commentInfomation;
    public Integer flag ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuan_goods_detail);
        ViewUtils.inject(this);
        //老价格添加中划线效果
        goods_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        //网页自适应屏幕
        WebSettings webSettings = tv_more_details_web_view.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        WebSettings webSettings1 = wv_gn_warm_prompt.getSettings();
        webSettings1.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //获取对象
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            goods = (Goods) bundle.get("goods");

        }
        if(goods!=null){//更新每样商品界面上所有内容
            updateTitleImage();
            updateGoodsInfo();
            updateShopInfo();
            updateMoreDetails();
            goodsId=goods.getId();
        }
        //显示最新评论
        getComment();

        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.ONLINE==1){
                    String comment=commentEdt.getText().toString().trim();
                    if(comment.length()<1){
                        Toast.makeText(getApplicationContext(),"输入评论不可为空",Toast.LENGTH_SHORT).show();
                    }else{
                        //提交到数据库中
                        sendCommentMsg(comment);
                        finish();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    /*
    得到该商品的最新评论
     */
    private void getComment() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("productId",goods.getId());
//        Toast.makeText(getApplicationContext(),goods.getId(),Toast.LENGTH_SHORT).show();
        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_CommentGet_URI,params,new RequestCallBack<String>(){

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
//                Log.i("TAG",CONSTS.USER_CommentGet_URI);
                ResponseObject<String> object =new Gson().fromJson(responseInfo.result,new TypeToken<ResponseObject<String>>(){}.getType());
                if(object.getState()==1){//收到的包信息为""也行
                    lastComment.setText(object.getDatas());
                    lastComment.setVisibility(View.VISIBLE);
//                    Toast.makeText(getApplicationContext(),"评论成功",Toast.LENGTH_SHORT).show();
                }else{
                    lastComment.setText("暂无评论");
                    Log.i("TAG","没有评论");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Log.i("TAG",CONSTS.USER_CommentGet_URI+":失败");
                Toast.makeText(getApplicationContext(),"未能得到评论信息",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //电话图标点击跳转到拨号界面,返回键
    @OnClick({R.id.shop_call,R.id.goods_detail_goback,R.id.btn_buy_now})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.shop_call:
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+goods.getShop().getTel()));
                startActivity(intent);
                break;
            case R.id.goods_detail_goback:
                finish();
                break;
            case R.id.btn_buy_now:
                if(MainActivity.ONLINE==1){
                    final PayPasswordDialog dialog=new PayPasswordDialog(GoodsDetailActivity.this,R.style.mydialog);
                    dialog.setDialogClick(new PayPasswordDialog.DialogClick() {
                        @Override
                        public void doConfirm(String password) {
                            dialog.dismiss();
//                        Toast.makeText(GoodsDetailActivity.this,password, Toast.LENGTH_LONG).show();
                            sendPayMsg(password);//发送支付信息,判断对错

                        }

                    });
                    dialog.show();
                }else {
                    Toast.makeText(getApplicationContext(),"您未登陆，请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
//            case R.id.commit_btn://评论提交
//                String comment=commentEdt.getText().toString().trim();
//                if(comment.length()<1){
//                    Toast.makeText(getApplicationContext(),"输入评论不可为空",Toast.LENGTH_SHORT).show();
//                }else{
//                    //提交到数据库中
//                    sendCommentMsg(comment);
//                    finish();
//                }
//                break;
            default:
                break;
        }
    }

    /*
    发送提交评论信息
     */
    private void sendCommentMsg(String commentInfo) {
        String uname = SharedUtils.getUserName(getApplicationContext());
        String comment = commentInfo;
        String goodId=goods.getId();
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("username",uname);
        params.addQueryStringParameter("goodId",goodId);
        params.addQueryStringParameter("comment",comment);
        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_CommentPut_URI,params,new RequestCallBack<String>(){

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                ResponseObject<String> object =new Gson().fromJson(responseInfo.result,new TypeToken<ResponseObject<String>>(){}.getType());
                if(object.getState()==1){//收到的包信息为""也行
                    Log.i("TAG", "onSuccess: 评论成功");
                    Toast.makeText(getApplicationContext(),"评论成功",Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("TAG","评论失败");
                    Toast.makeText(getApplicationContext(),"评论失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(getApplicationContext(),"未能发送成功评论信息",Toast.LENGTH_SHORT).show();
            }
        });
        //评论之后需要关闭
//        finish();
    }

    /*
    发送支付信息（密码匹配）
     */
    public void sendPayMsg(String paypwd){
        RequestParams params = new RequestParams();
        String uname = SharedUtils.getUserName(getApplicationContext());
        params.addQueryStringParameter("username",uname+"");
        params.addQueryStringParameter("payPwd",paypwd+"");
        params.addQueryStringParameter("goodsId",goods.getId());
        params.addQueryStringParameter("price",goods.getPrice());
        //Xutils使用封装好的方法进行使用

        Log.i("TAG", CONSTS.USER_Pay_URI);
        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_Pay_URI, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                ResponseObject<Order> object =new Gson().fromJson(responseInfo.result,new TypeToken<ResponseObject<Order>>(){}.getType());
                //拿到对象后,判断是否为空
                if(object.getState()==1){
                    Toast.makeText(getApplicationContext(),"购买成功",Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("TAG","购买失败");
                    Toast.makeText(getApplicationContext(),"购买失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(getApplicationContext(),"请求发送失败",Toast.LENGTH_SHORT).show();
            }

        });

    }


    //更新商品图片
    private void updateTitleImage() {
        Picasso.with(this).load(goods.getImgUrl())
                .placeholder(R.drawable.ic_empty_dish).into(goods_image);
    }

    //更新商品信息、原价、现价、标题信息、标题栏
    private void updateGoodsInfo() {
        goods_title.setText(goods.getSortTitle());
        goods_desc.setText(goods.getTip());//标题栏
        goods_price.setText("￥"+goods.getPrice());//价格
        goods_old_price.setText("￥"+goods.getValue());//价值
    }

    //店面信息+还有点击拨号
    private void updateShopInfo() {
        Shop shop = goods.getShop();
        shop_title.setText(shop.getName());
        shop_phone.setText(shop.getTel());
    }

    //解析html字符串，分别获取，本单详情、温馨提示、精品展示
    private void updateMoreDetails() {
        String data[]=htmlSub(goods.getDetail());
        tv_more_details_web_view.loadDataWithBaseURL("",data[1],"text/html","utf-8","");
        wv_gn_warm_prompt.loadDataWithBaseURL("", data[0], "text/html", "utf-8", "");
    }

    //解析html的数据详细函数
    public String[] htmlSub(String html){
        char[] str = html.toCharArray();
        int len = str.length;
        Log.i("TAG","长度是："+len);
        int n=0;
        String[] data = new String[3];
        int oneIndex = 0;
        int secIndex = 1;
        int thiIndex = 2;
        for (int i = 0; i < len; i++) {
            if(str[i] =='【'){
                n++;
                if(n==1)oneIndex=i;
                if(n==2)secIndex=i;
                if(n==3)thiIndex=i;
            }
        }
        if(oneIndex>0 && secIndex>1 && thiIndex>2){
            data[0]=html.substring(oneIndex,secIndex);
            data[1]=html.substring(secIndex,thiIndex);
            data[2]=html.substring(oneIndex,html.length()-6);//</div>占用6个字符
        }

        return data;
    }

}
