package getevaluate.module;

import android.text.TextUtils;

import java.io.IOException;

import getevaluate.listener.GetEvaluateListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.DataUtils;

/**
 * Created by Administrator on 2017/10/25.
 */

public class GetEvaluateModule implements IGetEvaluateModule {

    private OkHttpClient okHttpClient;
    private Call call;

    public GetEvaluateModule() {
        okHttpClient = new OkHttpClient();
    }

    @Override
    public void load(String url, final GetEvaluateListener getEvaluateListener) {
        Request request = new Request.Builder().url(url).get().build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    if (!TextUtils.isEmpty(json)) {
                        getEvaluateListener.success(DataUtils.getMyEvaluateBeanList(json));
                    }
                }
            }
        });
    }

    @Override
    public void cancelTask() {
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
}
