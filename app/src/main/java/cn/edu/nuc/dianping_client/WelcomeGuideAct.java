package cn.edu.nuc.dianping_client;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

/*
首次安装APP
有一个界面向导
 */

public class WelcomeGuideAct extends AppCompatActivity {

    @ViewInject(R.id.welcome_guide_btn)
    private Button btn;
    @ViewInject(R.id.welcome_pager)
    private ViewPager viewPager;


    private List<View> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_guide);


        ViewUtils.inject(this);
        initViewPager();

    }

    @OnClick(R.id.welcome_guide_btn)
    public void click(View view){
       // 页面跳转
        startActivity(new Intent(getBaseContext() ,MainActivity.class));
        finish();
    }

    //初始化viewpager,guide01-03，设置监听
    public void initViewPager(){
//        btn.setVisibility(View.INVISIBLE);//默认看不见

        list = new ArrayList<>();
        ImageView iv1=new ImageView(this);
        iv1.setImageResource(R.drawable.guide_01);
        list.add(iv1);

        ImageView iv2=new ImageView(this);
        iv2.setImageResource(R.drawable.guide_02);
        list.add(iv2);

        ImageView iv3=new ImageView(this);
        iv3.setImageResource(R.drawable.guide_03);
        list.add(iv3);

        viewPager.setAdapter(new MyPagerAdapter());//需要适配器
        //监听一下
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //滑动，暂时用不到
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            //选中，这个要
            @Override
            public void onPageSelected(int i) {
                if(i==2){
                    btn.setVisibility(View.VISIBLE);
                }else{
                    btn.setVisibility(View.INVISIBLE);
                }
            }
            //滑动状态改变，暂时用不到
            @Override
            public void onPageScrollStateChanged(int i) { }
        });

    }

    //定义ViewPager的适配器
    class MyPagerAdapter extends PagerAdapter{
        //计算需要多少item需要显示
        @Override
        public int getCount() {

            return list.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {

            return view==o;
        }

        //初始化item实例,添加方法
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        //item销毁方法，弹出方法
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(list.get(position));
        }
    }
}
