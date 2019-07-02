package cn.edu.nuc.dianping_client.entity;

public class Comment {
    String comment_id;
    String user;
    String prodouct;
    String comment_conent;
    String comment_time;
    String comment_star;
    String product_name;

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProdouct() {
        return prodouct;
    }

    public void setProdouct(String prodouct) {
        this.prodouct = prodouct;
    }

    public String getComment_conent() {
        return comment_conent;
    }

    public void setComment_conent(String comment_conent) {
        this.comment_conent = comment_conent;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public String getComment_star() {
        return comment_star;
    }

    public void setComment_star(String comment_star) {
        this.comment_star = comment_star;
    }
}
