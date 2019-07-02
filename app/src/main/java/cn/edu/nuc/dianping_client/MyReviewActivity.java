package cn.edu.nuc.dianping_client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

import cn.edu.nuc.dianping_client.consts.CONSTS;
import cn.edu.nuc.dianping_client.entity.Comment;
import cn.edu.nuc.dianping_client.entity.Order;
import cn.edu.nuc.dianping_client.entity.ResponseObject;
import cn.edu.nuc.dianping_client.utils.MyUtils;
import cn.edu.nuc.dianping_client.utils.SharedUtils;

/*
我的所有评论
 */

public class MyReviewActivity extends AppCompatActivity {
    public static String userId;
    @ViewInject(R.id.comment_list)
    private ListView listDatas ;
    public List<Comment> Commentlist = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_review);

        ViewUtils.inject(this);
        userId= SharedUtils.getUserID(getApplicationContext());
        getOrdersList();

    }


    private void showorders(){
//        getOrdersList();
        Log.i("TAG", "showorders: "+(Commentlist==null));
        MyAdapter adapter = new MyAdapter(Commentlist);
//        Log.i("TAG","Orderlist:"+(Commentlist==null)+" adapter:"+(adapter==null)+" listDatas:"+(listDatas==null));
        listDatas.setAdapter(adapter);

    }

    public void getOrdersList() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("userId", userId);
        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_ReView_URI,params,new RequestCallBack<String>(){

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                ResponseObject<List<Comment>> object =new Gson().fromJson(responseInfo.result,new TypeToken<ResponseObject<List<Comment>>>(){}.getType());
                if(object.getState()==1){//收到的包信息为""也行
                    Commentlist = object.getDatas();
                    Log.i("TAG","我收到订单信息"+Commentlist.size());
                    showorders();
                }else{
                    Log.i("TAG","我没有收到订单信息");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Log.i("TAG",CONSTS.USER_Order_URI+":失败");
                Toast.makeText(getApplicationContext(),"未能得到评论信息",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.comment_back)
    public void onClick(View view){
        switch (view.getId()){
            case R.id.comment_back:
                finish();
        }
    }

    //适配器
    public class MyAdapter extends BaseAdapter {

        List<Comment> listOrderDatas;
        public MyAdapter(List<Comment> listOrderDatas) {
            this.listOrderDatas = listOrderDatas;
        }

        @Override
        public int getCount() {
            Log.i("TAG", "getCount: "+(listOrderDatas==null));
            return listOrderDatas.size();
        }

        @Override
        public Object getItem(int position) {

            return listOrderDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if(convertView==null){
                holder=new Holder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_review_list_item,null);
                ViewUtils.inject(holder,convertView);

                convertView.setTag(holder);
            }else{
                holder=(Holder) convertView.getTag();

            }
            //数据显示处理
//            Log.i("TAG","开始处理数据");
            //名称，时间，评论
            Comment comment=listOrderDatas.get(position);
            String name = comment.getProduct_name();
            String time = comment.getComment_time();
            String content = comment.getComment_conent();
            holder.commentGoods.setText(name);
            holder.commentContent.setText(content);
            holder.commentTime.setText(MyUtils.stampToDate(time));
            return convertView;
        }
    }

    public class Holder{
        @ViewInject(R.id.comment_goods)
        public TextView commentGoods;
        @ViewInject(R.id.comment_content)
        public TextView commentContent;
        @ViewInject(R.id.comment_time)
        public TextView commentTime;
    }
}
