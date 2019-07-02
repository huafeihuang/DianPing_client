package cn.edu.nuc.dianping_client.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import cn.edu.nuc.dianping_client.R;

public class MyUtils {


    //返回值
    public static final int RequestCaptureCode = 3;
    public static final int RequestCityCode = 2;
    public static final int RequestLoginCode = 1;
    public static final int RequestLogoutCode = 4;
    /**首页的分类标题*/
    public static final String[] navsSort=
            {
//                    "美食","电影","酒店","KTV","自助餐","休闲娱乐",
//                    "旅游","购物","都市丽人","母婴","女装","美妆","户外运动","生活服务","全部"
                    "全部分类", "今日新单",
                    "美食","休闲娱乐", "电影","生活服务",
                    "摄影写真","酒店","旅游",
                    "教育培训","抽奖公益","购物","丽人",
            };
    /**首页的分类图片*/
    public static final int[] navsSortImages =
            {
//                    R.drawable.icon_home_food_99,R.drawable.icon_home_movie_29,
//                    R.drawable.icon_home_hotel_300,R.drawable.icon_home_ktv_31,R.drawable.icon_home_self_189,
//                    R.drawable.icon_home_happy_2,R.drawable.icon_home_flight_400,R.drawable.icon_home_shopping_3,
//                    R.drawable.icon_home_liren_442,R.drawable.icon_home_child_13,R.drawable.icon_home_nvzhuang_84,
//                    R.drawable.icon_home_meizhuang_173,R.drawable.icon_home_yundong_20,R.drawable.icon_home_life_46,
//                    R.drawable.icon_home_all_0
                    R.drawable.ic_all,R.drawable.ic_newest,
                    R.drawable.ic_food,R.drawable.ic_entertain,R.drawable.ic_movie,R.drawable.ic_life,
                    R.drawable.ic_photo,R.drawable.ic_hotel,R.drawable.ic_travel,
                    R.drawable.ic_edu,R.drawable.ic_luck,R.drawable.ic_shopping,R.drawable.ic_beauty,
//                    R.drawable.icon_home_all_0
            };
    /**显示分类列表的分类标题*/
    public static final String[] allCategray =
            {
                    "全部分类","今日新单",
                    "美食","休闲娱乐", "电影","生活服务",
                    "摄影写真","酒店","旅游",
                    "教育培训","抽奖公益","购物","丽人"
            };
    /**显示分类列表的分类图片*/
    public static final int[] allCategrayImages =
            {
                    R.drawable.ic_all,R.drawable.ic_newest,
                    R.drawable.ic_food,R.drawable.ic_entertain,R.drawable.ic_movie,R.drawable.ic_life,
                    R.drawable.ic_photo,R.drawable.ic_hotel,R.drawable.ic_travel,
                    R.drawable.ic_edu,R.drawable.ic_luck,R.drawable.ic_shopping,R.drawable.ic_beauty
            };

    public static long allCategrayNumber[] = new long[allCategray.length+5];//定义的大一些

    private static final String RANDOMS = "1234567890poiuytrewqasdfghjklmnbvcxzQWERTYUIOPASDFGHJKLZXCVBNM";
    public static String getRandom(int num){
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int random = (int)(Math.random()*RANDOMS.length());
            sbf.append(RANDOMS.charAt(random));
        }
        return sbf.toString();
    }

    /**
     * category
     *
     */
    public static String[] category={
            "全部生活","今日新单","餐饮美食",
            "休闲娱乐","电影","生活服务",
            "摄影写真","酒店","旅游",
            "教育培训","抽奖公益","购物","丽人","食品茶酒"
    };

    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


}
