package workerscreen.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gjzg.R;

import bean.WorkerScreenBean;
import config.CodeConfig;
import config.IntentConfig;
import utils.Utils;

public class WorkerScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private View rootView;
    private RelativeLayout closeRl, searchRl;
    private EditText nameEt;
    private ImageView leisureIv, midIv;
    private String u_status = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_screen_worker, null);
        setContentView(rootView);
        initView();
        setListener();
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        closeRl = (RelativeLayout) rootView.findViewById(R.id.rl_screen_close);
        searchRl = (RelativeLayout) rootView.findViewById(R.id.rl_screen_search);
        nameEt = (EditText) rootView.findViewById(R.id.et_screen_name);
        leisureIv = (ImageView) rootView.findViewById(R.id.iv_screen_worker_leisure);
        midIv = (ImageView) rootView.findViewById(R.id.iv_screen_worker_mid);
    }

    private void setListener() {
        closeRl.setOnClickListener(this);
        searchRl.setOnClickListener(this);
        leisureIv.setOnClickListener(this);
        midIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_screen_close:
                finish();
                break;
            case R.id.rl_screen_search:
                result();
                break;
            case R.id.iv_screen_worker_leisure:
                u_status = "0";
                refreshState();
                break;
            case R.id.iv_screen_worker_mid:
                u_status = "1";
                refreshState();
                break;
        }
    }

    private void refreshState() {
        if (u_status.equals("0")) {
            leisureIv.setImageResource(R.mipmap.worker_leisure);
            midIv.setImageResource(R.mipmap.worker_mid_g);
        } else if (u_status.equals("1")) {
            leisureIv.setImageResource(R.mipmap.worker_leisure_g);
            midIv.setImageResource(R.mipmap.worker_mid);
        }
    }

    private void result() {
        Intent searchIntent = new Intent();
        WorkerScreenBean workerScreenBean = new WorkerScreenBean();
        workerScreenBean.setU_true_name(nameEt.getText().toString());
        workerScreenBean.setU_status(u_status);
        searchIntent.putExtra(IntentConfig.screenToWorker, workerScreenBean);
        setResult(CodeConfig.screenResultCode, searchIntent);
        finish();
    }
}
