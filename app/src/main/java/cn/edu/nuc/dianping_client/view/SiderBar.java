package cn.edu.nuc.dianping_client.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.edu.nuc.dianping_client.R;


//依然是显示在home_city_List中
//绘制对应的英文字母
public class SiderBar extends View {
    //new 对象时使用
    public SiderBar(Context context) {
        super(context);
    }
    //XML文件创建控件成对象
    public SiderBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //26个字字母
    public static String[] sideBar={ "热门","A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"
    };

    //监听器
    private OnTouchingListenerChangedListener listenerChangedListener;

    private int choose;
    private Paint paint= new Paint();

    //写入
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.GRAY);//画笔颜色
        paint.setTypeface(Typeface.DEFAULT_BOLD);//粗体
        paint.setTextSize(30);
        //自定义宽和高
        int height = getHeight();
        int width = getWidth();
        //设定每一个字母所在控件高度
        int each_height = height/sideBar.length;
        //画出每一个字母
        for (int i = 0; i < sideBar.length; i++) {
            float x=width/2-paint.measureText(sideBar[i])/2;
            float y=(i+1)*each_height;
            canvas.drawText(sideBar[i],x,y,paint);
        }
    }
    //根据滑动位置的索引作出处理
    public interface OnTouchingListenerChangedListener{
        public void onTouchingListenerChanged(String s);


    }
    public void setOnTouchingListenerChangedListener(OnTouchingListenerChangedListener onTouchingListenerChangedListener){
        this.listenerChangedListener=onTouchingListenerChangedListener;
    }

    //重写分发对应的touch监听
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();//获取对应动作
        final float y=event.getY();
        final OnTouchingListenerChangedListener listener = listenerChangedListener;
        //获取对应的字母索引i
        final int c = (int)(y/getHeight()*sideBar.length);//获取点击y的坐标，字母索引
        switch (action){
            case MotionEvent.ACTION_UP:
                setBackgroundResource(android.R.color.transparent);
                invalidate();
                break;

            default:
                setBackgroundResource(R.drawable.sidebar_background);//这里面定义了角度和颜色的引脚坐标
                if (c>0&&c<sideBar.length){
                    if (listener!=null){
                        listener.onTouchingListenerChanged(sideBar[c]);
                    }
                    choose = c;
                    invalidate();
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
