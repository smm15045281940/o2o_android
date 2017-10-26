package talkworker.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.gjzg.R;

import utils.Utils;
import worker.bean.WorkerBean;

public class TalkWorkerActivity extends AppCompatActivity implements View.OnClickListener {

    private View rootView;
    private RelativeLayout returnRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(TalkWorkerActivity.this).inflate(R.layout.activity_talk_worker, null);
        setContentView(rootView);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_worker_return);
    }

    private void initData() {
        WorkerBean workerBean = (WorkerBean) getIntent().getSerializableExtra("workerBean");
        Utils.log(TalkWorkerActivity.this, workerBean.toString());
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_talk_worker_return:
                finish();
                break;
        }
    }
}
