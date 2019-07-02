package cn.edu.nuc.dianping_client;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nuc.dianping_client.consts.CONSTS;
import cn.edu.nuc.dianping_client.entity.City;
import cn.edu.nuc.dianping_client.entity.ResponseObject;
import cn.edu.nuc.dianping_client.view.SiderBar;
/*
  显示城市列表，已作废
     */

public class CityActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SiderBar.OnTouchingListenerChangedListener {


    @ViewInject(R.id.city_list)
    private ListView listDatas;

    private List<City> cityList;

    @ViewInject(R.id.index_city_back)
    private TextView tvCityBack;

    @ViewInject(R.id.city_side_bar)
    private SiderBar siderBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_city_list);
        ViewUtils.inject(this);

        View view = LayoutInflater.from(this).inflate(R.layout.home_city_search,null);
        Log.i("TAG",":开始干活填充数据");
        listDatas.addHeaderView(view);

        //执行异步任务
        new CityDataTask().execute();//执行一次异步任务
        Log.i("TAG",":开始干活异步填充数据");

        //自定义SiderBar
        siderBar.setOnTouchingListenerChangedListener(this);
    }
    //设置Onclick监听
    @OnClick({R.id.index_city_back,R.id.index_city_flushcity})
    public void Onclick(View view){
        switch (view.getId()){
            case R.id.index_city_back://返回
                finish();
                break;
            case R.id.index_city_flushcity://刷新
                Log.i("TAG","开始刷新异步任务");
                new CityDataTask().execute();
                break;

            default:
                break;
        }
    }

    //选中item后回传给HomeFragment,穿回到OnActivityResult方法中
    @OnItemClick(R.id.city_list)
    public void onItemClick(AdapterView<?> parent, View view,int position, long id){

        Intent intent = new Intent();
        TextView textView = (TextView) view.findViewById(R.id.city_list_item_name);
        intent.putExtra("cityName",textView.getText().toString());
        setResult(RESULT_OK,intent);
        finish();

    }




    //执行异步任务获取json串
    public class CityDataTask extends AsyncTask<Void,Void,List<City>>{

        @Override
        protected List<City> doInBackground(Void... voids) {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CONSTS.City_Data_URI);
//            HttpPost httpPost=new HttpPost(CONSTS.City_Data_URI);
            Log.i("TAG",httpGet.toString());
            try {
                HttpResponse httpResponse=client.execute(httpGet);
                if(httpResponse.getStatusLine().getStatusCode()==200){
                    String jsonString = EntityUtils.toString(httpResponse.getEntity());
                    Log.i("TAG","得到正确信息"+jsonString);
                    return parseCityDataJson(jsonString);
                }else{
                    Log.i("TAG","没有得到正确信息");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<City> cities) {
            super.onPostExecute(cities);
            //cityList赋值
            cityList = cities;
            Log.i("TAG","是否为空"+cityList.size());
            //适配显示
            MyAdapter adapter = new MyAdapter(cityList);
            listDatas.setAdapter(adapter);
        }
    }

    //解析城市数据的json
    private List<City> parseCityDataJson(String json) {//doInBackGround
        Gson gson = new Gson();
        ResponseObject<List<City>> responseObject = gson.fromJson(json,new TypeToken<ResponseObject<List<City>>>(){}.getType());
        return responseObject.getDatas();
    }

    //用来保存第一次保存首字母的索引
    private StringBuffer buffer=new StringBuffer();//

    //保存索引对象的城市名称
    private List<String> firstList= new ArrayList<String>();

    //适配器
    public class MyAdapter extends BaseAdapter{

        List<City> listCityDatas;
        public MyAdapter(List<City> listCityDatas) {
            this.listCityDatas = listCityDatas;
        }

        @Override
        public int getCount() {

            return listCityDatas.size();
        }

        @Override
        public Object getItem(int position) {

            return listCityDatas.get(position);
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_city_list_item,null);

                ViewUtils.inject(holder,convertView);
                convertView.setTag(holder);
            }else{
                holder=(Holder)convertView.getTag();

            }
            //数据显示处理
            Log.i("TAG","开始处理数据");
            City city=listCityDatas.get(position);
            String sort = city.getSortKey();
            String name = city.getName();
            if(buffer.indexOf(sort)==-1){
                buffer.append(sort);
                firstList.add(name);
            }
            if(firstList.contains(name)){
                holder.keySort.setText(sort);
                holder.keySort.setVisibility(View.VISIBLE);//可见
            }else{
                holder.keySort.setVisibility(View.GONE);//不可见
            }

            holder.cityName.setText(name);
            return convertView;
        }
    }

    public class Holder{
        @ViewInject(R.id.city_list_item_sort)
        public TextView keySort;
        @ViewInject(R.id.city_list_item_name)
        public TextView cityName;

    }

    //siderbar实现监听方法
    @Override
    public void onTouchingListenerChanged(String s) {
        //找到ListView中显示的索引位置
        listDatas.setSelection(findIndex(cityList,s));//根据下面实现的方法
    }

    //  根据s索引的位置
    public int findIndex(List<City> list,String s){
        if(list!=null){
            for (int i = 0; i < list.size(); i++) {
                City city=list.get(i);
                //根据sortKey进行比较
                if (s.equals(city.getSortKey())){
                    return i;
                }
            }
        }else{
            Toast.makeText(this,"暂无信息",Toast.LENGTH_SHORT).show();
        }
        return -1;
    }

}
