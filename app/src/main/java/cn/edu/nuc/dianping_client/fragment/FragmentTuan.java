package cn.edu.nuc.dianping_client.fragment;


import android.app.DownloadManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;


import cn.edu.nuc.dianping_client.GoodsDetailActivity;
import cn.edu.nuc.dianping_client.MainActivity;
import cn.edu.nuc.dianping_client.R;
import cn.edu.nuc.dianping_client.consts.CONSTS;
import cn.edu.nuc.dianping_client.entity.City;
import cn.edu.nuc.dianping_client.entity.Goods;
import cn.edu.nuc.dianping_client.entity.ResponseObject;
import cn.edu.nuc.dianping_client.utils.MyUtils;

/*
搜索商品，可以点击
 */

public class FragmentTuan extends Fragment {

    @ViewInject(R.id.index_listGoods)
    private PullToRefreshListView listGoods;

    private List<Goods> listDatas;
    private MyAdapter adapter;
    @ViewInject(R.id.spinner_menu)
    private Spinner spinner;

    public static int localPosition;//选择我的类别下拉框

    @OnItemClick(R.id.index_listGoods)
    public void onItemOnclick(AdapterView<?> parent,View view , int position, long id){
        Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
        intent.putExtra("goods", listDatas.get(position-1));
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从主页选过来的
        localPosition= MainActivity.categoryPosition;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.tuan_index,null);
        // Inflate the layout for this fragment
        ViewUtils.inject(this,view);
        //显示默认全部
//        spinner.setTooltipText("");
        spinner.setSelection(localPosition-1,true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(),"你选择为："+MyUtils.category[position],Toast.LENGTH_SHORT).show();
                localPosition=position+1;
                //重新发送信息
                loadDatas(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置商品的信息列表属性
        listGoods.setMode(PullToRefreshBase.Mode.BOTH);//支持上拉和下拉
        listGoods.setScrollingWhileRefreshingEnabled(true);//滚动时不加载数据
        listGoods.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新有y<0
                loadDatas(listGoods.getScrollY()<0);//获取数据,是否加载下一页
            }
        });

        //首次来到页面自动加载数据
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                listGoods.setRefreshing();
                return false;
            }
        }).sendEmptyMessageDelayed(0,300);

        return view;
    }

    //测试初始化数据

    private int page;
    public static int size=10;
    private int count;

    /*
    载入数据
     */
    public void loadDatas(final boolean reflush){

        if(reflush){
            page=1;
        }else {
            page++;
        }
        if(size==0){
            size=10;
        }

        //请求数据
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("page",page+"");
        params.addQueryStringParameter("size",size+"");
        params.addQueryStringParameter("category",localPosition+"");
        //Xutils使用封装好的方法进行使用
        Log.i("TAG",CONSTS.Goods_Data_URI);
        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.Goods_Data_URI, params, new RequestCallBack<String>() {
            //请求失败时
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                listGoods.onRefreshComplete();//停止刷新
                Gson gson = new Gson();
                ResponseObject<List<Goods>> o = gson.fromJson(responseInfo.result, new TypeToken<ResponseObject<List<Goods>>>(){}.getType());
                //获取对象中封装的内容
                page = o.getPage();
                size = o.getSize();
                count = o.getCount();
                listDatas = o.getDatas();

                if (reflush){//下拉刷新
                        listDatas=o.getDatas();
                        adapter = new MyAdapter();
                        listGoods.setAdapter(adapter);
                }else {//加载更多
                        listDatas.addAll(o.getDatas());
                        adapter.notifyDataSetChanged();
                }

                if (count==page){//没有更多数据显示了
                    listGoods.setMode(PullToRefreshBase.Mode.PULL_FROM_START);//只能刷新
                }
            }
            //请求失败时
            @Override
            public void onFailure(HttpException error, String msg) {
                listGoods.onRefreshComplete();//停止刷新
                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }


    /*
    适配器
     */
    public class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return listDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        //渲染每一个数据
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //convertView为null的时候将布局转换为View
            MyHolder myHolder=null;
            if(convertView==null)
            {
                convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.tuan_goods_list_item,parent,false);
                myHolder=new MyHolder();
                ViewUtils.inject(myHolder,convertView);
                convertView.setTag(myHolder);
            }else{
                myHolder=(MyHolder)convertView.getTag();
            }
            //获取对应内容
            Goods goods =  listDatas.get(position);
            //获取图片信息
//            String imageUrl=goods.getImgUrl();

            //picasso框架调用图片,避免图片错位
            Picasso.with(parent.getContext()).load(goods.getImgUrl()).placeholder(R.drawable.ic_empty_dish).into(myHolder.image);


            StringBuffer sbf = new StringBuffer("￥"+goods.getValue());
            //添加中划线
            SpannableString spannable = new SpannableString(sbf);
            spannable.setSpan(new StrikethroughSpan(),0,sbf.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            myHolder.value.setText(spannable);
            myHolder.count.setText(goods.getBought()+"份");
            myHolder.price.setText("￥"+goods.getPrice());
            myHolder.title.setText(goods.getTitle());
            return convertView;
        }
    }

    /*
    Holder对每一个进行配置
     */
    public class MyHolder{
        @ViewInject(R.id.index_gl_item_image)
        public ImageView image;
        @ViewInject(R.id.index_gl_item_title)
        public TextView title;
        @ViewInject(R.id.index_gl_item_titlecontent)
        public TextView titleContent;
        @ViewInject(R.id.index_gl_item_price)
        public TextView price;
        @ViewInject(R.id.index_gl_item_value)
        public TextView value;
        @ViewInject(R.id.index_gl_item_count)
        public TextView count;
    }
}
