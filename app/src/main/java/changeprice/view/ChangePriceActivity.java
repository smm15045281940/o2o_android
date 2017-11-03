package changeprice.view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import bean.ToChangePriceBean;
import changeprice.presenter.ChangePricePresenter;
import changeprice.presenter.IChangePricePresenter;
import config.IntentConfig;
import config.NetConfig;
import utils.DataUtils;
import utils.Utils;
import view.CProgressDialog;

public class ChangePriceActivity extends AppCompatActivity implements IChangePriceActivity, View.OnClickListener {

    private View rootView;
    private RelativeLayout returnRl, submitRl;
    private TextView skillNameTv, amountTv, startTimeTv, endTimeTv;
    private EditText priceEt;
    private CProgressDialog cpd;
    private String skillName;
    private ToChangePriceBean toChangePriceBean;
    private IChangePricePresenter changePricePresenter;

    private final int LOAD_SUCCESS = 1;
    private final int LOAD_FAILURE = 2;
    private final int CHANGE_SUCCESS = 3;
    private final int CHANGE_FAILURE = 4;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case LOAD_SUCCESS:
                        notifyData();
                        break;
                    case LOAD_FAILURE:
                        cpd.dismiss();
                        break;
                    case CHANGE_SUCCESS:
                        cpd.dismiss();
                        Utils.toast(ChangePriceActivity.this, "修改成功");
                        finish();
                        break;
                    case CHANGE_FAILURE:
                        cpd.dismiss();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(ChangePriceActivity.this).inflate(R.layout.activity_change_price, null);
        setContentView(rootView);
        initView();
        initData();
        setListener();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (changePricePresenter != null) {
            changePricePresenter.destroy();
            changePricePresenter = null;
        }
        if (handler != null) {
            handler.removeMessages(LOAD_SUCCESS);
            handler.removeMessages(LOAD_FAILURE);
            handler.removeMessages(CHANGE_SUCCESS);
            handler.removeMessages(CHANGE_FAILURE);
            handler = null;
        }
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_change_price_return);
        submitRl = (RelativeLayout) rootView.findViewById(R.id.rl_change_price_submit);
        skillNameTv = (TextView) rootView.findViewById(R.id.tv_change_price_skill_name);
        amountTv = (TextView) rootView.findViewById(R.id.tv_change_price_amount);
        startTimeTv = (TextView) rootView.findViewById(R.id.tv_change_price_startTime);
        endTimeTv = (TextView) rootView.findViewById(R.id.tv_change_price_endTime);
        priceEt = (EditText) rootView.findViewById(R.id.et_change_price_price);
        cpd = Utils.initProgressDialog(ChangePriceActivity.this, cpd);
    }

    private void initData() {
        changePricePresenter = new ChangePricePresenter(this);
        toChangePriceBean = (ToChangePriceBean) getIntent().getSerializableExtra(IntentConfig.toChangePrice);
        Utils.log(ChangePriceActivity.this, "toChangePriceBean=" + toChangePriceBean.toString());
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        submitRl.setOnClickListener(this);
        priceEt.addTextChangedListener(priceTw);
    }

    private void loadData() {
        cpd.show();
        changePricePresenter.load(NetConfig.skillsUrl);
    }

    private void notifyData() {
        cpd.dismiss();
        skillNameTv.setText(skillName);
        amountTv.setText(toChangePriceBean.getAmount());
        startTimeTv.setText(toChangePriceBean.getStartTime());
        endTimeTv.setText(toChangePriceBean.getEndTime());
        priceEt.setText(toChangePriceBean.getPrice());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_change_price_return:
                finish();
                break;
            case R.id.rl_change_price_submit:
                cpd.show();
                Utils.log(ChangePriceActivity.this, "submit=" + toChangePriceBean.toString());
                String url = NetConfig.orderUrl +
                        "?action=price" +
                        "&tew_id=" + toChangePriceBean.getTewId() +
                        "&t_id=" + toChangePriceBean.getTaskId() +
                        "&t_author=" + toChangePriceBean.getAuthorId() +
                        "&amount=" + toChangePriceBean.getPrice() +
                        "&worker_num=" + toChangePriceBean.getAmount() +
                        "&start_time=" + toChangePriceBean.getStartTime() +
                        "&end_time=" + toChangePriceBean.getEndTime() +
                        "&o_worker=" + toChangePriceBean.getWorkerId();
                Utils.log(ChangePriceActivity.this, url);
                changePricePresenter.change(url);
                break;
        }
    }

    @Override
    public void loadSuccess(String json) {
        List<String> idList = new ArrayList<>();
        idList.add(toChangePriceBean.getSkillName());
        if (idList.size() != 0) {

        }
        handler.sendEmptyMessage(LOAD_SUCCESS);
    }

    @Override
    public void loadFailure(String failure) {
        handler.sendEmptyMessage(LOAD_FAILURE);
    }

    @Override
    public void changeSuccess(String json) {
        Utils.log(ChangePriceActivity.this, json);
        handler.sendEmptyMessage(CHANGE_SUCCESS);
    }

    @Override
    public void changeFailure(String failure) {
        Utils.log(ChangePriceActivity.this, failure);
        handler.sendEmptyMessage(CHANGE_FAILURE);
    }

    TextWatcher priceTw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            toChangePriceBean.setPrice(s.toString());
        }
    };
}
