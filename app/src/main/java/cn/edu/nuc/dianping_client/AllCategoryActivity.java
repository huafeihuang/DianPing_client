package cn.edu.nuc.dianping_client;

import android.content.Intent;
import android.icu.util.ULocale;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

//import cn.edu.nuc.dianping_client.fragment.FragmentHome;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

import cn.edu.nuc.dianping_client.consts.CONSTS;
import cn.edu.nuc.dianping_client.entity.Category;
import cn.edu.nuc.dianping_client.entity.ResponseObject;
import cn.edu.nuc.dianping_client.utils.MyUtils;
/*
    所有类别的统计数据，已作废
     */
public class AllCategoryActivity extends AppCompatActivity {

    @ViewInject(R.id.home_nav_all_categray)
    private ListView categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载布局
        setContentView(R.layout.home_index_nav_all);
        ViewUtils.inject(this);
        //适配，单机版
        categoryList.setAdapter(new MyAdapter());

        //使用异步操作进行处理
        new CategoryDataTask().execute();
    }


    //异步任务获取分类的个数信息
    public class CategoryDataTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet=new HttpGet(CONSTS.Category_Data_URI);
            try {
                HttpResponse response = client.execute(httpGet);
                if(response.getStatusLine().getStatusCode()==200){
                    String jsonString = EntityUtils.toString(response.getEntity());
                    //更新数据内容
                    Log.i("Tag",jsonString);
                    parseCategoryDataJson(jsonString);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        //提交操作

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //适配
            MyAdapter adapter=new MyAdapter();
            categoryList.setAdapter(adapter);
        }
    }
    //解析Json数据信息
    private void parseCategoryDataJson(String json) {
        Gson gson=new Gson();
        ResponseObject<List<Category>> responseObject = gson.fromJson(json,
                new TypeToken<ResponseObject<List<Category>>>(){}.getType());
        List<Category> datas = responseObject.getDatas();
        //遍历集合对象
        for (Category category:datas) {
            int position = Integer.parseInt(category.getCategoryId());
            //id定义成下标
            MyUtils.allCategrayNumber[position-1] =category.getCategoryNumber();
        }

    }




    /*
    适配器
     */
    public class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return MyUtils.allCategray.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //convertView为null的时候将布局转换为View
            MyHolder myHolder=null;
            if(convertView==null){
                myHolder=new MyHolder();
                //home_index_nav_item是一个单个图标的Layout
                convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.home_index_nav_all_item,parent,false);
                ViewUtils.inject(myHolder,convertView);
                convertView.setTag(myHolder);//打标签
            }else {
                myHolder = (MyHolder)convertView.getTag();
            }
            //赋值
            myHolder.textDesc.setText(MyUtils.allCategray[position]);
            myHolder.imageView.setImageResource(MyUtils.allCategrayImages[position]);
            //第三个控件
            myHolder.textNumber.setText(MyUtils.allCategrayNumber[position]+"");

            return convertView;
        }
    }

    public class MyHolder {
        @ViewInject(R.id.home_nav_all_item_desc)
        public TextView textDesc;
        @ViewInject(R.id.home_nav_all_item_image)
        public ImageView imageView;
        @ViewInject(R.id.home_nav_all_item_number)
        public TextView textNumber;
    }

    //点击的监听
    @OnClick(R.id.home_nav_all_back)
    public void onClick(View view){
        switch (view.getId()){
            case R.id.home_nav_all_back:
                finish();
                break;
            default:
                break;
        }
    }
}
