package cn.edu.nuc.dianping_client.entity;

public class Order {
    String id;
    String user_id;
    String orders_prodouct_count;
    String orders_time;
    String orders_all_price;
    String orders_paystate;
    String orders_prodouct_id;
    String goods_name;

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrders_prodouct_count() {
        return orders_prodouct_count;
    }

    public void setOrders_prodouct_count(String orders_prodouct_count) {
        this.orders_prodouct_count = orders_prodouct_count;
    }

    public String getOrders_time() {
        return orders_time;
    }

    public void setOrders_time(String orders_time) {
        this.orders_time = orders_time;
    }

    public String getOrders_all_price() {
        return orders_all_price;
    }

    public void setOrders_all_price(String orders_all_price) {
        this.orders_all_price = orders_all_price;
    }

    public String getOrders_paystate() {
        return orders_paystate;
    }

    public void setOrders_paystate(String orders_paystate) {
        this.orders_paystate = orders_paystate;
    }

    public String getOrders_prodouct_id() {
        return orders_prodouct_id;
    }

    public void setOrders_prodouct_id(String orders_prodouct_id) {
        this.orders_prodouct_id = orders_prodouct_id;
    }
}
