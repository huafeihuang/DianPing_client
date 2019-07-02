package cn.edu.nuc.dianping_client.entity;

public class ResponseObject<T> {
    private String msg;
    private int state;
    private T datas;
    private int page;
    private int size;
    private int count;
    public ResponseObject(){
        super();
    }

    public ResponseObject(int state, T datas){

        this.state=state;
        this.datas=datas;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public T getDatas() {
        return datas;
    }

    public void setDatas(T datas) {
        this.datas = datas;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
