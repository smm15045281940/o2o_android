package withdraw.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

import config.VarConfig;
import utils.Utils;
import view.CProgressDialog;
import withdraw.bean.WithDrawBean;
import withdraw.presenter.IWithDrawPresenter;
import withdraw.presenter.WithDrawPresenter;

public class WithDrawActivity extends AppCompatActivity implements IWithDrawActivity, View.OnClickListener {

    private View rootView;
    private RelativeLayout returnRl;
    private EditText nameEt, numberEt, moneyEt;
    private TextView bankTv, limitTv, nextTv;
    private CProgressDialog cpd;
    private IWithDrawPresenter withDrawPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_with_draw, null);
        setContentView(rootView);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_withdraw_return);
        nameEt = (EditText) rootView.findViewById(R.id.et_with_draw_card_name);
        numberEt = (EditText) rootView.findViewById(R.id.et_with_draw_card_number);
        moneyEt = (EditText) rootView.findViewById(R.id.et_with_draw_money);
        bankTv = (TextView) rootView.findViewById(R.id.tv_with_draw_bank);
        limitTv = (TextView) rootView.findViewById(R.id.tv_with_draw_limit);
        nextTv = (TextView) rootView.findViewById(R.id.tv_with_draw_next);
        cpd = Utils.initProgressDialog(WithDrawActivity.this, cpd);
    }

    private void initData() {
        withDrawPresenter = new WithDrawPresenter(this);
        WithDrawBean withDrawBean = new WithDrawBean();
        withDrawBean.setU_id("22");
        withDrawBean.setUwl_amount("100");
        withDrawBean.setP_id("1");
        withDrawBean.setUwl_card("666666");
        withDrawBean.setUwl_truename("傻宇");
        withDrawBean.setPassword("123456");
        withDrawPresenter.withdraw(withDrawBean);
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        bankTv.setOnClickListener(this);
        limitTv.setOnClickListener(this);
        nextTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_withdraw_return:
                finish();
                break;
            case R.id.tv_with_draw_bank:
                break;
            case R.id.tv_with_draw_limit:
                break;
            case R.id.tv_with_draw_next:
                break;
            default:
                break;
        }
    }

    @Override
    public void showLoading() {
        cpd.show();
    }

    @Override
    public void hideLoading() {
        cpd.dismiss();
    }

    @Override
    public void showSuccess(String success) {
        Utils.log(WithDrawActivity.this, success);
    }

    @Override
    public void showFailure(String failure) {
        Utils.log(WithDrawActivity.this, failure);
    }
}
