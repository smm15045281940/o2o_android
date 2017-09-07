package fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import view.CProgressDialog;

/**
 * 创建日期：2017/8/8 on 11:07
 * 作者:孙明明
 * 描述:系统消息
 */

public class SysMsgFrag extends CommonFragment implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener {

    //根视图
    private View rootView;
    //无网络视图
    private LinearLayout noNetLl;
    private TextView noNetTv;
    //无数据视图
    private LinearLayout noDataLl;
    //刷新加载布局
    private PullToRefreshLayout sysMsgPtrl;
    //刷新加载ListView
    private PullableListView sysMsgLv;
    //加载对话框视图
    private CProgressDialog cPd;
    //系统消息集合
    private List<MsgBean> msgBeanList;
    //系统消息适配器
    private MsgAdapter msgAdapter;
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
        //初始化无网络视图
        noNetLl = (LinearLayout) rootView.findViewById(R.id.ll_no_net);
        noNetTv = (TextView) rootView.findViewById(R.id.tv_no_net_refresh);
        //初始化无数据视图
        noDataLl = (LinearLayout) rootView.findViewById(R.id.ll_no_data);
        //初始化刷新加载布局
        sysMsgPtrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        //初始化刷新加载ListView
        sysMsgLv = (PullableListView) rootView.findViewById(R.id.plv);
    }

    private void initDialogView() {
        //初始化加载对话框视图
        cPd = new CProgressDialog(getActivity(), R.style.dialog_cprogress);
    }

    @Override
    protected void initData() {
        //初始化系统消息集合
        msgBeanList = new ArrayList<>();
        //初始化系统消息适配器
        msgAdapter = new MsgAdapter(getActivity(), msgBeanList);
        //初始化okHttpClient
        okHttpClient = new OkHttpClient();
    }

    @Override
    protected void setData() {
        //绑定系统消息适配器
        sysMsgLv.setAdapter(msgAdapter);
    }

    @Override
    protected void setListener() {
        //无网络刷新适配器
        noNetTv.setOnClickListener(this);
        //刷新加载布局监听
        sysMsgPtrl.setOnRefreshListener(this);
    }

    @Override
    protected void loadData() {
        cPd.show();
        loadNetData();
    }

    private void loadNetData() {
        Request sysMsgRequest = new Request.Builder().url(NetConfig.testUrl).get().build();
        okHttpClient.newCall(sysMsgRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(StateConfig.LOAD_NO_NET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (state == StateConfig.LOAD_REFRESH) {
                        msgBeanList.clear();
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
                MsgBean sm0 = new MsgBean();
                sm0.setTitle("系统消息");
                sm0.setDate("2017/03/05");
                sm0.setDes("系统升级，钢建众工2.0版本全新上市，更多高薪工作等你来");
                sm0.setArrowShow(false);
                MsgBean sm1 = new MsgBean();
                sm1.setTitle("系统消息");
                sm1.setDate("2017/03/04");
                sm1.setDes("你有新的红包等待领取");
                sm1.setArrowShow(false);
                msgBeanList.add(sm0);
                msgBeanList.add(sm1);
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
                if (msgBeanList.size() == 0) {
                    noNetLl.setVisibility(View.VISIBLE);
                    noDataLl.setVisibility(View.GONE);
                }
                break;
            case StateConfig.LOAD_REFRESH:
                break;
            case StateConfig.LOAD_LOAD:
                break;
        }
    }

    private void notifyData() {
        msgAdapter.notifyDataSetChanged();
        switch (state) {
            case StateConfig.LOAD_DONE:
                cPd.dismiss();
                if (msgBeanList.size() == 0) {
                    noDataLl.setVisibility(View.VISIBLE);
                    noNetLl.setVisibility(View.GONE);
                }
                break;
            case StateConfig.LOAD_REFRESH:
                break;
            case StateConfig.LOAD_LOAD:
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
}
