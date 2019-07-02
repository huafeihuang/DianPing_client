package cn.edu.nuc.dianping_client.consts;

public class CONSTS {

    //wlan
//    public static final String  HOST="http://192.168.4.157:8000";//真机192.168.6.123
//    public static final String  HOST="http://10.130.152.38:8000";//真机
    public static final String  HOST="http://192.168.137.1:8000";//真机192.168.4.157
//    public static final String  HOST="http://10.0.2.2:8080/dianping-server";//模拟器

    //城市数据
    public static final String  City_Data_URI = HOST +"/api/city/";
    //商品分类数据
    public static final String  Category_Data_URI = HOST +"/api/category/";

    //商品列表信息
    public static final String Goods_Data_URI = HOST + "/api/goods/";

    //周围店铺列表信息
    public static final String Goods_NearBy_URI = HOST + "/api/nearby/";

    //登陆验证URL
    public static final String USER_LOGIN = HOST + "/api/user/?flag=login";

    //注册的URL
    public static final String USER_REGISTER = HOST + "/api/user/?flag=register";

    //个人详细信息（包括地址）
    public static final String USER_Detail_URI = HOST + "/api/address/";

    //修改个人信息
    public static final String USER_Changge_Info_URI = HOST + "/api/change/";

    //支付界面
    public static final String USER_Pay_URI = HOST + "/api/pay/";

    //评论功能
    public static final String  USER_CommentPut_URI = HOST +"/api/commentPut/";

    //获取评论功能
    public static final String  USER_CommentGet_URI = HOST +"/api/commentGet/";

    //订单信息统计
    public static final String  USER_Order_Count_URI = HOST +"/api/ordersCount/";

    //订单信息
    public static final String  USER_Order_URI = HOST +"/api/show_orders/";

    //获取消费统计
    public static final String  USER_Wallet_Count_URI = HOST +"/api/walletCount/";

    //获取评论的统计
    public static final String  USER_ReView_Count_URI = HOST +"/api/reviewCount/";

    //获取评论的统计
    public static final String  USER_ReView_URI = HOST +"/api/show_review/";

}
