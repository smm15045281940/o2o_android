package activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.gjzg.R;

public class SetPwdActivity extends AppCompatActivity implements View.OnClickListener {

    //根视图
    private View rootView;
    //返回视图
    private RelativeLayout returnRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //初始化根视图
        rootView = View.inflate(this, R.layout.activity_set_pwd, null);
        setContentView(rootView);
        initView();
        setListener();
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        //初始化返回视图
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_set_pwd_return);
    }

    private void setListener() {
        //返回视图监听
        returnRl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回视图点击事件
            case R.id.rl_set_pwd_return:
                finish();
                break;
        }
    }
}
