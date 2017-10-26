package worker.module;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import listener.JsonListener;
import worker.bean.WorkerBean;
import config.VarConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import worker.listener.FavoriteAddListener;
import worker.listener.WorkerListener;

public class WorkerModule implements IWorkerModule {

    private OkHttpClient okHttpClient;
    private Call call, favoriteAddCall, favoriteDelCall;

    public WorkerModule() {
        okHttpClient = new OkHttpClient();
    }

    @Override
    public void load(String url, final WorkerListener workerListener) {
        if (!TextUtils.isEmpty(url)) {
            Request request = new Request.Builder().url(url).get().build();
            call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    workerListener.failure(VarConfig.noNet);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String json = response.body().string();
                        if (!TextUtils.isEmpty(json)) {
                            try {
                                JSONObject objBean = new JSONObject(json);
                                if (objBean.optInt("code") == 1) {
                                    JSONObject objData = objBean.optJSONObject("data");
                                    if (objData != null) {
                                        JSONArray arrData = objData.optJSONArray("data");
                                        if (arrData != null) {
                                            List<WorkerBean> workerBeanList = new ArrayList<>();
                                            for (int i = 0; i < arrData.length(); i++) {
                                                JSONObject o = arrData.optJSONObject(i);
                                                if (o != null) {
                                                    WorkerBean workerBean = new WorkerBean();
                                                    workerBean.setU_id(o.optString("u_id"));
                                                    workerBean.setU_name(o.optString("u_name"));
                                                    workerBean.setU_skills(o.optString("u_skills"));
                                                    workerBean.setUei_info(o.optString("uei_info"));
                                                    workerBean.setU_task_status(o.optString("u_task_status"));
                                                    workerBean.setU_true_name(o.optString("u_true_name"));
                                                    workerBean.setUcp_posit_x(o.optString("ucp_posit_x"));
                                                    workerBean.setUcp_posit_y(o.optString("ucp_posit_y"));
                                                    workerBean.setU_img(o.optString("u_img"));
                                                    workerBean.setFavorite(o.optInt("is_fav"));
                                                    workerBeanList.add(workerBean);
                                                } else {
                                                    workerListener.failure("obj == null");
                                                }
                                            }
                                            workerListener.success(workerBeanList);
                                        } else {
                                            workerListener.failure("data array == null");
                                        }
                                    } else {
                                        workerListener.failure("data == null");
                                    }
                                } else {
                                    workerListener.failure("WorkerModule:" + "code != 1");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                workerListener.failure(e.getMessage());
                            }
                        } else {
                            workerListener.failure("json == null");
                        }
                    } else {
                        workerListener.failure("response is fail");
                    }
                }
            });
        } else {
            workerListener.failure("WorkerModule:" + "workerInfo == null");
        }
    }

    @Override
    public void favoriteAdd(String url, final FavoriteAddListener favoriteAddListener) {
        Request favoriteAddRequest = new Request.Builder()
                .url(url)
                .get()
                .build();
        favoriteAddCall = okHttpClient.newCall(favoriteAddRequest);
        favoriteAddCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject beanObj = new JSONObject(json);
                        int code = beanObj.optInt("code");
                        switch (code) {
                            case 0:
                                favoriteAddListener.failure("收藏失败");
                                break;
                            case 1:
                                favoriteAddListener.success("收藏成功");
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void favoriteDel(String url, final JsonListener jsonListener) {
        Request favorateDelRequest = new Request.Builder()
                .url(url)
                .get()
                .build();
        favoriteDelCall = okHttpClient.newCall(favorateDelRequest);
        favoriteDelCall.enqueue(new Callback() {
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
        if (favoriteAddCall != null) {
            favoriteAddCall.cancel();
            favoriteAddCall = null;
        }
        if (favoriteDelCall != null) {
            favoriteDelCall.cancel();
            favoriteDelCall = null;
        }
        if (okHttpClient == null)
            okHttpClient = null;
    }
}
