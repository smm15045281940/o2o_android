package employertodoing.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gjzg.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.EmployerToDoingAdapter;
import bean.EmployerToDoingBean;
import bean.EmployerToTalkBean;
import bean.TInfoTaskBean;
import bean.ToEmployerToDoingBean;
import bean.ToJumpWorkerBean;
import config.IntentConfig;
import config.NetConfig;
import jumpworker.view.JumpWorkerActivity;
import listener.IdPosClickHelp;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.DataUtils;
import utils.Utils;
import view.CProgressDialog;

public class EmployerToDoingActivity extends AppCompatActivity implements View.OnClickListener, IdPosClickHelp {

    private View rootView;
    private RelativeLayout returnRl;
    private ListView lv;
    private ToEmployerToDoingBean toEmployerToDoingBean;
    private List<EmployerToDoingBean> employerToDoingBeanList = new ArrayList<>();
    private EmployerToDoingAdapter employerToDoingAdapter;
    private OkHttpClient okHttpClient;
    private int clickPosition;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case 1:
                        skill();
                        break;
                    case 2:
                        employerToDoingAdapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(EmployerToDoingActivity.this).inflate(R.layout.activity_employer_to_doing, null);
        setContentView(rootView);
        initView();
        initData();
        setData();
        setListener();
        loadData();
    }

    private void initView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_employer_to_doing_return);
        lv = (ListView) rootView.findViewById(R.id.lv_employer_to_doing);
    }

    private void initData() {
        toEmployerToDoingBean = (ToEmployerToDoingBean) getIntent().getSerializableExtra(IntentConfig.toEmployerToDoing);
        employerToDoingAdapter = new EmployerToDoingAdapter(EmployerToDoingActivity.this, employerToDoingBeanList, this);
        okHttpClient = new OkHttpClient();
    }

    private void setData() {
        lv.setAdapter(employerToDoingAdapter);
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
    }

    private void loadData() {
        String url = NetConfig.taskBaseUrl + "?action=info&t_id=" + toEmployerToDoingBean.getTaskId();
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    List<EmployerToDoingBean> list = new ArrayList<>();
                    list.addAll(DataUtils.getEmployerToDoingBeanList(json));
                    for (int i = 0; i < list.size(); i++) {
                        String status = list.get(i).getStatus();
                        if (TextUtils.isEmpty(status) || status.equals("null")) {
                            employerToDoingBeanList.add(list.get(i));
                        } else if (status.equals("1")) {
                            employerToDoingBeanList.add(list.get(i));
                        }
                    }
                    Utils.log(EmployerToDoingActivity.this, employerToDoingBeanList.toString());

                    TInfoTaskBean tInfoTaskBean = DataUtils.getTInfoTaskBean(json);
                    Utils.log(EmployerToDoingActivity.this, "tInfoTaskBean\n" + tInfoTaskBean.toString());

                    handler.sendEmptyMessage(1);
                }
            }
        });

    }

    private void skill() {
        Request request = new Request.Builder().url(NetConfig.skillsUrl).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    for (int i = 0; i < employerToDoingBeanList.size(); i++) {
                        List<String> skillIdList = new ArrayList<>();
                        skillIdList.add(employerToDoingBeanList.get(i).getSkillId());
                        List<String> skillNameList = new ArrayList<String>();
                        skillNameList.addAll(DataUtils.getSkillNameList(json, skillIdList));
                        if (skillNameList.size() != 0) {
                            employerToDoingBeanList.get(i).setSkillName(skillNameList.get(0));
                        }
                    }
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_employer_to_doing_return:
                finish();
                break;
        }
    }

    @Override
    public void onClick(int id, int pos) {
        clickPosition = pos;
        switch (id) {
            case R.id.ll_item_employer_to_talk_type_1:
                ToJumpWorkerBean toJumpWorkerBean = new ToJumpWorkerBean();
                toJumpWorkerBean.setWorkerId(employerToDoingBeanList.get(clickPosition).getWorkerId());
                toJumpWorkerBean.setOrderId(employerToDoingBeanList.get(clickPosition).getOrderId());
                toJumpWorkerBean.setTewId(employerToDoingBeanList.get(clickPosition).getTewId());
                toJumpWorkerBean.setTaskId(employerToDoingBeanList.get(clickPosition).getTaskId());
                toJumpWorkerBean.setSkillId(employerToDoingBeanList.get(clickPosition).getSkillId());
                toJumpWorkerBean.setO_status(employerToDoingBeanList.get(clickPosition).getO_status());
                toJumpWorkerBean.setO_confirm(employerToDoingBeanList.get(clickPosition).getO_confirm());
                Intent i = new Intent(EmployerToDoingActivity.this, JumpWorkerActivity.class);
                i.putExtra(IntentConfig.toJumpWorker, toJumpWorkerBean);
                startActivity(i);
                break;
        }
    }
}
