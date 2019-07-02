package cn.edu.nuc.dianping_client;

import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import cn.edu.nuc.dianping_client.fragment.FragmentHome;
import cn.edu.nuc.dianping_client.fragment.FragmentMy;
import cn.edu.nuc.dianping_client.fragment.FragmentSearch;
import cn.edu.nuc.dianping_client.fragment.FragmentTuan;
import cn.edu.nuc.dianping_client.utils.MyUtils;
import cn.edu.nuc.dianping_client.utils.SharedUtils;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
//    @ViewInject(R.id.btn1)
//    Button btn;
    @ViewInject(R.id.main_bottom_tabs)
    private RadioGroup radioGroup;

    @ViewInject(R.id.main_home)//home的button
    private RadioButton main_home;

    @ViewInject(R.id.main_tuan)
    private RadioButton main_tuan;

    private FragmentManager fragmentManager;//管理fragment

    //选择类别的位置
    public static int categoryPosition;//正式从1开始

    public static int ONLINE = 0;//上线状态

    //选择类别的位置0-3
    public static int fragmentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        fragmentPosition=0;
        //初始化FragmentManager
        fragmentManager = getSupportFragmentManager();
        //默认首页选中
        main_home.setChecked(true);
        radioGroup.setOnCheckedChangeListener(this);//下面就是重写了
        //切换fragment
        changeFragment(new FragmentHome(),false);

        ONLINE=Integer.parseInt(SharedUtils.getUserState(getApplicationContext()));

    }

    /*
    改变点击之后更改fragment
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId)  {
            case R.id.main_home:
                changeFragment(new FragmentHome(),true);
                break;
            case R.id.main_my:
                changeFragment(new FragmentMy(),true);
                break;
            case R.id.main_search:
                changeFragment(new FragmentSearch(),true);
                break;
            case R.id.main_tuan:
                changeFragment(new FragmentTuan(),true);
                break;
            default:
                break;
        }

    }

    //切换不同的fragment
    public void changeFragment(Fragment fragment,boolean isInit){
        //检查一下是否是需要改变颜色
        if(fragmentPosition==1&&!main_tuan.isChecked()){
            fragmentPosition=0;
            Log.i("TAG", "changeFragment: 改变图标");
            main_tuan.setChecked(true);
            main_home.setChecked(false);
        }

        //开启事物
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_content,fragment);//切换开始

        if(!isInit){
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
