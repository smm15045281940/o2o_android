package activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.PersonAdapter;
import bean.KindBean;
import bean.PersonBean;
import bean.ScreenBean;
import config.CodeConfig;
import config.NetConfig;
import config.StateConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import utils.Utils;
import view.CProgressDialog;

public class WorkerActivity extends CommonActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    //根视图
    private View rootView;
    //返回视图
    private RelativeLayout returnRl;
    //无网络视图
    private LinearLayout noNetLl;
    private TextView noNetTv;
    //无数据视图
    private LinearLayout noDataLl;
    //筛选视图
    private RelativeLayout screenRl;
    //刷新加载布局
    private PullToRefreshLayout pTrl;
    //刷新加载ListView
    private PullableListView pLv;
    //加载对话框视图
    private CProgressDialog cPd;
    //工人信息数据类集合
    private List<PersonBean> personBeanList;
    //工人信息数据适配器
    private PersonAdapter personAdapter;
    //okHttpClient
    private OkHttpClient okHttpClient;
    //加载状态
    private int state;

    private KindBean kindBean;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case StateConfig.LOAD_NO_NET:
                        notifyNoNet();
                        break;
                    case StateConfig.LOAD_DONE:
                        notifyData();
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(StateConfig.LOAD_NO_NET);
        handler.removeMessages(StateConfig.LOAD_DONE);
    }

    @Override
    protected View getRootView() {
        //初始化根视图
        return rootView = LayoutInflater.from(this).inflate(R.layout.activity_worker, null);
    }

    @Override
    protected void initView() {
        initRootView();
        initDialogView();
    }

    private void initRootView() {
        //初始化返回视图
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_worker_return);
        //初始化无网络视图
        noNetLl = (LinearLayout) rootView.findViewById(R.id.ll_no_net);
        noNetTv = (TextView) rootView.findViewById(R.id.tv_no_net_refresh);
        //初始化无数据视图
        noDataLl = (LinearLayout) rootView.findViewById(R.id.ll_no_data);
        //初始化筛选视图
        screenRl = (RelativeLayout) rootView.findViewById(R.id.rl_worker_screen);
        //初始化刷新加载布局
        pTrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl_common_listview);
        //初始化刷新加载ListView
        pLv = (PullableListView) rootView.findViewById(R.id.plv_common_listview);
    }

    private void initDialogView() {
        //初始化加载对话框
        cPd = new CProgressDialog(this, R.style.dialog_cprogress);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        kindBean = (KindBean) intent.getSerializableExtra("kindBean");
        //初始化工人信息数据类集合
        personBeanList = new ArrayList<>();
        //初始化工人信息数据适配器
        personAdapter = new PersonAdapter(this, personBeanList);
        //初始化okHttpClient
        okHttpClient = new OkHttpClient();
        //初始化加载状态
        state = StateConfig.LOAD_DONE;
    }

    @Override
    protected void setData() {
        //绑定工人信息数据适配器
        pLv.setAdapter(personAdapter);
    }

    @Override
    protected void setListener() {
        //返回视图监听
        returnRl.setOnClickListener(this);
        //筛选视图监听
        screenRl.setOnClickListener(this);
        //无网络刷新视图监听
        noNetTv.setOnClickListener(this);
        //刷新加载布局监听
        pTrl.setOnRefreshListener(this);
    }

    @Override
    protected void loadData() {
        if (checkLocalData()) {
            loadLocalData();
        } else {
            cPd.show();
            loadNetData();
        }
    }

    private boolean checkLocalData() {
        return false;
    }

    private void loadLocalData() {
    }

    private void loadNetData() {
        Request request = new Request.Builder().url(NetConfig.testUrl).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(StateConfig.LOAD_NO_NET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    if (state == StateConfig.LOAD_REFRESH) {
                        personBeanList.clear();
                    }
                    parseJson(result);
                }
            }
        });
    }

    private void notifyNoNet() {
        switch (state) {
            case StateConfig.LOAD_DONE:
                cPd.dismiss();
                if (personBeanList.size() == 0) {
                    noNetLl.setVisibility(View.VISIBLE);
                    noDataLl.setVisibility(View.GONE);
                    pTrl.setVisibility(View.GONE);
                }
                break;
            case StateConfig.LOAD_REFRESH:
                pTrl.refreshFinish(PullToRefreshLayout.FAIL);
                break;
            case StateConfig.LOAD_LOAD:
                pTrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                break;
        }
    }

    private void notifyData() {
        switch (state) {
            case StateConfig.LOAD_DONE:
                cPd.dismiss();
                if (personBeanList.size() == 0) {
                    noNetLl.setVisibility(View.GONE);
                    noDataLl.setVisibility(View.VISIBLE);
                    pTrl.setVisibility(View.GONE);
                }
                break;
            case StateConfig.LOAD_REFRESH:
                pTrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                break;
            case StateConfig.LOAD_LOAD:
                pTrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                break;
        }
        personAdapter.notifyDataSetChanged();
    }

    private void parseJson(String json) {
        try {
            JSONObject objBean = new JSONObject(json);
            if (objBean.optInt("code") == 200) {
                for (int i = 0; i < 5; i++) {
                    PersonBean personBean = new PersonBean();
                    personBean.setImage("");
                    personBean.setName(kindBean.getName() + "-" + i);
                    personBean.setPlay("精通刮大白");
                    personBean.setShow("十年刮大白经验");
                    personBean.setState(1);
                    personBean.setCollect(false);
                    personBean.setDistance("距离3公里");
                    personBeanList.add(personBean);
                }
                handler.sendEmptyMessage(StateConfig.LOAD_DONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回视图点击事件
            case R.id.rl_worker_return:
                finish();
                break;
            //筛选视图点击事件
            case R.id.rl_worker_screen:
                startActivityForResult(new Intent(this, WorkerScnActivity.class), CodeConfig.screenRequestCode);
                break;
            case R.id.tv_no_net_refresh:
                state = StateConfig.LOAD_REFRESH;
                loadNetData();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, TalkActivity.class);
        intent.putExtra("worker", personBeanList.get(position - 1));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodeConfig.screenRequestCode && resultCode == CodeConfig.screenResultCode && data != null) {
            ScreenBean screenBean = (ScreenBean) data.getSerializableExtra("screenBean");
            if (screenBean != null) {
                int a = screenBean.getState();
                Utils.toast(this, a + "");
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        state = StateConfig.LOAD_REFRESH;
        loadNetData();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        state = StateConfig.LOAD_LOAD;
        loadNetData();
    }
}
