package serviceclause.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import config.NetConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.Utils;
import view.CProgressDialog;

public class ServiceTermActivity extends AppCompatActivity implements View.OnClickListener {

    private View rootView;
    private RelativeLayout returnRl;
    private CProgressDialog cProgressDialog;
    private TextView contentTv;
    private String content;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case 1:
                        cProgressDialog.dismiss();
                        contentTv.setText(content);
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_service_term, null);
        setContentView(rootView);
        initView();
        setListener();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(1);
            handler = null;
        }
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_service_term_return);
        cProgressDialog = Utils.initProgressDialog(ServiceTermActivity.this, cProgressDialog);
        contentTv = (TextView) rootView.findViewById(R.id.tv_service_term);
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
    }

    private void loadData() {
        cProgressDialog.show();
        String articleUrl = NetConfig.articleUrl;
        Utils.log(ServiceTermActivity.this, articleUrl);
        Request request = new Request.Builder().url(articleUrl).get().build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Utils.log(ServiceTermActivity.this, "serviceTermJson\n" + json);
                    try {
                        JSONObject beanObj = new JSONObject(json);
                        if (beanObj.optInt("code") == 1) {
                            JSONObject dataObj = beanObj.optJSONObject("data");
                            if (dataObj != null) {
                                content = dataObj.optString("a_desc");
                                if (!TextUtils.isEmpty(content)) {
                                    handler.sendEmptyMessage(1);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_service_term_return:
                finish();
                break;
        }
    }
}
