package skills.view;

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

import java.util.ArrayList;
import java.util.List;

import skills.adapter.SkillsAdapter;
import skills.bean.SkillsBean;
import config.StateConfig;
import config.VarConfig;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import skills.presenter.ISkillsPresenter;
import skills.presenter.SkillsPresenter;
import utils.Utils;
import view.CProgressDialog;
import worker.view.WorkerActivity;

public class SkillsActivity extends AppCompatActivity implements ISkillsActivity, View.OnClickListener, PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private View rootView, emptyView, netView;
    private FrameLayout fl;
    private TextView netTv;
    private RelativeLayout returnRl;
    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private CProgressDialog cpd;
    private List<SkillsBean> list;
    private SkillsAdapter adapter;

    private final int FIRST = 0, REFRESH = 1, LOAD = 2;
    private int STATE;

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

    private ISkillsPresenter skillsPresenter;

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
        if (skillsPresenter != null) {
            skillsPresenter.destroy();
            skillsPresenter = null;
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
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_skills_return);
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
        cpd = Utils.initProgressDialog(this, cpd);
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

    private void initData() {
        STATE = FIRST;
        list = new ArrayList<>();
        adapter = new SkillsAdapter(this, list);
        skillsPresenter = new SkillsPresenter(SkillsActivity.this);
    }

    private void setData() {
        plv.setAdapter(adapter);
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
        skillsPresenter.load();
    }

    private void notifyData() {
        switch (STATE) {
            case FIRST:
                cpd.dismiss();
                if (list.size() == 0) {
                    ptrl.setVisibility(View.GONE);
                    netView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
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
            case R.id.rl_skills_return:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, WorkerActivity.class);
        intent.putExtra("skillsBean", list.get(position));
        startActivity(intent);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        STATE = REFRESH;
        skillsPresenter.load();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        ptrl.hideFootView();
    }

    @Override
    public void success(List<SkillsBean> skillsBeanList) {
        Utils.log(SkillsActivity.this, "工种列表:" + skillsBeanList.toString());
        if (STATE == REFRESH) {
            list.clear();
        }
        list.addAll(skillsBeanList);
        handler.sendEmptyMessage(1);
    }

    @Override
    public void failure(String failure) {
        Utils.log(SkillsActivity.this, "failure=" + failure);
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
                    Utils.toast(SkillsActivity.this, StateConfig.loadNonet);
                    break;
            }
        }
    }

}
