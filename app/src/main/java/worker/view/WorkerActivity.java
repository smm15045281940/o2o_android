package worker.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import bean.ScreenBean;
import config.NetConfig;
import login.view.LoginActivity;
import skills.bean.SkillsBean;
import talkworker.view.TalkWorkerActivity;
import utils.UserUtils;
import worker.bean.WorkerBean;
import config.CodeConfig;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import utils.Utils;
import worker.adapter.WorkerAdapter;
import worker.listener.WorkerClickHelp;
import worker.presenter.IWorkerPresenter;
import worker.presenter.WorkerPresenter;
import workerscreen.view.WorkerScnActivity;

public class WorkerActivity extends AppCompatActivity implements IWorkerActivity, View.OnClickListener, PullToRefreshLayout.OnRefreshListener, WorkerClickHelp {

    private View rootView;
    private RelativeLayout returnRl;
    private RelativeLayout screenRl;
    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private List<WorkerBean> list;
    private WorkerAdapter adapter;
    private String workerKindId;
    private IWorkerPresenter workerPresenter;

    private int collectPosition;
    private String tip;

    private final int DONE = 0;
    private final int COLLECT_SUCCESS = 1;
    private final int COLLECT_FAILURE = 2;
    private final int CANCEL_COLLECT_SUCCESS = 3;
    private final int CANCEL_COLLECT_FAILURE = 4;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case DONE:
                        adapter.notifyDataSetChanged();
                        break;
                    case COLLECT_SUCCESS:
                        Utils.toast(WorkerActivity.this, tip);
                        list.get(collectPosition).setFavorite(1);
                        adapter.notifyDataSetChanged();
                        break;
                    case COLLECT_FAILURE:
                        Utils.toast(WorkerActivity.this, tip);
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
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1);
        workerPresenter.destroy();
        workerPresenter = null;
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_worker_return);
        screenRl = (RelativeLayout) rootView.findViewById(R.id.rl_worker_screen);
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
    }

    private void initData() {
        workerPresenter = new WorkerPresenter(this);
        list = new ArrayList<>();
        adapter = new WorkerAdapter(WorkerActivity.this, list, this);
        Intent intent = getIntent();
        if (intent != null) {
            SkillsBean wkb = (SkillsBean) intent.getSerializableExtra("skillsBean");
            if (wkb != null) {
                workerKindId = wkb.getS_id();
            }
        }
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
        if (UserUtils.isUserLogin(WorkerActivity.this)) {
            workerPresenter.load("http://api.gangjianwang.com/Users/getUsers?u_skills=" + workerKindId + "&fu_id=" + UserUtils.readUserData(WorkerActivity.this).getId());
        } else {
            workerPresenter.load("http://api.gangjianwang.com/Users/getUsers?u_skills=" + workerKindId + "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_worker_return:
                finish();
                break;
            case R.id.rl_worker_screen:
                startActivityForResult(new Intent(this, WorkerScnActivity.class), CodeConfig.screenRequestCode);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodeConfig.screenRequestCode && resultCode == CodeConfig.screenResultCode && data != null) {
            ScreenBean screenBean = (ScreenBean) data.getSerializableExtra("screenBean");
            if (screenBean != null) {
                Log.e("TAG", "screenBean=" + screenBean.toString());
                workerPresenter.load("http://api.gangjianwang.com/Users/getUsers?u_skills=" + workerKindId + "&u_task_status=" + screenBean.getState() + "&u_true_name=" + screenBean.getName());
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        ptrl.hideHeadView();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        ptrl.hideFootView();
    }

    @Override
    public void success(List<WorkerBean> workerBeanList) {
        Utils.log(WorkerActivity.this, "工人列表:" + workerBeanList.toString());
        list.clear();
        list.addAll(workerBeanList);
        handler.sendEmptyMessage(DONE);
    }

    @Override
    public void failure(String failure) {
        Utils.log(WorkerActivity.this, "failure=" + failure);
    }

    @Override
    public void collectSuccess(String success) {
        tip = success;
        handler.sendEmptyMessage(COLLECT_SUCCESS);
    }

    @Override
    public void collectFailure(String failure) {
        tip = failure;
        handler.sendEmptyMessage(COLLECT_FAILURE);
    }

    @Override
    public void cancelCollectSuccess(String success) {

    }

    @Override
    public void cancelCollectFailure(String failure) {

    }

    @Override
    public void onClick(int position, int id) {
        switch (id) {
            case R.id.ll_item_worker:
                Intent intent = new Intent(WorkerActivity.this, TalkWorkerActivity.class);
                intent.putExtra("workerBean", list.get(position));
                startActivity(intent);
                break;
            case R.id.iv_item_worker_collect:
                if (UserUtils.isUserLogin(WorkerActivity.this)) {
                    collectPosition = position;
                    int favorite = list.get(collectPosition).getFavorite();
                    switch (favorite) {
                        case 0:
                            workerPresenter.favoriteAdd(NetConfig.favorateAddUrl + "?u_id=" + UserUtils.readUserData(WorkerActivity.this).getId() + "&f_type_id=" + list.get(collectPosition).getU_id() + "&f_type=1");
                            break;
                        case 1:

                            Log.e("TAG", "cancel_collect");
                            break;
                    }
                } else {
                    startActivity(new Intent(WorkerActivity.this, LoginActivity.class));
                }
                break;
        }
    }
}
