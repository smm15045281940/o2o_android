package withdraw.module;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import config.NetConfig;
import config.VarConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import withdraw.bean.WithDrawBean;
import withdraw.listener.WithDrawListener;

/**
 * Created by Administrator on 2017/10/24.
 */

public class WithDrawModule implements IWithDrawModule {

    private OkHttpClient okHttpClient;
    private Call withDrawCall;

    public WithDrawModule() {
        okHttpClient = new OkHttpClient();
    }

    @Override
    public void withdraw(WithDrawBean withDrawBean, final WithDrawListener withDrawListener) {
        RequestBody requestBody = new FormBody.Builder()
                .add("u_id", withDrawBean.getU_id())
                .add("uwl_amount", withDrawBean.getUwl_amount())
                .add("p_id", withDrawBean.getP_id())
                .add("uwl_card", withDrawBean.getUwl_card())
                .add("uwl_truename", withDrawBean.getUwl_truename())
                .add("u_pass", withDrawBean.getPassword())
                .build();
        Request request = new Request.Builder().url(NetConfig.applyWithdrawUrl).post(requestBody).build();
        withDrawCall = okHttpClient.newCall(request);
        withDrawCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                withDrawListener.failure(VarConfig.noNet);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Log.e("WithDrawModule", "json=" + json);
                    if (!TextUtils.isEmpty(json)) {
                        try {
                            JSONObject beanObj = new JSONObject(json);
                            int code = beanObj.optInt("code");
                            JSONObject dataObj = beanObj.optJSONObject("data");
                            if (dataObj != null) {
                                String msg = dataObj.optString("msg");
                                if (!TextUtils.isEmpty(msg)) {
                                    switch (code) {
                                        case 0:
                                            withDrawListener.failure(msg);
                                            break;
                                        case 1:
                                            withDrawListener.success(msg);
                                            break;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void cancelTask() {
        if (withDrawCall != null) {
            withDrawCall.cancel();
            withDrawCall = null;
        }
        if (okHttpClient != null) {
            okHttpClient = null;
        }
    }
}
