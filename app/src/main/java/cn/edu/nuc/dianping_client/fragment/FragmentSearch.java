package cn.edu.nuc.dianping_client.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import cn.edu.nuc.dianping_client.NearbyMapActivity;
import cn.edu.nuc.dianping_client.R;


/*
有一个检察权限的操作
为了以防万一，添加了两个按钮进行权限获取和界面跳转
 */
public class FragmentSearch extends Fragment {
    @ViewInject(R.id.open_gps)
    private Button gpsbtn;
    @ViewInject(R.id.come_toMap)
    private Button openMapBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.search_index,null);
        ViewUtils.inject(this,view);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
            startActivity(new Intent(getActivity(), NearbyMapActivity.class));
        }
        // Inflate the layout for this fragment
        return view;
    }

    @OnClick({R.id.open_gps,R.id.come_toMap})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.open_gps:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED&&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                }
                break;
            case R.id.come_toMap:
                startActivity(new Intent(getActivity(), NearbyMapActivity.class));
        }
    }

}
