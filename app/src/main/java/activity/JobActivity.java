package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import adapter.JobAdapter;
import bean.Job;
import listener.OnRefreshListener;
import view.CListView;

public class JobActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshListener, AdapterView.OnItemClickListener {

    private View rootView;
    private ImageView returnIv;
    private CListView cListView;

    private List<Job> jobList = new ArrayList<>();
    private JobAdapter jobAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_job, null);
        setContentView(rootView);
        initView();
        initData();
        setData();
        setListener();
        loadData();
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        returnIv = (ImageView) rootView.findViewById(R.id.iv_job_return);
        cListView = (CListView) rootView.findViewById(R.id.clv_job);
    }

    private void initData() {
        jobAdapter = new JobAdapter(this, jobList);
    }

    private void setData() {
        cListView.setAdapter(jobAdapter);
    }

    private void setListener() {
        returnIv.setOnClickListener(this);
        cListView.setOnRefreshListener(this);
        cListView.setOnItemClickListener(this);
    }

    private void loadData() {
        for (int i = 0; i < 10; i++) {
            Job j = new Job();
            j.setName("工种：" + i);
            jobList.add(j);
        }
        jobAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_job_return:
                finish();
                break;
        }
    }

    @Override
    public void onDownPullRefresh() {
        cListView.hideHeadView();
    }

    @Override
    public void onLoadingMore() {
        cListView.hideFootView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, WorkerActivity.class);
        intent.putExtra("job", jobList.get(position - 1).getName());
        startActivity(intent);
    }
}
