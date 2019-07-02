package cn.edu.nuc.dianping_client.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import cn.edu.nuc.dianping_client.R;

public class PayPasswordDialog extends Dialog{
    Context context;
    @ViewInject(R.id.tv_return)
    private TextView tvReturn;
    @ViewInject(R.id.pay_password)
    private PayPasswordView payPassword;
    @ViewInject(R.id.tv1)
    private TextView tv1;
    @ViewInject(R.id.tv2)
    private TextView tv2;
    @ViewInject(R.id.tv3)
    private TextView tv3;
    @ViewInject(R.id.tv4)
    private TextView tv4;
    @ViewInject(R.id.tv5)
    private TextView tv5;
    @ViewInject(R.id.tv6)
    private TextView tv6;
    @ViewInject(R.id.tv7)
    private TextView tv7;
    @ViewInject(R.id.tv8)
    private TextView tv8;
    @ViewInject(R.id.tv9)
    private TextView tv9;
    @ViewInject(R.id.tv)
    private TextView tv;
    @ViewInject(R.id.tv_del)
    private TextView tvDel;

    public PayPasswordDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pay_pwd, null);

        ViewUtils.inject(this,view);
        setContentView(view);
//        initView();

        Window window = getWindow();
        WindowManager.LayoutParams mParams = window.getAttributes();
        mParams.width= WindowManager.LayoutParams.MATCH_PARENT;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(mParams);
        setCanceledOnTouchOutside(true);


        tvReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payPassword.delLastPassword();
            }
        });
        payPassword.setPayPasswordEndListener(new PayPasswordView.PayEndListener() {
            @Override
            public void doEnd(String password) {
                if (dialogClick!=null){
                    dialogClick.doConfirm(password);
                }
            }
        });

    }




    DialogClick dialogClick;
    public void setDialogClick(DialogClick dialogClick){
        this.dialogClick=dialogClick;
    }

    @OnClick({R.id.tv,R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7,R.id.tv8,R.id.tv9,R.id.tv_del})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv:
                payPassword.addPassword("0");
                break;
            case R.id.tv1:
                payPassword.addPassword("1");
                break;
            case R.id.tv2:
                payPassword.addPassword("2");
                break;
            case R.id.tv3:
                payPassword.addPassword("3");
                break;
            case R.id.tv4:
                payPassword.addPassword("4");
                break;
            case R.id.tv5:
                payPassword.addPassword("5");
                break;
            case R.id.tv6:
                payPassword.addPassword("6");
                break;
            case R.id.tv7:
                payPassword.addPassword("7");
                break;
            case R.id.tv8:
                payPassword.addPassword("8");
                break;
            case R.id.tv9:
                payPassword.addPassword("9");
                break;
        }
    }

    public interface DialogClick{
        void doConfirm(String password);
    }
}
