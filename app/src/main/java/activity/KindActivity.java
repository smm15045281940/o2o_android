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

import adapter.KindAdapter;
import bean.KindBean;
import config.NetConfig;
import config.StateConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import view.CProgressDialog;

public class KindActivity extends CommonActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    //根视图
    private View rootView;
    //返回视图
    private RelativeLayout returnRl;
    //无网络视图
    private LinearLayout noNetLl;
    private TextView noNetTv;
    //无数据视图
    private LinearLayout noDataLl;
    //刷新加载布局
    private PullToRefreshLayout pTrl;
    //刷新加载ListView
    private PullableListView pLv;
    //加载对话框视图
    private CProgressDialog cPd;
    //种类数据类集合
    private List<KindBean> kindBeanList;
    //种类数据适配器
    private KindAdapter kindAdapter;
    //okHttpClient
    private OkHttpClient okHttpClient;
    //加载状态
    private int state;
    //handler
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
        return rootView = LayoutInflater.from(this).inflate(R.layout.activity_kind, null);
    }

    @Override
    protected void initView() {
        initRootView();
        initDialogView();
    }

    private void initRootView() {
        //初始化返回视图
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_kind_return);
        //初始化无网络视图
        noNetLl = (LinearLayout) rootView.findViewById(R.id.ll_no_net);
        noNetTv = (TextView) rootView.findViewById(R.id.tv_no_net_refresh);
        //初始化无数据视图
        noDataLl = (LinearLayout) rootView.findViewById(R.id.ll_no_data);
        //初始化刷新加载布局
        pTrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl_common_listview);
        //初始化刷新加载ListView
        pLv = (PullableListView) rootView.findViewById(R.id.plv_common_listview);
    }

    private void initDialogView() {
        //初始化对话框
        cPd = new CProgressDialog(this, R.style.dialog_cprogress);
    }

    @Override
    protected void initData() {
        //初始化种类数据类集合集合
        kindBeanList = new ArrayList<>();
        //初始化种类数据适配器
        kindAdapter = new KindAdapter(this, kindBeanList);
        //初始化okHttpClient
        okHttpClient = new OkHttpClient();
        //初始化加载状态
        state = StateConfig.LOAD_DONE;
    }

    @Override
    protected void setData() {
        //绑定种类数据适配器
        pLv.setAdapter(kindAdapter);
    }

    @Override
    protected void setListener() {
        //返回视图监听
        returnRl.setOnClickListener(this);
        //无网络刷新监听
        noNetTv.setOnClickListener(this);
        //刷新加载布局监听
        pTrl.setOnRefreshListener(this);
        //刷新加载ListView项点击监听
        pLv.setOnItemClickListener(this);
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
                        kindBeanList.clear();
                    }
                    parseJson(result);
                }
            }
        });
    }

    private void parseJson(String json) {
        try {
            JSONObject objBean = new JSONObject(json);
            if (objBean.optInt("code") == 200) {
                KindBean k0 = new KindBean();
                k0.setName("水泥工");
                KindBean k1 = new KindBean();
                k1.setName("搬运工");
                KindBean k2 = new KindBean();
                k2.setName("焊接工");
                KindBean k3 = new KindBean();
                k3.setName("xx工");
                KindBean k4 = new KindBean();
                k4.setName("xx工");
                KindBean k5 = new KindBean();
                k5.setName("xx工");
                kindBeanList.add(k0);
                kindBeanList.add(k1);
                kindBeanList.add(k2);
                kindBeanList.add(k3);
                kindBeanList.add(k4);
                kindBeanList.add(k5);
                handler.sendEmptyMessage(StateConfig.LOAD_DONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void notifyNoNet() {
        switch (state) {
            case StateConfig.LOAD_DONE:
                cPd.dismiss();
                if (kindBeanList.size() == 0) {
                    noNetLl.setVisibility(View.VISIBLE);
                    pTrl.setVisibility(View.GONE);
                    noDataLl.setVisibility(View.GONE);
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
                if (kindBeanList.size() == 0) {
                    noDataLl.setVisibility(View.VISIBLE);
                    noNetLl.setVisibility(View.GONE);
                }
                break;
            case StateConfig.LOAD_REFRESH:
                pTrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                break;
            case StateConfig.LOAD_LOAD:
                pTrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                break;
        }
        kindAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回视图点击事件
            case R.id.rl_kind_return:
                finish();
                break;
            //无网络刷新视图点击事件
            case R.id.tv_no_net_refresh:
                noNetLl.setVisibility(View.GONE);
                pTrl.setVisibility(View.VISIBLE);
                loadNetData();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, WorkerActivity.class);
        intent.putExtra("kindBean", kindBeanList.get(position));
        startActivity(intent);
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
