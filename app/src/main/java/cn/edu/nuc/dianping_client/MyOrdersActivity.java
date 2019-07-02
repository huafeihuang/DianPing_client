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
import cn.edu.nuc.dianping_client.entity.Order;
import cn.edu.nuc.dianping_client.entity.ResponseObject;
import cn.edu.nuc.dianping_client.utils.MyUtils;
import cn.edu.nuc.dianping_client.utils.SharedUtils;

/*
我的所有订单
 */

public class MyOrdersActivity extends AppCompatActivity {


    public static String userId;
    @ViewInject(R.id.order_list)
    private ListView listDatas ;
    public List<Order> Orderlist = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_orders);
        ViewUtils.inject(this);
        userId=SharedUtils.getUserID(getApplicationContext());
//        getOrdersList();
//        if(list==null){
//
//        }else{
//            for(Order o :list){
//                Toast.makeText(this,o.getGoods_name(),Toast.LENGTH_SHORT).show();
//            }
//        }
        getOrdersList();

//        showorders();




    }

    @OnClick(R.id.order_back)
    public void onClick(View view){
        switch (view.getId()){
            case R.id.order_back:
                finish();
        }
    }

    /*
    显示所有订单
     */
    private void showorders(){
//        getOrdersList();
        Log.i("TAG", "showorders: "+(Orderlist==null));
        MyAdapter adapter = new MyAdapter(Orderlist);
        Log.i("TAG","Orderlist:"+(Orderlist==null)+" adapter:"+(adapter==null)+" listDatas:"+(listDatas==null));
        listDatas.setAdapter(adapter);

    }

    /*
    得到订单信息
     */
    public void getOrdersList() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("userId", userId);
        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.USER_Order_URI,params,new RequestCallBack<String>(){

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                ResponseObject<List<Order>> object =new Gson().fromJson(responseInfo.result,new TypeToken<ResponseObject<List<Order>>>(){}.getType());
                if(object.getState()==1){//收到的包信息为""也行
                    Orderlist = object.getDatas();
                    Log.i("TAG","我收到订单信息"+Orderlist.size());
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

    //适配器和设置
    public class MyAdapter extends BaseAdapter {

        List<Order> listOrderDatas;
        public MyAdapter(List<Order> listOrderDatas) {
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_list_item,null);
                ViewUtils.inject(holder,convertView);

                convertView.setTag(holder);
            }else{
                holder=(Holder)convertView.getTag();

            }
            //数据显示处理
//            Log.i("TAG","开始处理数据");
            Order order=listOrderDatas.get(position);
            String price = order.getOrders_all_price();
            String name = order.getGoods_name();
            String time = order.getOrders_time();
            holder.buyName.setText(name);
            holder.buyPrice.setText(price);
            holder.buyTime.setText(MyUtils.stampToDate(time));
            return convertView;
        }
    }

    public class Holder{
        @ViewInject(R.id.buy_price)
        public TextView buyPrice;
        @ViewInject(R.id.buy_name)
        public TextView buyName;
        @ViewInject(R.id.buy_time)
        public TextView buyTime;
    }
}
