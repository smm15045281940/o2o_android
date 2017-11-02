package employertotalk.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import adapter.EmployerToTalkAdapter;
import bean.EmployerToTalkBean;
import bean.ToEmployerToTalkBean;
import bean.ToJumpWorkerBean;
import bean.ToTalkWorkerBean;
import bean.WorkerBean;
import config.IntentConfig;
import config.NetConfig;
import employertotalk.presenter.EmployerToTalkPresenter;
import employertotalk.presenter.IEmployerToTalkPresenter;
import listener.IdPosClickHelp;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import talkworker.view.TalkWorkerActivity;
import utils.DataUtils;
import utils.Utils;
import view.CProgressDialog;

public class EmployerToTalkActivity extends AppCompatActivity implements IEmployerToTalkActivity, View.OnClickListener, IdPosClickHelp, PullToRefreshLayout.OnRefreshListener {

    private View rootView;
    private RelativeLayout returnRl;
    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private CProgressDialog cpd;
    private List<EmployerToTalkBean> employerToTalkBeanList = new ArrayList<>();
    private EmployerToTalkAdapter employerToTalkAdapter;
    private IEmployerToTalkPresenter employerToTalkPresenter;

    private final int LOAD_SUCCESS = 1;
    private final int LOAD_FAILURE = 2;
    private final int SKILL_SUCCESS = 3;
    private final int SKILL_FAILURE = 4;
    private int clickPosition;

    private ToEmployerToTalkBean toEmployerToTalkBean;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case LOAD_SUCCESS:
                        employerToTalkPresenter.skill(NetConfig.skillUrl);
                        break;
                    case LOAD_FAILURE:
                        break;
                    case SKILL_SUCCESS:
                        notifyData();
                        break;
                    case SKILL_FAILURE:
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(EmployerToTalkActivity.this).inflate(R.layout.activity_employer_to_talk, null);
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
            handler.removeMessages(LOAD_SUCCESS);
            handler.removeMessages(LOAD_FAILURE);
            handler.removeMessages(SKILL_SUCCESS);
            handler.removeMessages(SKILL_FAILURE);
            handler = null;
        }
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_employer_to_talk_return);
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
        cpd = Utils.initProgressDialog(EmployerToTalkActivity.this, cpd);
    }

    private void initData() {
        employerToTalkAdapter = new EmployerToTalkAdapter(EmployerToTalkActivity.this, employerToTalkBeanList, this);
        employerToTalkPresenter = new EmployerToTalkPresenter(this);
        toEmployerToTalkBean = (ToEmployerToTalkBean) getIntent().getSerializableExtra(IntentConfig.toEmployerToTalk);
    }

    private void setData() {
        plv.setAdapter(employerToTalkAdapter);
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        ptrl.setOnRefreshListener(this);
    }

    private void loadData() {
        cpd.show();
        employerToTalkPresenter.load(NetConfig.taskBaseUrl + "?action=info&t_id=" + toEmployerToTalkBean.getTaskId());
    }

    private void notifyData() {
        cpd.dismiss();
        employerToTalkAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_employer_to_talk_return:
                finish();
                break;
        }
    }

    @Override
    public void loadSuccess(String json) {
        Utils.log(EmployerToTalkActivity.this, json);
        Utils.log(EmployerToTalkActivity.this, DataUtils.getEmployerToTalkBeanList(json).toString());
        List<EmployerToTalkBean> list = new ArrayList<>();
        list.addAll(DataUtils.getEmployerToTalkBeanList(json));
        for (int i = 0; i < list.size(); i++) {
            String status = list.get(i).getWorkerStatus();
            if (TextUtils.isEmpty(status) || status.equals("null")) {
                employerToTalkBeanList.add(list.get(i));
            } else if (status.equals("0")) {
                employerToTalkBeanList.add(list.get(i));
            }
        }
        handler.sendEmptyMessage(LOAD_SUCCESS);
    }

    @Override
    public void loadFailure(String failure) {

    }

    @Override
    public void skillSuccess(String json) {
        for (int i = 0; i < employerToTalkBeanList.size(); i++) {
            List<String> skillIdList = new ArrayList<>();
            skillIdList.add(employerToTalkBeanList.get(i).getSkillId());
            employerToTalkBeanList.get(i).setSkillName(DataUtils.getSkillNameList(json, skillIdList).get(0));
            handler.sendEmptyMessage(SKILL_SUCCESS);
        }
    }

    @Override
    public void skillFailure(String failure) {

    }

    @Override
    public void onClick(int id, int pos) {
        clickPosition = pos;
        switch (id) {
            case R.id.ll_item_employer_to_talk_type_1:
                Intent waitIntent = new Intent(EmployerToTalkActivity.this, TalkWorkerActivity.class);
                ToJumpWorkerBean toJumpWorkerBean = new ToJumpWorkerBean();
                toJumpWorkerBean.setWorkerId(employerToTalkBeanList.get(clickPosition).getWorkerId());
                waitIntent.putExtra(IntentConfig.toJumpWorker, toJumpWorkerBean);
                startActivity(waitIntent);
                break;
            case R.id.iv_item_employer_to_talk_type_1_worker_phone:
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + employerToTalkBeanList.get(clickPosition).getMobile()));
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(i);
                }
                break;
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


}
