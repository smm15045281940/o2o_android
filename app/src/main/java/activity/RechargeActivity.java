package activity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.gjzg.R;

import utils.Utils;

public class RechargeActivity extends CommonActivity implements View.OnClickListener {

    //根视图
    private View rootView;
    //返回视图
    private RelativeLayout returnRl;
    //金额视图
    private EditText moneyEt;
    //微信支付视图
    private LinearLayout wxLl;
    private RadioButton wxRb;
    //支付宝支付视图
    private LinearLayout zfbLl;
    private RadioButton zfbRb;
    //银联支付视图
    private LinearLayout ylLl;
    private RadioButton ylRb;
    //去支付视图
    private RelativeLayout toPayRl;
    //当前支付状态
    private int curState;
    //目标支付状态
    private int tarState;
    //微信支付状态
    private final int WX_PAY = 0;
    //支付宝支付状态
    private final int ZFB_PAY = 1;
    //银联支付状态
    private final int YL_PAY = 2;

    @Override
    protected View getRootView() {
        //初始化根视图
        return rootView = LayoutInflater.from(this).inflate(R.layout.activity_recharge, null);
    }

    @Override
    protected void initView() {
        initRootView();
    }

    private void initRootView() {
        //初始化返回视图
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_recharge_return);
        //初始化金额视图
        moneyEt = (EditText) rootView.findViewById(R.id.et_recharge_money);
        //初始化微信支付视图
        wxLl = (LinearLayout) rootView.findViewById(R.id.ll_recharge_wx);
        wxRb = (RadioButton) rootView.findViewById(R.id.rb_recharge_wx);
        //初始化支付宝支付视图
        zfbLl = (LinearLayout) rootView.findViewById(R.id.ll_recharge_zfb);
        zfbRb = (RadioButton) rootView.findViewById(R.id.rb_recharge_zfb);
        //初始化银联支付视图
        ylLl = (LinearLayout) rootView.findViewById(R.id.ll_recharge_yl);
        ylRb = (RadioButton) rootView.findViewById(R.id.rb_recharge_yl);
        //初始化去支付视图
        toPayRl = (RelativeLayout) rootView.findViewById(R.id.rl_recharge_to_pay);
    }

    @Override
    protected void initData() {
        //初始化当前支付状态
        curState = 0;
        //初始化目标支付状态
        tarState = -1;
    }

    @Override
    protected void setData() {
    }

    @Override
    protected void setListener() {
        //返回视图监听
        returnRl.setOnClickListener(this);
        //微信支付视图监听
        wxLl.setOnClickListener(this);
        //支付宝支付视图监听
        zfbLl.setOnClickListener(this);
        //银联支付视图监听
        ylLl.setOnClickListener(this);
        //去支付视图监听
        toPayRl.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
    }

    private void changePay() {
        if (curState != tarState) {
            switch (tarState) {
                case WX_PAY:
                    wxRb.setChecked(true);
                    zfbRb.setChecked(false);
                    ylRb.setChecked(false);
                    break;
                case ZFB_PAY:
                    wxRb.setChecked(false);
                    zfbRb.setChecked(true);
                    ylRb.setChecked(false);
                    break;
                case YL_PAY:
                    wxRb.setChecked(false);
                    zfbRb.setChecked(false);
                    ylRb.setChecked(true);
                    break;
                default:
                    break;
            }
            curState = tarState;
        }
    }

    private void toPay() {
        String money = moneyEt.getText().toString();
        if (TextUtils.isEmpty(money)) {
            Utils.toast(this, "请输入金额");
        } else {
            switch (curState) {
                case WX_PAY:
                    Utils.toast(this, "微信支付：" + money);
                    break;
                case ZFB_PAY:
                    Utils.toast(this, "支付宝支付：" + money);
                    break;
                case YL_PAY:
                    Utils.toast(this, "银联支付：" + money);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回视图点击事件
            case R.id.rl_recharge_return:
                finish();
                break;
            //微信支付视图点击事件
            case R.id.ll_recharge_wx:
                tarState = WX_PAY;
                changePay();
                break;
            //支付宝支付视图点击事件
            case R.id.ll_recharge_zfb:
                tarState = ZFB_PAY;
                changePay();
                break;
            //银联支付视图点击事件
            case R.id.ll_recharge_yl:
                tarState = YL_PAY;
                changePay();
                break;
            //去支付视图点击事件
            case R.id.rl_recharge_to_pay:
                toPay();
                break;
        }
    }

}
