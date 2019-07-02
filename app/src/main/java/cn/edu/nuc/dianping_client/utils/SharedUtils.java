package cn.edu.nuc.dianping_client.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import static android.content.Context.MODE_APPEND;

//写入和读取
public class SharedUtils {
    private static final String FILE_NAME = "dianping";
    private static final String MODE_NAME = "welcome";
    //获取boolean类型的值
    public static boolean getWelcomeBoolean(Context context){
        //没有的话默认返回false
        return context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).getBoolean(MODE_NAME,false);
    }

//    写入boolean
    public static void putWelcomeBoolean(Context context,boolean isFirst){
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME, MODE_APPEND).edit();
        editor.putBoolean(MODE_NAME,isFirst);
        editor.commit();
    }

    //写入一个String类型的数据
    public static void putCityName(Context context,String cityName){
        SharedPreferences.Editor editor= context.getSharedPreferences(FILE_NAME, MODE_APPEND).edit();
        editor.putString("cityName",cityName);
        editor.commit();
    }

    //读入一个String类型的数据
    public static String getCityName(Context context){
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getString("cityName","选择城市");
    }

    //写入登陆的名称
    public static void putUserName(Context context,String userName){
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME, MODE_APPEND).edit();
        editor.putString("userName",userName);
        editor.commit();
    }
    //获取登陆的名称
    public static String getUserName(Context context){
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getString("userName","");
    }

    //获取登录状态
    public static String getUserState(Context context){
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getString("userState","0");
    }
    //输入登录状态
    public static void putUserState(Context context,String State){
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME, MODE_APPEND).edit();
        editor.putString("userState",State);
        editor.commit();
    }

    //获取登录状态
    public static String getUserPwd(Context context){
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getString("password","");
    }
    //输入登录状态
    public static void putUserPwd(Context context,String Pwd){
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME, MODE_APPEND).edit();
        editor.putString("password",Pwd);
        editor.commit();
    }

    //获取用户ID
    public static String getUserID(Context context){
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getString("userId","-1");
    }
    //输入用户ID
    public static void putUserID(Context context,String ID){
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME, MODE_APPEND).edit();
        editor.putString("userId",ID);
        editor.commit();
    }
}
