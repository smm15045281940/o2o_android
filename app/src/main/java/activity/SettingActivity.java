package activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gjzg.R;

import cache.LruJsonCache;
import utils.Utils;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private View rootView;
    private RelativeLayout returnRl;
    private Button clearCacheBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_setting, null);
        setContentView(rootView);
        initView();
        setListener();
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_setting_return);
        clearCacheBtn = (Button) rootView.findViewById(R.id.btn_cache_clear);
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        clearCacheBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting_return:
                finish();
                break;
            case R.id.btn_cache_clear:
                LruJsonCache lruJsonCache = LruJsonCache.get(this);
                Utils.toast(this, lruJsonCache.getCacheSize() + "");
//                lruJsonCache.clear();
                break;
        }
    }
}
