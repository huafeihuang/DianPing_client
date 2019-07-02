package cn.edu.nuc.dianping_client.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.edu.nuc.dianping_client.CityActivity;
import cn.edu.nuc.dianping_client.GoodsDetailActivity;
import cn.edu.nuc.dianping_client.MainActivity;
import cn.edu.nuc.dianping_client.R;
import cn.edu.nuc.dianping_client.consts.CONSTS;
import cn.edu.nuc.dianping_client.entity.Goods;
import cn.edu.nuc.dianping_client.entity.ResponseObject;
import cn.edu.nuc.dianping_client.utils.MyUtils;
import cn.edu.nuc.dianping_client.utils.SharedUtils;


//public class FragmentHome extends Fragment implements LocationListener, AdapterView.OnItemClickListener {
public class FragmentHome extends Fragment implements  AdapterView.OnItemClickListener {
    static String TAG = "TAG";

//    @ViewInject(R.id.index_top_city)
//    private TextView topCity;

    private String cityName;//当前城市
    private LocationManager locationManager;//地理位置管理

    @ViewInject(R.id.home_nav_sort)
    private GridView navSort;


    @ViewInject(R.id.recommend_pager)
    ViewPager viewPager;

    private List<Integer> list;

    private long downTime;
    private int downX;
    ImageView iv;

    private List<Goods> listDatas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_index,null);
        ViewUtils.inject(this,view);

        initRecommendPager();

        //获取数据并且显示
//        topCity.setText(SharedUtils.getCityName(getActivity()));
        //开始gridView显示

        navSort.setAdapter(new NavAdapter());

        //开始设置点击事件
        navSort.setOnItemClickListener(this);

        //随机显示
        int page=new Random().nextInt(20);
        int size=5;

        RequestParams params = new RequestParams();
        params.addQueryStringParameter("page",page+"");
        params.addQueryStringParameter("size",size+"");
//                    params.addQueryStringParameter("raidus",radius);
        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.Goods_Data_URI, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //服务器端返回了一段JSON串
                ResponseObject<List<Goods>> object = new Gson().fromJson(responseInfo.result,new TypeToken<ResponseObject<List<Goods>>>(){}.getType());
                Log.i("TAG",object.getSize()+""+object.getState()+CONSTS.Goods_NearBy_URI);
                //拿到对象后,判断是否为空
                if(object.getState()==1){
                    listDatas = object.getDatas();
                    //标记到地图上
                    Log.i("TAG","成功获得首页显示");
                }else{
                    Log.i("TAG","未能获得首页显示");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                error.printStackTrace();
                Toast.makeText(getContext(),"不能连接本商品:"+msg,Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /*
    初始化每一个推荐界面View-顶部的滑动界面
     */
    private void initRecommendPager() {
        viewPager.setPageMargin(80);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageTransformer(false, new ScaleTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {//监听器
            int currentPosition;

            @Override
            public void onPageScrolled(int i, float v, int i1) {////正在滑动

            }

            @Override
            public void onPageSelected(int position) {//选中了那个pager 也表示当前显示那个pager
                currentPosition = position;
//                Toast.makeText(getContext(),"点击了么",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {//page的状态发生改变
                // ViewPager.SCROLL_STATE_IDLE 标识的状态是当前页面完全展现，并且没有动画正在进行中，如果不
                // 是此状态下执行 setCurrentItem 方法回在首位替换的时候会出现跳动！
                if (state != ViewPager.SCROLL_STATE_IDLE) return;

                // 当视图在第一个时，将页面号设置为图片的最后一张。
                if (currentPosition == 0) {
                    viewPager.setCurrentItem(list.size() - 2, false);

                } else if (currentPosition == list.size() - 1) {
                    // 当视图在最后一个是,将页面号设置为图片的第一张。
                    viewPager.setCurrentItem(1, false);
                }

            }
        });
        list = new ArrayList<>();
        list.add(R.drawable.page01);
        list.add(R.drawable.page02);
        list.add(R.drawable.page03);
        list.add(R.drawable.page04);
        list.add(R.drawable.page01);
        MyVpAdater adater = new MyVpAdater(getContext(), list);
        viewPager.setAdapter(adater);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }

    }

    /*
    选择格子里的选项，Fragment
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(getActivity(),"你点击了"+position+":"+id,Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onItemClick: position:"+position+":"+id);
//        if(position!=MyUtils.navsSort.length-1){
            MainActivity.categoryPosition=position+1;
            MainActivity mainActivity=(MainActivity)getActivity();
            if(mainActivity!=null){
                MainActivity.fragmentPosition=1;
                mainActivity.changeFragment(new FragmentTuan(),true);
            }

    }




    public class NavAdapter extends BaseAdapter{
        public NavAdapter(){

        }
        @Override
        public int getCount() {
            return MyUtils.navsSort.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        //item对应的view数据渲染
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //convertView为null的时候将布局转换为View
            MyHolder myHolder=null;
            if(convertView==null){
                myHolder=new MyHolder();
                //home_index_nav_item是一个单个图标的Layout
                convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.home_index_nav_item,parent,false);
                ViewUtils.inject(myHolder,convertView);
                convertView.setTag(myHolder);//打标签
            }else {
                myHolder = (MyHolder)convertView.getTag();
            }
            //赋值
            myHolder.textView.setText(MyUtils.navsSort[position]);
            myHolder.imageView.setImageResource(MyUtils.navsSortImages[position]);
            //选中的如果是全部，则需要跳转
//            if (position==MyUtils.navsSort.length-1){
//                myHolder.imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startActivity(new Intent(getActivity(), AllCategoryActivity.class));
//                    }
//                });
//            }

            return convertView;
        }
    }

    //创建MyHolder
    public class MyHolder{
        @ViewInject(R.id.home_nav_item_desc)
        public TextView textView;
        @ViewInject(R.id.home_nav_item_image)
        public ImageView imageView;
    }


//    @OnClick(R.id.index_top_city)
//    public void onClick(View view){
//        switch (view.getId()){
//            case R.id.index_top_city://城市
//                //带返回值跳转
//
//                startActivityForResult(new Intent(getActivity(), CityActivity.class), MyUtils.RequestCityCode);
//                break;
//            default:
//                break;
//        }
//    }


    //处理返回值

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode==MyUtils.RequestCityCode && resultCode== Activity.RESULT_OK){
//            cityName=data.getStringExtra("cityName");
////            topCity.setText(cityName);
//        }else{
//            Log.i("TAG","没有获取到有效数据：requestCode："+requestCode+"resultCode:"+resultCode);
//        }
//
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        //检查当前GPS模块是否打开
//        checkGPSIsOpen();
//    }
//
//    //检查是否打开GPS
//    private void checkGPSIsOpen() {
//        //获取当前Location对象
//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        boolean isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if (!isOpen) {
//            //打开设置页面
//            Intent intent = new Intent();
//            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivityForResult(intent, 0);
//        }
//        //开始定位
//        startLocation();
//    }
//
//    //使用GPS定位的方法
//
//    private void startLocation() {
//
//        //获取权限
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                        !=PackageManager.PERMISSION_GRANTED&&
//                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
//        }
//
//
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
//
//    }
//
//    //接收并且处理消息
//    private Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            if(msg.what==1){
//                topCity.setText(cityName);
//            }
//            return false;
//        }
//    });
//
//
//    //获取对应位置信息（经纬度）并且定位城市
//    private void updateWithNewLocation(Location location){
//        double lat = 0.0;//经度
//        double lng = 0.0;//维度
//        if(location!=null){
//            lat=location.getLatitude();
//            lng=location.getLongitude();
//            Log.i("TAG","经度是"+lat+"维度是"+lng);
//        }else{
//            cityName = "无法获取城市信息";
//        }
//
//        //通过经纬度获取地址，可能会返回多个，定义最大返回数为2，经纬度可能会有多个值
//        List<Address> list = null;
//        Geocoder ge = new Geocoder(getActivity());
//        try {
//            list = ge.getFromLocation(lat,lng,2);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if(list!=null&&list.size()>0){
//            for (int i=0;i<list.size();i++){
//                Address ad = list.get(i);
//                cityName = ad.getLocality();//获取城市
//            }
//        }
////    发送空消息
//        handler.sendEmptyMessage(1);
//    }
//
//
//    //位置信息更改的执行方法
//    @Override
//    public void onLocationChanged(Location location) {
//        //更新当前的位置信息
//        updateWithNewLocation(location);
//    }
//
//    //定位状态改变
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    //定位模式可用
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    //定位模式不可用
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }

    @Override
    public void onDestroy() {
        //取消导航，保存位置，停止定位
//        stopLocation();
        //保存当前城市信息
//        SharedUtils.putCityName(getActivity(),cityName);
        //停止定位
        super.onDestroy();
    }



    /*
    实现网页滑动效果
     */

    public class MyVpAdater extends PagerAdapter {
        private List<Integer> list;
        private Context context;

        public MyVpAdater(Context context, List<Integer> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {


            iv = new ImageView(context);
            iv.setImageResource(list.get(position));
            container.addView(iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
                    //发送数据，获得goods
                    Goods goods = listDatas.get(position);
                    if(goods!=null){
                        Intent intent = new Intent(getContext(),GoodsDetailActivity.class);
                        intent.putExtra("goods",goods);
                        startActivity(intent);
                    }else {
                        Toast.makeText(getContext(),"没有多的数据:",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /*
    实现滑动半隐藏效果
     */


    public class ScaleTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.70f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(View page, float position) {
            if (position < -1 || position > 1) {
                page.setAlpha(MIN_ALPHA);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            } else if (position <= 1) { // [-1,1]
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                if (position < 0) {
                    float scaleX = 1 + 0.3f * position;
                    Log.d("google_lenve_fb", "transformPage: scaleX:" + scaleX);
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                } else {
                    float scaleX = 1 - 0.3f * position;
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                }
                page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            }
        }
    }



}
