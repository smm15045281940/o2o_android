package task.module;


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import task.bean.TaskBean;
import config.VarConfig;
import task.listener.TaskCollectListener;
import task.listener.TaskListener;
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
    public void load(String url, final TaskListener taskListener) {
        Request request = new Request.Builder().url(url).get().build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                taskListener.failure(VarConfig.noNet);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    if (!TextUtils.isEmpty(json)) {
                        try {
                            JSONObject objBean = new JSONObject(json);
                            if (objBean.optInt("code") == 200) {
                                JSONArray arrData = objBean.optJSONArray("data");
                                if (arrData != null) {
                                    List<TaskBean> taskBeanList = new ArrayList<>();
                                    for (int i = 0; i < arrData.length(); i++) {
                                        JSONObject o = arrData.optJSONObject(i);
                                        if (o != null) {
                                            TaskBean taskBean = new TaskBean();
                                            taskBean.setT_id(o.optString("t_id"));
                                            taskBean.setT_title(o.optString("t_title"));
                                            taskBean.setT_info(o.optString("t_info"));
                                            taskBean.setT_amount(o.optString("t_amount"));
                                            taskBean.setT_duration(o.optString("t_duration"));
                                            taskBean.setT_edit_amount(o.optString("t_edit_amount"));
                                            taskBean.setT_amount_edit_times(o.optString("t_amount_edit_times"));
                                            taskBean.setT_posit_x(o.optString("t_posit_x"));
                                            taskBean.setT_posit_y(o.optString("t_posit_y"));
                                            taskBean.setT_author(o.optString("t_author"));
                                            taskBean.setT_in_time(o.optString("t_in_time"));
                                            taskBean.setT_last_edit_time(o.optString("t_last_edit_time"));
                                            taskBean.setT_last_editor(o.optString("t_last_editor"));
                                            taskBean.setT_status(o.optString("t_status"));
                                            taskBean.setT_phone(o.optString("t_phone"));
                                            taskBean.setT_phone_status(o.optString("t_phone_status"));
                                            taskBean.setT_type(o.optString("t_type"));
                                            taskBean.setT_storage(o.optString("t_storage"));
                                            taskBean.setBd_id(o.optString("bd_id"));
                                            taskBean.setFavorate(o.optInt("favorate"));
                                            taskBean.setU_img(o.optString("u_img"));
                                            taskBeanList.add(taskBean);
                                        } else {
                                            taskListener.failure("obj == null");
                                        }
                                    }
                                    taskListener.success(taskBeanList);
                                } else {
                                    taskListener.failure("data array == null");
                                }
                            } else {
                                taskListener.failure("code != 200");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        taskListener.failure("json == null");
                    }
                } else {
                    taskListener.failure("response is fail");
                }
            }
        });
    }

    @Override
    public void taskCollect(String url, final TaskCollectListener taskCollectListener) {
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
                    taskCollectListener.success(response.body().string());
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
        if (okHttpClient != null)
            okHttpClient = null;
    }
}
