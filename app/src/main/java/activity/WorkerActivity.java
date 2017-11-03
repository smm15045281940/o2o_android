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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bean.SkillsBean;
import bean.ToTalkWorkerBean;
import bean.ToWorkerBean;
import bean.WorkerScreenBean;
import config.IntentConfig;
import config.NetConfig;
import config.VarConfig;
import listener.IdPosClickHelp;
import login.view.LoginActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import talkworker.view.TalkWorkerActivity;
import utils.DataUtils;
import utils.UserUtils;
import bean.WorkerBean;
import config.CodeConfig;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import utils.Utils;
import adapter.WorkerAdapter;
import view.CProgressDialog;
import workerscreen.view.WorkerScreenActivity;

public class WorkerActivity extends AppCompatActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, IdPosClickHelp {

    private View rootView, emptyView, netView;
    private RelativeLayout returnRl, screenRl;
    private CProgressDialog cpd;
    private FrameLayout fl;
    private TextView netTv;
    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private List<WorkerBean> workerBeanList = new ArrayList<>();
    private WorkerAdapter workerAdapter;
    private int clickPosition;
    private final int FIRST = 1;
    private final int REFRESH = 2;
    private final int SCREEN = 3;
    private final int COLLECT = 4;
    private int STATE = FIRST;
    private ToWorkerBean toWorkerBean;
    private String workerUrl, collectWorkerUrl;
    private OkHttpClient okHttpClient;
    private Call workerCall, collectWorkerCall;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case 0:
                        notifyNet();
                        break;
                    case 1:
                        notifyData();
                        break;
                    case 2:
                        Utils.toast(WorkerActivity.this, "收藏成功");
                        workerBeanList.get(clickPosition).setIs_fav(1);
                        workerAdapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_worker, null);
        setContentView(rootView);
        initView();
        initData();
        setData();
        setListener();
        if (UserUtils.isUserLogin(WorkerActivity.this)) {
            workerUrl = NetConfig.workerUrl +
                    "?u_skills=" + toWorkerBean.getS_id() +
                    "&fu_id=" + UserUtils.readUserData(WorkerActivity.this).getId();
        } else {
            workerUrl = NetConfig.workerUrl +
                    "?u_skills=" + toWorkerBean.getS_id();
        }
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (workerCall != null) {
            workerCall.cancel();
            workerCall = null;
        }
        if (collectWorkerCall != null) {
            collectWorkerCall.cancel();
            collectWorkerCall = null;
        }
        if (okHttpClient != null) {
            okHttpClient = null;
        }
        if (handler != null) {
            handler.removeMessages(0);
            handler.removeMessages(1);
            handler.removeMessages(2);
            handler = null;
        }
    }

    private void initView() {
        initRootView();
        initEmptyView();
        initDialogView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_worker_return);
        screenRl = (RelativeLayout) rootView.findViewById(R.id.rl_worker_screen);
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
    }

    private void initEmptyView() {
        fl = (FrameLayout) rootView.findViewById(R.id.fl);
        emptyView = LayoutInflater.from(WorkerActivity.this).inflate(R.layout.empty_data, null);
        fl.addView(emptyView);
        emptyView.setVisibility(View.GONE);
        netView = LayoutInflater.from(WorkerActivity.this).inflate(R.layout.empty_net, null);
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
        cpd = new CProgressDialog(WorkerActivity.this, R.style.dialog_cprogress);
    }

    private void initData() {
        okHttpClient = new OkHttpClient();
        workerAdapter = new WorkerAdapter(WorkerActivity.this, workerBeanList, this);
        toWorkerBean = (ToWorkerBean) getIntent().getSerializableExtra(IntentConfig.toWorker);
        Utils.log(WorkerActivity.this, "toWorkerBean\n" + toWorkerBean.toString());
    }

    private void setData() {
        plv.setAdapter(workerAdapter);
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        screenRl.setOnClickListener(this);
        ptrl.setOnRefreshListener(this);
    }

    private void loadData() {
        switch (STATE) {
            case FIRST:
                cpd.show();
                break;
            case SCREEN:
                cpd.show();
                break;
        }
        Utils.log(WorkerActivity.this, "workerUrl\n" + workerUrl);
        Request workerRequest = new Request.Builder().url(workerUrl).get().build();
        workerCall = okHttpClient.newCall(workerRequest);
        workerCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    switch (STATE) {
                        case REFRESH:
                            workerBeanList.clear();
                            break;
                        case SCREEN:
                            workerBeanList.clear();
                            break;
                    }
                    workerBeanList.addAll(DataUtils.getWorkerBeanList(response.body().string()));
                    Utils.log(WorkerActivity.this, "workerBeanList\n" + workerBeanList.toString());
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }

    private void collectWorker() {
        collectWorkerUrl = NetConfig.addCollectUrl +
                "?u_id=" + UserUtils.readUserData(WorkerActivity.this).getId() +
                "&f_type_id=" + workerBeanList.get(clickPosition).getU_id() +
                "&f_type=1";
        Utils.log(WorkerActivity.this, "collectWorkerUrl\n" + collectWorkerUrl);
        Request collectWorkerRequest = new Request.Builder().url(collectWorkerUrl).get().build();
        collectWorkerCall = okHttpClient.newCall(collectWorkerRequest);
        collectWorkerCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Utils.log(WorkerActivity.this, "collectWorkerJson\n" + json);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if (jsonObject.optInt("code") == 1) {
                            handler.sendEmptyMessage(2);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void notifyData() {
        switch (STATE) {
            case FIRST:
                cpd.dismiss();
                if (workerBeanList.size() == 0) {
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
            case SCREEN:
                cpd.dismiss();
                if (workerBeanList.size() == 0) {
                    ptrl.setVisibility(View.GONE);
                    netView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    ptrl.setVisibility(View.VISIBLE);
                    netView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                }
                break;
        }
        workerAdapter.notifyDataSetChanged();
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
                Utils.toast(WorkerActivity.this, VarConfig.noNetTip);
                break;
            case SCREEN:
                cpd.dismiss();
                ptrl.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
                netView.setVisibility(View.VISIBLE);
                break;
            case COLLECT:
                Utils.toast(WorkerActivity.this, "收藏失败");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_worker_return:
                finish();
                break;
            case R.id.rl_worker_screen:
                startActivityForResult(new Intent(this, WorkerScreenActivity.class), CodeConfig.screenRequestCode);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodeConfig.screenRequestCode && resultCode == CodeConfig.screenResultCode && data != null) {
            WorkerScreenBean workerScreenBean = (WorkerScreenBean) data.getSerializableExtra(IntentConfig.screenToWorker);
            Utils.log(WorkerActivity.this, "workerScreenBean\n" + workerScreenBean.toString());
            if (UserUtils.isUserLogin(WorkerActivity.this)) {
                workerUrl = NetConfig.workerUrl +
                        "?u_skills=" + toWorkerBean.getS_id() +
                        "&u_task_status=" + workerScreenBean.getU_status() +
                        "&u_true_name=" + workerScreenBean.getU_true_name() +
                        "&fu_id=" + UserUtils.readUserData(WorkerActivity.this).getId();
            } else {
                workerUrl = NetConfig.workerUrl +
                        "?u_skills=" + toWorkerBean.getS_id() +
                        "&u_task_status=" + workerScreenBean.getU_status() +
                        "&u_true_name=" + workerScreenBean.getU_true_name();
            }
            STATE = SCREEN;
            loadData();
        }
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

    @Override
    public void onClick(int id, int pos) {
        clickPosition = pos;
        WorkerBean workerBean = workerBeanList.get(clickPosition);
        if (UserUtils.isUserLogin(WorkerActivity.this)) {
            switch (id) {
                case R.id.ll_item_worker:
                    Intent intent = new Intent(WorkerActivity.this, TalkWorkerActivity.class);
                    ToTalkWorkerBean toTalkWorkerBean = new ToTalkWorkerBean();
                    toTalkWorkerBean.setU_id(workerBeanList.get(pos).getU_id());
                    toTalkWorkerBean.setS_id(toWorkerBean.getS_id());
                    toTalkWorkerBean.setS_name(toWorkerBean.getS_name());
                    intent.putExtra(IntentConfig.toTalkWorker, toTalkWorkerBean);
                    startActivity(intent);
                    break;
                case R.id.iv_item_worker_collect:
                    if (UserUtils.readUserData(WorkerActivity.this).getId().equals(workerBean.getU_id())) {
                        Utils.toast(WorkerActivity.this, VarConfig.cannotCollectSelf);
                    } else {
                        int favorite = workerBean.getIs_fav();
                        switch (favorite) {
                            case 0:
                                collectWorker();
                                break;
                            case 1:
                                Utils.toast(WorkerActivity.this, "已经收藏过了不能再收藏");
                                break;
                        }
                    }
                    break;
            }
        } else {
            startActivity(new Intent(WorkerActivity.this, LoginActivity.class));
        }
    }

}
