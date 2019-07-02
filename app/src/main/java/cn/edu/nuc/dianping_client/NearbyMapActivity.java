package cn.edu.nuc.dianping_client;

import android.animation.TimeAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.autonavi.amap.mapcore.maploader.AMapLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.amap.api.location.AMapLocationListener;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.List;

import cn.edu.nuc.dianping_client.consts.CONSTS;
import cn.edu.nuc.dianping_client.entity.Goods;
import cn.edu.nuc.dianping_client.entity.ResponseObject;
import cn.edu.nuc.dianping_client.entity.Shop;

public class NearbyMapActivity extends AppCompatActivity implements LocationSource,
        AMapLocationListener , AMap.OnMarkerClickListener, AMap.OnMapLoadedListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter {
/*
呈现地图模式
 */
    @ViewInject(R.id.search_mymap)
    private MapView mapView;//AndroidView的一个子类

    private AMap aMap;//地图显示
    private Marker marker;
    private double longitude=119.95909;//经度
    private double latitude=30.05343;//维度

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);

    private static final double BJlat=116.3980865479;
    private static final double BJlon=39.9034155951;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private MyLocationStyle myLocationStyle;

    private List<Goods> listDatas;//商品信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("TAG","我创建了一次----------------------------------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_map_act);
        ViewUtils.inject(this);
        mapView.onCreate(savedInstanceState);

        if(aMap==null){
            aMap=mapView.getMap();
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            setupLocationStyle(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);

            aMap.setOnMapLoadedListener(this);//加载成功事件监听
            aMap.setOnMarkerClickListener(this);//点击marker时间监听器

            aMap.setInfoWindowAdapter(this);//设置自定义的InfoWindow样式
            aMap.setOnInfoWindowClickListener(this);//设置点击InfoWindow的事件监听器
        }

        initLocation();
        loadData(String.valueOf(latitude),String.valueOf(longitude),"5000");

    }

    private void initLocation(){
        //初始化定位
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位回调监听
        mlocationClient.setLocationListener(this);
        //设置只定位一次
//        mLocationOption.setOnceLocation(true);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(10000);
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        mlocationClient.startLocation();//启动定位
    }


    /**
     * 设置自定义定位蓝点
     */
    private void setupLocationStyle(int style){
        // 自定义系统定位蓝点
        myLocationStyle = new MyLocationStyle();
        //不跟随移动
        myLocationStyle.myLocationType(style);
        // 自定义定位蓝点图标
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
//                fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
        //连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。

    }


    @OnClick({R.id.search_back,R.id.search_refresh})
    public void oncClick(View view){
        switch (view.getId()){
            case R.id.search_back:
                finish();
                break;
            case R.id.search_refresh:
                Log.i("TAG","经度："+latitude+"纬度："+longitude);
                loadData(String.valueOf(latitude),String.valueOf(longitude),"10000");//最后是m的单位
                Log.i("TAG","refresh执行完了");
                //重新开始定位
//                mlocationClient.startLocation();
                break;
            default:
                break;
        }
    }
    /*
    按照给定的经纬数据搜索当地的数据
     */
    private void loadData(String lat, final String lon, String radius){
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("lat",lat);
        params.addQueryStringParameter("lon",lon);
        params.addQueryStringParameter("raidus",radius);
        new HttpUtils().send(HttpRequest.HttpMethod.GET, CONSTS.Goods_NearBy_URI, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //服务器端返回了一段JSON串
//                Log.i("TAG",responseInfo.result);
                Gson gson = new Gson();

                ResponseObject<List<Goods>> object = gson.fromJson(responseInfo.result,new TypeToken<ResponseObject<List<Goods>>>(){}.getType());
                Log.i("TAG",object.getSize()+""+object.getState()+CONSTS.Goods_NearBy_URI);
                //拿到对象后,判断是否为空
                if(object.getState()==1){
                    listDatas = object.getDatas();
                    aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latitude,longitude),10,0,30)));

                    //标记到地图上
                    addMarker(listDatas);
                    Log.i("TAG","addmarker");
                }else{
                    Log.i("TAG","未能获取到有效位置数据");
                }


                //设置地图缩放
                /*new LatLng(latitude,longitude) 以当前位置为中心
                  16  缩放级别（4-20级别）
                   0 方向，默认为0屏幕为正向北0-360 逆时针（正北方与手机正上方夹角为地图方向）
                    倾斜角度
                 */


            }

            @Override
            public void onFailure(HttpException error, String msg) {
                error.printStackTrace();
                Toast.makeText(NearbyMapActivity.this,"地图加载数据失败，请重新尝试:"+msg,Toast.LENGTH_LONG).show();
            }
        });


    }

    //标记到地图上
    public void addMarker(List<Goods> list){
        //声明标记对象、覆盖物，maps.model.MarkerOptions
        MarkerOptions markerOptions;

        if(list!=null){
            for (Goods goods:list){
                Shop shop = goods.getShop();
                markerOptions = new MarkerOptions();
                //设置当前的markerOptions经纬度
                markerOptions.position(new LatLng(Double.parseDouble(shop.getLat()),Double.parseDouble(shop.getLon())));
                //点击每一个图标显示信息，商铺名称，商品价钱,小二级标题
//                Log.i("TAG",shop.getName()+"-￥"+goods.getPrice());
                markerOptions.title(shop.getName()).snippet("￥"+goods.getPrice());
                //不同类型商品设置不同类型的图标
                if(goods.getCategoryId().equals(3)){
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_landmark_chi));
                }else if(goods.getCategoryId().equals(5)){
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_landmark_movie));
                }else if(goods.getCategoryId().equals(8)){
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_landmark_hotel));
                }else if(goods.getCategoryId().equals(6)){
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_landmark_life));
                }else if(goods.getCategoryId().equals(4)){
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_landmark_wan));
                }else{
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_landmark_default));
                }
                //在地图上显示所有的图标
                marker = aMap.addMarker(markerOptions);
//                marker.setObject(goods);
//                marker.setTitle("213");
//                marker.showInfoWindow();
            }
        }else{
            Toast.makeText(NearbyMapActivity.this,"list中没有数据，请重新尝试:",Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onDestroy() {
        Log.i("TAG","destroy");

        super.onDestroy();
        mapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }

    }

    @Override
    protected void onPause() {
        Log.i("TAG","onPause");

        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        Log.i("TAG","onResume");

        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.i("TAG","onResume");

        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    //locationSource
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

        Log.i("TAG", "定位开始");
        mListener = onLocationChangedListener;

        if(mlocationClient == null){
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(5000);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
        longitude=mlocationClient.getLastKnownLocation().getLongitude();
        latitude = mlocationClient.getLastKnownLocation().getLatitude();


    }


    @Override
    public void deactivate() {
        Log.i("TAG","deactivate");
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
   /*
   位置改变后的逻辑
    */
    @Override
    public void onLocationChanged(AMapLocation location) {
        Log.i("TAG","坐标改变一次，发生了定位");
        Log.i("TAG","onLocationChanged");
        Log.i("TAG","当前的经纬度是+"+longitude+","+latitude);
        if (mListener != null&&location != null) {
            if (location != null
                    &&location.getErrorCode() == 0) {
//                longitude = location.getLongitude();
//                latitude = location.getLatitude();
                mListener.onLocationChanged(location);// 显示系统小蓝点
                Log.i("TAG","当前的经纬度是+"+longitude+","+latitude);
                //加载信息
                loadData(latitude+"",longitude+"","10000");
//                loadData(BJlat+"",BJlon+"","1000");
                //取消定位请求,以免资源浪费
//                mlocationClient.stopLocation();
            } else {
                String errText = "定位失败," + location.getErrorCode()+ ": " + location.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        loadData(String.valueOf(latitude),String.valueOf(longitude),"5000");//最后是m的单位

    }

    //
    @Override
    public boolean onMarkerClick(Marker marker) {
        //不移动了
        setupLocationStyle(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        //停止定位
        mlocationClient.stopLocation();

        Log.i("TAG","显示infowindow"+marker.getTitle()+"option:"+marker.getOptions()+"position:"+marker.getPosition());
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(latitude,longitude));
//        options.title("haha");
//        marker.setTitle(marker.getTitle());
//        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.infowindow));
//        marker.setin
        marker.showInfoWindow();
        return false;
    }
    //加载地图
    @Override
    public void onMapLoaded() {

    }

    //图标点击，显示窗体进行点击，出来
    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i("TAG","infowindow的title:"+marker.getTitle());
        //获取商店信息
        String shopName = marker.getTitle();
        //获取商铺名称，找到对应物品
        Goods goods = getGoodsByShopName(shopName);
        if(goods!=null){
            //跳转到详情界面
            Intent intent = new Intent(this,GoodsDetailActivity.class);
            intent.putExtra("goods",goods);
            startActivity(intent);
        }

    }
    //根据商店名称获取1当前商品信息
    public Goods getGoodsByShopName(String shopName){
        for(Goods good:listDatas){//便利商品集合进行商铺的匹配，逆向匹配
            if(good.getShop().getName().equals(shopName)){
                return good;
            }
        }
        return null;
    }

    //InfowindowAdapter
    @Override
    public View getInfoWindow(Marker marker) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(marker.getTitle());

        textView.setTextSize(15);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setBackgroundResource(R.drawable.infowindow);// 通过View获取BitmapDescriptor对象
        textView.setPadding(40, 20, 40, 20);
        return textView;


    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
