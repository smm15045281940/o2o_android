package employermanage.view;

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

import java.util.ArrayList;
import java.util.List;

import config.ColorConfig;
import config.StateConfig;
import config.VarConfig;
import draft.view.DraftActivity;
import employermanage.adapter.EmployerManageAdapter;
import employermanage.bean.EmployerManageBean;
import employermanage.presenter.EmployerManagePresenter;
import employermanage.presenter.IEmployerManagePresenter;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import utils.UrlUtils;
import utils.Utils;
import view.CProgressDialog;
import workermanage.view.WorkerManageActivity;

public class EmployerManageActivity extends AppCompatActivity implements IEmployerManageActivity, View.OnClickListener, PullToRefreshLayout.OnRefreshListener {

    private View rootView, emptyView, netView;
    private FrameLayout fl;
    private TextView netTv;
    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private CProgressDialog cpd;
    private RelativeLayout returnRl, draftRl;
    private RelativeLayout allRl, waitRl, talkRl, doingRl, doneRl;
    private TextView allTv, waitTv, talkTv, doingTv, doneTv;

    private EmployerManageAdapter employerManageAdapter;
    private List<EmployerManageBean> list;

    private final int ALL = 0, WAIT = 1, TALK = 2, DOING = 3, DONE = 4;
    private int curState = ALL, tarState = -1;
    private final int FIRST = 0, REFRESH = 1, LOAD = 2;
    private int STATE = FIRST;
    private IEmployerManagePresenter employerManagePresenter = new EmployerManagePresenter(this);

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case 1:
                        notifyData();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_employer_manage, null);
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
        if (employerManagePresenter != null) {
            employerManagePresenter.destroy();
            employerManagePresenter = null;
        }
        if (handler != null) {
            handler.removeMessages(1);
            handler = null;
        }
    }

    private void initView() {
        initRootView();
        initEmptyView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_employer_manage_return);
        draftRl = (RelativeLayout) rootView.findViewById(R.id.rl_employer_manage_draft);
        allRl = (RelativeLayout) rootView.findViewById(R.id.rl_employer_manage_all);
        allTv = (TextView) rootView.findViewById(R.id.tv_employer_manage_all);
        waitRl = (RelativeLayout) rootView.findViewById(R.id.rl_employer_manage_wait);
        waitTv = (TextView) rootView.findViewById(R.id.tv_employer_manage_wait);
        talkRl = (RelativeLayout) rootView.findViewById(R.id.rl_employer_manage_talk);
        talkTv = (TextView) rootView.findViewById(R.id.tv_employer_manage_talk);
        doingRl = (RelativeLayout) rootView.findViewById(R.id.rl_employer_manage_doing);
        doingTv = (TextView) rootView.findViewById(R.id.tv_employer_manage_doing);
        doneRl = (RelativeLayout) rootView.findViewById(R.id.rl_employer_manage_done);
        doneTv = (TextView) rootView.findViewById(R.id.tv_employer_manage_done);
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
        cpd = Utils.initProgressDialog(EmployerManageActivity.this, cpd);
    }

    private void initEmptyView() {
        fl = (FrameLayout) rootView.findViewById(R.id.fl);
        emptyView = LayoutInflater.from(EmployerManageActivity.this).inflate(R.layout.empty_data, null);
        fl.addView(emptyView);
        emptyView.setVisibility(View.GONE);
        netView = LayoutInflater.from(EmployerManageActivity.this).inflate(R.layout.empty_net, null);
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
        employerManageAdapter = new EmployerManageAdapter(EmployerManageActivity.this, list);
    }

    private void setData() {
        plv.setAdapter(employerManageAdapter);
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        draftRl.setOnClickListener(this);
        allRl.setOnClickListener(this);
        waitRl.setOnClickListener(this);
        talkRl.setOnClickListener(this);
        doingRl.setOnClickListener(this);
        doneRl.setOnClickListener(this);
        ptrl.setOnRefreshListener(this);
    }

    private void loadData() {
        switch (STATE) {
            case FIRST:
                cpd.show();
                break;
        }
        employerManagePresenter.load(UrlUtils.getEmployerManageUrl(EmployerManageActivity.this, curState));
    }

    private void notifyData() {
        switch (STATE) {
            case FIRST:
                if (list.size() == 0) {
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
        employerManageAdapter.notifyDataSetChanged();
    }

    private void refreshView() {
        if (tarState != curState) {
            switch (tarState) {
                case 0:
                    allTv.setTextColor(ColorConfig.red_ff3e50);
                    waitTv.setTextColor(ColorConfig.black_252323);
                    talkTv.setTextColor(ColorConfig.black_252323);
                    doingTv.setTextColor(ColorConfig.black_252323);
                    doneTv.setTextColor(ColorConfig.black_252323);
                    break;
                case 1:
                    allTv.setTextColor(ColorConfig.black_252323);
                    waitTv.setTextColor(ColorConfig.red_ff3e50);
                    talkTv.setTextColor(ColorConfig.black_252323);
                    doingTv.setTextColor(ColorConfig.black_252323);
                    doneTv.setTextColor(ColorConfig.black_252323);
                    break;
                case 2:
                    allTv.setTextColor(ColorConfig.black_252323);
                    waitTv.setTextColor(ColorConfig.black_252323);
                    talkTv.setTextColor(ColorConfig.red_ff3e50);
                    doingTv.setTextColor(ColorConfig.black_252323);
                    doneTv.setTextColor(ColorConfig.black_252323);
                    break;
                case 3:
                    allTv.setTextColor(ColorConfig.black_252323);
                    waitTv.setTextColor(ColorConfig.black_252323);
                    talkTv.setTextColor(ColorConfig.black_252323);
                    doingTv.setTextColor(ColorConfig.red_ff3e50);
                    doneTv.setTextColor(ColorConfig.black_252323);
                    break;
                case 4:
                    allTv.setTextColor(ColorConfig.black_252323);
                    waitTv.setTextColor(ColorConfig.black_252323);
                    talkTv.setTextColor(ColorConfig.black_252323);
                    doingTv.setTextColor(ColorConfig.black_252323);
                    doneTv.setTextColor(ColorConfig.red_ff3e50);
                    break;
            }
            curState = tarState;
            STATE = FIRST;
            Utils.log(EmployerManageActivity.this, "url=" + UrlUtils.getEmployerManageUrl(EmployerManageActivity.this, curState));
            loadData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_employer_manage_return:
                finish();
                break;
            case R.id.rl_employer_manage_draft:
                startActivity(new Intent(this, DraftActivity.class));
                break;
            case R.id.rl_employer_manage_all:
                tarState = ALL;
                refreshView();
                break;
            case R.id.rl_employer_manage_wait:
                tarState = WAIT;
                refreshView();
                break;
            case R.id.rl_employer_manage_talk:
                tarState = TALK;
                refreshView();
                break;
            case R.id.rl_employer_manage_doing:
                tarState = DOING;
                refreshView();
                break;
            case R.id.rl_employer_manage_done:
                tarState = DONE;
                refreshView();
                break;
        }
    }

    @Override
    public void showSuccess(List<EmployerManageBean> employerManageBeanList) {
        Utils.log(EmployerManageActivity.this, "employerManageBeanList=" + employerManageBeanList.toString());
        switch (STATE) {
            case FIRST:
                cpd.dismiss();
                list.clear();
                break;
            case REFRESH:
                list.clear();
                break;
            case LOAD:
                break;
        }
        list.addAll(employerManageBeanList);
        handler.sendEmptyMessage(1);
    }

    @Override
    public void showFailure(String failure) {
        if (failure.equals(VarConfig.noNet)) {
            switch (STATE) {
                case FIRST:
                    cpd.dismiss();
                    ptrl.setVisibility(View.GONE);
                    netView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    break;
                case REFRESH:
                    ptrl.hideHeadView();
                    Utils.toast(EmployerManageActivity.this, StateConfig.loadNonet);
                    break;
            }
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
}
