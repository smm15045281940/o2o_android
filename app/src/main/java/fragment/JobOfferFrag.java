package fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjzg.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.MsgAdapter;
import bean.MsgBean;
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

/**
 * 创建日期：2017/8/8 on 11:06
 * 作者:孙明明
 * 描述:工作邀约
 */

public class JobOfferFrag extends CommonFragment implements View.OnClickListener, AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {

    //根视图
    private View rootView;
    //无网络视图
    private LinearLayout noNetLl;
    private TextView noNetTv;
    //无数据视图
    private LinearLayout noDataLl;
    //刷新加载布局
    private PullToRefreshLayout jobOfferPtrl;
    //刷新加载ListView
    private PullableListView jobOfferLv;
    //加载对话框视图
    private CProgressDialog cPd;
    //工作邀约数据类集合
    private List<MsgBean> jobOfferList;
    //工作邀约数据适配器
    private MsgAdapter jobOfferAdapter;
    //okHttpClient
    private OkHttpClient okHttpClient;
    //加载状态
    private int state = StateConfig.LOAD_DONE;
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
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(StateConfig.LOAD_NO_NET);
        handler.removeMessages(StateConfig.LOAD_DONE);
    }

    @Override
    protected View getRootView() {
        //初始化根布局
        return rootView = LayoutInflater.from(getActivity()).inflate(R.layout.common_listview, null);
    }

    @Override
    protected void initView() {
        initRootView();
        initDialogView();
    }

    private void initRootView() {
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //初始化刷新加载布局
        jobOfferPtrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl_common_listview);
        //初始化刷新加载ListView
        jobOfferLv = (PullableListView) rootView.findViewById(R.id.plv_common_listview);
        //初始化无网络视图
        noNetLl = (LinearLayout) rootView.findViewById(R.id.ll_no_net);
        noNetTv = (TextView) rootView.findViewById(R.id.tv_no_net_refresh);
        //初始化无数据视图
        noDataLl = (LinearLayout) rootView.findViewById(R.id.ll_no_data);
    }

    private void initDialogView() {
        //初始化加载对话框
        cPd = new CProgressDialog(getActivity(), R.style.dialog_cprogress);
    }

    @Override
    protected void initData() {
        //初始化工作邀约数据类集合
        jobOfferList = new ArrayList<>();
        //初始化工作邀约数据适配器
        jobOfferAdapter = new MsgAdapter(getActivity(), jobOfferList);
        //初始化okHttpClient
        okHttpClient = new OkHttpClient();
    }

    @Override
    protected void setData() {
        //绑定工作邀约数据适配器
        jobOfferLv.setAdapter(jobOfferAdapter);
    }

    @Override
    protected void setListener() {
        //无网络刷新监听
        noNetTv.setOnClickListener(this);
        //工作邀约项点击监听
        jobOfferLv.setOnItemClickListener(this);
    }

    @Override
    protected void loadData() {
        cPd.show();
        loadNetData();
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
                    if (state == StateConfig.LOAD_REFRESH) {
                        jobOfferList.clear();
                    }
                    String result = response.body().string();
                    parseJson(result);
                }
            }
        });
    }

    private void parseJson(String json) {
        try {
            JSONObject objBean = new JSONObject(json);
            if (objBean.optInt("code") == 200) {
                MsgBean mo0 = new MsgBean();
                mo0.setTitle("工作邀约");
                mo0.setDate("2017/03/07");
                mo0.setDes("有一份适合你的工作，雇主已经向您发起邀约，等待您同意");
                mo0.setArrowShow(true);
                MsgBean mo1 = new MsgBean();
                mo1.setTitle("工作邀约");
                mo1.setDate("2017/03/06");
                mo1.setDes("您发布的工作有工人感兴趣并向您发起邀约，等待您同意");
                mo1.setArrowShow(true);
                jobOfferList.add(mo0);
                jobOfferList.add(mo1);
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
                if (jobOfferList.size() == 0) {
                    noNetLl.setVisibility(View.VISIBLE);
                    noDataLl.setVisibility(View.GONE);
                }
                break;
            case StateConfig.LOAD_REFRESH:
                jobOfferPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                break;
            case StateConfig.LOAD_LOAD:
                jobOfferPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                break;
        }
    }

    private void notifyData() {
        jobOfferAdapter.notifyDataSetChanged();
        switch (state) {
            case StateConfig.LOAD_DONE:
                cPd.dismiss();
                break;
            case StateConfig.LOAD_REFRESH:
                jobOfferPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                break;
            case StateConfig.LOAD_LOAD:
                jobOfferPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_no_net_refresh:
                loadNetData();
                break;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Utils.toast(getActivity(), "你点击了第" + (position + 1) + "个" + jobOfferList.get(position).getTitle());
    }
}
