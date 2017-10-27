package task.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import listener.IdPosClickHelp;
import adapter.TaskAdapter;
import bean.TaskBean;
import config.NetConfig;
import config.StateConfig;
import config.VarConfig;
import login.view.LoginActivity;
import taskscreen.bean.TaskScreenBean;
import taskscreen.view.TaskScreenActivity;
import task.presenter.ITaskPresenter;
import task.presenter.TaskPresenter;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import utils.DataUtils;
import utils.UserUtils;
import utils.Utils;
import view.CProgressDialog;

public class TaskActivity extends AppCompatActivity implements ITaskActivity, View.OnClickListener, PullToRefreshLayout.OnRefreshListener, IdPosClickHelp {

    private View rootView, emptyView, netView;
    private FrameLayout fl;
    private TextView netTv;
    private CProgressDialog cpd;
    private RelativeLayout returnRl, screenRl;
    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private List<TaskBean> list;
    private TaskAdapter adapter;
    private final int FIRST = 0, REFRESH = 1, LOAD = 2;
    private int STATE;
    private ITaskPresenter taskPresenter;

    private final int DONE = 0;
    private final int COLLECT_SUCCESS = 1;
    private final int COLLECT_FAILURE = 2;

    private int clickPostion;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case DONE:
                        notifyData();
                        break;
                    case COLLECT_SUCCESS:
                        list.get(clickPostion).setFavorite(1);
                        adapter.notifyDataSetChanged();
                        Utils.toast(TaskActivity.this, "收藏成功");
                        break;
                    case COLLECT_FAILURE:
                        Utils.toast(TaskActivity.this, "收藏失败");
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_task, null);
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
        if (handler != null) {
            handler.removeMessages(1);
            handler = null;
        }
        if (taskPresenter != null) {
            taskPresenter.destroy();
            taskPresenter = null;
        }
    }

    private void initView() {
        initRootView();
        initEmptyView();
    }

    private void initRootView() {
        cpd = Utils.initProgressDialog(TaskActivity.this, cpd);
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_task_return);
        screenRl = (RelativeLayout) rootView.findViewById(R.id.rl_task_screen);
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
    }

    private void initEmptyView() {
        fl = (FrameLayout) rootView.findViewById(R.id.fl);
        emptyView = LayoutInflater.from(TaskActivity.this).inflate(R.layout.empty_data, null);
        fl.addView(emptyView);
        emptyView.setVisibility(View.GONE);
        netView = LayoutInflater.from(TaskActivity.this).inflate(R.layout.empty_net, null);
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

    private void initData() {
        list = new ArrayList<>();
        adapter = new TaskAdapter(this, list, this);
        taskPresenter = new TaskPresenter(TaskActivity.this);
        STATE = FIRST;
    }

    private void setData() {
        plv.setAdapter(adapter);
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
        }
        if (UserUtils.isUserLogin(TaskActivity.this)) {
            taskPresenter.load(NetConfig.taskBaseUrl + "?u_id=" + UserUtils.readUserData(TaskActivity.this).getId());
        } else {
            taskPresenter.load(NetConfig.taskBaseUrl);
        }
    }

    private void notifyData() {
        switch (STATE) {
            case FIRST:
                cpd.dismiss();
                if (list.size() == 0) {
                    ptrl.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    netView.setVisibility(View.GONE);
                } else {
                    ptrl.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    netView.setVisibility(View.GONE);
                }
                break;
            case REFRESH:
                ptrl.hideHeadView();
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_task_return:
                finish();
                break;
            case R.id.rl_task_screen:
                startActivityForResult(new Intent(this, TaskScreenActivity.class), 1);
                break;
        }
    }

    @Override
    public void onClick(int id, int pos) {
        clickPostion = pos;
        TaskBean taskBean = list.get(clickPostion);
        int favorite = taskBean.getFavorite();
        String taskId = taskBean.getTaskId();
        switch (id) {
            case R.id.ll_item_task:
                Log.e("TAG", "task-jump");
                break;
            case R.id.iv_item_task_collect:
                switch (favorite) {
                    case 0:
                        if (UserUtils.isUserLogin(TaskActivity.this)) {
                            String collectUrl = NetConfig.favorateAddUrl + "?u_id=" + UserUtils.readUserData(TaskActivity.this).getId() + "&f_type_id=" + taskId + "&f_type=0";
                            Log.e("TAG", "collectUrl=" + collectUrl);
                            taskPresenter.collect(collectUrl);
                        } else {
                            startActivity(new Intent(TaskActivity.this, LoginActivity.class));
                        }
                        break;
                }
                break;
        }
        /**
         * String t_id = list.get(position).getT_id();
         if (!TextUtils.isEmpty(t_id)) {
         Intent intent = new Intent(this, TalkActivity.class);
         intent.putExtra("t_id", t_id);
         startActivity(intent);
         }*/
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
    public void loadSuccess(String taskJson) {
        list.clear();
        list.addAll(DataUtils.getTaskBeanList(taskJson));
        handler.sendEmptyMessage(DONE);
    }

    @Override
    public void loadFailure(String loadFailure) {
        if (loadFailure.equals(VarConfig.noNet)) {
            switch (STATE) {
                case FIRST:
                    cpd.dismiss();
                    ptrl.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    netView.setVisibility(View.VISIBLE);
                    break;
                case REFRESH:
                    ptrl.hideHeadView();
                    Utils.toast(TaskActivity.this, StateConfig.loadNonet);
                    break;
            }
        }
    }

    @Override
    public void collectSuccess(String collectJson) {
        Log.e("TAG", "collectJson=" + collectJson);
        try {
            JSONObject beanObj = new JSONObject(collectJson);
            int code = beanObj.optInt("code");
            switch (code) {
                case 1:
                    handler.sendEmptyMessage(COLLECT_SUCCESS);
                    break;
                default:
                    handler.sendEmptyMessage(COLLECT_FAILURE);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void collectFailure(String collectFailure) {
        handler.sendEmptyMessage(COLLECT_FAILURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1 && data != null) {
            TaskScreenBean taskScreenBean = (TaskScreenBean) data.getSerializableExtra("taskScreenBean");
            if (taskScreenBean != null) {
                taskScreen(taskScreenBean);
            }
        }
    }

    private void taskScreen(TaskScreenBean taskScreenBean) {
        if (taskScreenBean != null) {
            STATE = FIRST;
            cpd.show();
            Utils.log(TaskActivity.this, Utils.getTaskUrl(taskScreenBean));
            taskPresenter.load(Utils.getTaskUrl(taskScreenBean));
        }
    }
}
