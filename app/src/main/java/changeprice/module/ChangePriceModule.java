package changeprice.module;

import java.io.IOException;

import listener.JsonListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/10/31.
 */

public class ChangePriceModule implements IChangePriceModule {

    private OkHttpClient okHttpClient;
    private Call loadCall, changeCall;

    public ChangePriceModule() {
        okHttpClient = new OkHttpClient();
    }

    @Override
    public void load(String url, final JsonListener jsonListener) {
        Request loadRequest = new Request.Builder().url(url).get().build();
        loadCall = okHttpClient.newCall(loadRequest);
        loadCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                jsonListener.failure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    jsonListener.success(response.body().string());
                }
            }
        });
    }

    @Override
    public void change(String url, final JsonListener jsonListener) {
        Request changeRequest = new Request.Builder().url(url).get().build();
        changeCall = okHttpClient.newCall(changeRequest);
        changeCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                jsonListener.failure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    jsonListener.success(response.body().string());
                }
            }
        });
    }

    @Override
    public void cancelTask() {
        if (loadCall != null) {
            loadCall.cancel();
            loadCall = null;
        }
        if (changeCall != null) {
            changeCall.cancel();
            changeCall = null;
        }
        if (okHttpClient != null) {
            okHttpClient = null;
        }
    }
}
