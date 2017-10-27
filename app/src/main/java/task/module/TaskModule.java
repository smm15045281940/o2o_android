package task.module;

import java.io.IOException;

import listener.JsonListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TaskModule implements ITaskModule {

    private OkHttpClient okHttpClient;
    private Call call, taskCollectCall;

    public TaskModule() {
        okHttpClient = new OkHttpClient();
    }

    @Override
    public void load(String url, final JsonListener jsonListener) {
        Request request = new Request.Builder().url(url).get().build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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
    public void collect(String url, final JsonListener jsonListener) {
        Request taskCollectRequest = new Request.Builder()
                .url(url)
                .get()
                .build();
        taskCollectCall = okHttpClient.newCall(taskCollectRequest);
        taskCollectCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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
        if (call != null) {
            call.cancel();
            call = null;
        }
        if (taskCollectCall != null) {
            taskCollectCall.cancel();
            taskCollectCall = null;
        }
        if (okHttpClient != null)
            okHttpClient = null;
    }
}
