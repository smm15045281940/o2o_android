package skills.module;


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import skills.bean.SkillsBean;
import config.NetConfig;
import config.VarConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import skills.listener.SkillsListener;

public class SkillsModule implements ISkillsModule {

    private OkHttpClient okHttpClient;
    private Call call;

    public SkillsModule() {
        okHttpClient = new OkHttpClient();
    }

    @Override
    public void load(final SkillsListener skillsListener) {
        Request request = new Request.Builder().url(NetConfig.skillBaseUrl).get().build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                skillsListener.failure(VarConfig.noNet);
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
                                    List<SkillsBean> skillsBeanList = new ArrayList<>();
                                    for (int i = 0; i < arrData.length(); i++) {
                                        JSONObject o = arrData.optJSONObject(i);
                                        if (o != null) {
                                            SkillsBean skillsBean = new SkillsBean();
                                            skillsBean.setS_id(o.optString("s_id"));
                                            skillsBean.setS_name(o.optString("s_name"));
                                            skillsBean.setS_info(o.optString("s_info"));
                                            skillsBean.setS_desc(o.optString("s_desc"));
                                            skillsBean.setS_status(o.optString("s_status"));
                                            skillsBeanList.add(skillsBean);
                                        } else {
                                            skillsListener.failure("obj == null");
                                        }
                                    }
                                    skillsListener.success(skillsBeanList);
                                } else {
                                    skillsListener.failure("data array == null");
                                }
                            } else {
                                skillsListener.failure("code != 200");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            skillsListener.failure(e.getMessage());
                        }
                    } else {
                        skillsListener.failure("json == null");
                    }
                } else {
                    skillsListener.failure("response is fail");
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
        if (okHttpClient == null)
            okHttpClient = null;
    }

}
