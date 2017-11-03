package activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.gjzg.R;

import bean.ToFireBean;
import config.IntentConfig;
import config.NetConfig;
import utils.UserUtils;
import utils.Utils;

public class FireActivity extends AppCompatActivity implements View.OnClickListener {

    private View rootView;
    private ToFireBean toFireBean;
    private EditText contentEt;
    private TextView submitTv;
    private int praiseCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(FireActivity.this).inflate(R.layout.activity_fire, null);
        setContentView(rootView);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        contentEt = (EditText) rootView.findViewById(R.id.et_fire_content);
        submitTv = (TextView) rootView.findViewById(R.id.tv_fire_submit);
    }

    private void initData() {
        toFireBean = (ToFireBean) getIntent().getSerializableExtra(IntentConfig.toFire);
        Utils.log(FireActivity.this, "toFireBean\n" + toFireBean.toString());
    }

    private void setListener() {
        submitTv.setOnClickListener(this);
    }

    private void submit() {
        String fireUrl = NetConfig.orderUrl +
                "?action=unbind" +
                "&tew_id=" + toFireBean.getTewId() +
                "&t_id=" + toFireBean.getTaskId() +
                "&type=fire" +
                "&o_worker=" + toFireBean.getFireId() +
                "&u_id=" + UserUtils.readUserData(FireActivity.this).getId() +
                "&s_id=" + toFireBean.getSkillId() +
                "&start=" + praiseCount +
                "&appraisal=" + contentEt.getText().toString();
        Utils.log(FireActivity.this, "fireUrl\n" + fireUrl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_fire_submit:
                submit();
                break;
        }
    }
}
