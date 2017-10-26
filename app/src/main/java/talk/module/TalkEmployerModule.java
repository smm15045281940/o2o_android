package talk.module;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import talk.listener.LoadSkillListener;
import talk.listener.TalkEmployerListener;

/**
 * Created by Administrator on 2017/10/26.
 */

public class TalkEmployerModule implements ITalkEmployerModule {

    private OkHttpClient okHttpClient;
    private Call call, skillCall;

    public TalkEmployerModule() {
        okHttpClient = new OkHttpClient();
    }

    @Override
    public void load(String url, final TalkEmployerListener talkEmployerListener) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    talkEmployerListener.success(response.body().string());
                }
            }
        });
    }

    @Override
    public void loadSkill(String url, final LoadSkillListener loadSkillListener) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        skillCall = okHttpClient.newCall(request);
        skillCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    loadSkillListener.success(response.body().string());
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
        if (skillCall != null) {
            skillCall.cancel();
            skillCall = null;
        }
        if (okHttpClient != null) {
            okHttpClient = null;
        }
    }
}
