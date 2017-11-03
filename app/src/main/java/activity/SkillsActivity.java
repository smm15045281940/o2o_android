package activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bean.SkillsBean;
import adapter.SkillsAdapter;
import bean.ToWorkerBean;
import config.IntentConfig;
import config.NetConfig;
import config.VarConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import utils.DataUtils;
import utils.Utils;
import view.CProgressDialog;

public class SkillsActivity extends AppCompatActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private View rootView, emptyView, netView;
    private FrameLayout fl;
    private TextView netTv;
    private RelativeLayout returnRl;
    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private CProgressDialog cpd;
    private List<SkillsBean> skillsBeanList = new ArrayList<>();
    private SkillsAdapter skillsAdapter;
    private final int SUCCESS = 1;
    private final int FAILURE = 2;
    private final int FIRST = 3;
    private final int REFRESH = 4;
    private int STATE = FIRST;
    private OkHttpClient okHttpClient;
    private Call skillsCall;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case SUCCESS:
                        notifyData();
                        break;
                    case FAILURE:
                        notifyNet();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_skills, null);
        setContentView(rootView);
        initView();
        initData();
        setData();
        setListener();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (skillsCall != null) {
            skillsCall.cancel();
            skillsCall = null;
        }
        if (okHttpClient != null) {
            okHttpClient = null;
        }
        if (handler != null) {
            handler.removeMessages(SUCCESS);
            handler.removeMessages(FAILURE);
            handler = null;
        }
    }

    private void initView() {
        initRootView();
        initEmptyView();
        initDialogView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_skills_return);
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
    }

    private void initEmptyView() {
        fl = (FrameLayout) rootView.findViewById(R.id.fl);
        emptyView = LayoutInflater.from(SkillsActivity.this).inflate(R.layout.empty_data, null);
        fl.addView(emptyView);
        emptyView.setVisibility(View.GONE);
        netView = LayoutInflater.from(SkillsActivity.this).inflate(R.layout.empty_net, null);
        netTv = (TextView) netView.findViewById(R.id.tv_no_net_refresh);
        netTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ptrl.setVisibility(View.VISIBLE);
                netView.setVisibility(View.GONE);
                STATE = FIRST;
                loadData();
            }
        });
        fl.addView(netView);
        netView.setVisibility(View.GONE);
    }

    private void initDialogView() {
        cpd = new CProgressDialog(SkillsActivity.this, R.style.dialog_cprogress);
    }

    private void initData() {
        skillsAdapter = new SkillsAdapter(this, skillsBeanList);
        okHttpClient = new OkHttpClient();
    }

    private void setData() {
        plv.setAdapter(skillsAdapter);
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        ptrl.setOnRefreshListener(this);
        plv.setOnItemClickListener(this);
    }

    private void loadData() {
        switch (STATE) {
            case FIRST:
                cpd.show();
                break;
        }
        String skillsUrl = NetConfig.skillsUrl;
        Utils.log(SkillsActivity.this, "skillsUrl\n" + skillsUrl);
        Request skillsRequest = new Request
                .Builder()
                .url(skillsUrl)
                .get()
                .build();
        skillsCall = okHttpClient.newCall(skillsRequest);
        skillsCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(FAILURE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    switch (STATE) {
                        case REFRESH:
                            skillsBeanList.clear();
                            break;
                    }
                    skillsBeanList.addAll(DataUtils.getSkillBeanList(response.body().string()));
                    Utils.log(SkillsActivity.this, "skillsBeanList\n" + skillsBeanList.toString());
                    handler.sendEmptyMessage(SUCCESS);
                }
            }
        });
    }

    private void notifyData() {
        switch (STATE) {
            case FIRST:
                cpd.dismiss();
                if (skillsBeanList.size() == 0) {
                    ptrl.setVisibility(View.GONE);
                    netView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    ptrl.setVisibility(View.VISIBLE);
                    netView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                }
                break;
            case REFRESH:
                ptrl.hideHeadView();
                break;
        }
        skillsAdapter.notifyDataSetChanged();
    }

    private void notifyNet() {
        switch (STATE) {
            case FIRST:
                cpd.dismiss();
                ptrl.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
                netView.setVisibility(View.VISIBLE);
                break;
            case REFRESH:
                ptrl.hideHeadView();
                Utils.toast(SkillsActivity.this, VarConfig.noNetTip);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_skills_return:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToWorkerBean toWorkerBean = new ToWorkerBean();
        toWorkerBean.setS_id(skillsBeanList.get(position).getS_id());
        toWorkerBean.setS_name(skillsBeanList.get(position).getS_name());
        Intent intent = new Intent(this, WorkerActivity.class);
        intent.putExtra(IntentConfig.toWorker, toWorkerBean);
        startActivity(intent);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        STATE = REFRESH;
        loadData();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        ptrl.hideFootView();
    }
}
