package activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.gjzg.R;

import utils.Utils;

public class WorkerActivity extends AppCompatActivity implements View.OnClickListener {

    private View rootView;
    private LinearLayout backLl, screenLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_worker, null);
        setContentView(rootView);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        backLl = (LinearLayout) rootView.findViewById(R.id.ll_worker_back);
        screenLl = (LinearLayout) rootView.findViewById(R.id.ll_worker_screen);
    }

    private void initData() {
        Intent intent = getIntent();
        String str = intent.getStringExtra("job");
        Utils.toast(this, str);
    }

    private void setListener() {
        backLl.setOnClickListener(this);
        screenLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_worker_back:
                finish();
                break;
            case R.id.ll_worker_screen:
                Utils.toast(this, "筛选");
                break;
        }
    }
}
