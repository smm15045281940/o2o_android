package employermanage.module;

import android.text.TextUtils;

import java.io.IOException;

import config.VarConfig;
import employermanage.listener.EmployerManageListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.DataUtils;

/**
 * Created by Administrator on 2017/10/23.
 */

public class EmployerManageModule implements IEmployerManageModule {

    private OkHttpClient okHttpClient;
    private Call call;

    public EmployerManageModule() {
        okHttpClient = new OkHttpClient();
    }

    @Override
    public void load(String url, final EmployerManageListener employerManageListener) {
        Request request = new Request.Builder().url(url).get().build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                employerManageListener.failure(VarConfig.noNet);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    if (!TextUtils.isEmpty(json)) {
                        employerManageListener.success(DataUtils.getEmployerManageBeanList(json));
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
        if (okHttpClient != null) {
            okHttpClient = null;
        }
    }
}
