package fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjzg.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.DcAdapter;
import bean.Dc;
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
 * 创建日期：2017/7/28 on 13:53
 * 作者:孙明明
 * 描述:优惠信息
 */

public class DiscountFrag extends CommonFragment implements AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener, View.OnClickListener {

    //根视图
    private View rootView;
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
    //优惠信息数据类集合
    private List<Dc> dcList;
    //优惠信息数据适配器
    private DcAdapter dcAdapter;
    //okHttpClient
    private OkHttpClient okHttpClient;
    //加载状态
    private int state;

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
                    default:
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
        return rootView = LayoutInflater.from(getActivity()).inflate(R.layout.frag_discount, null);
    }

    @Override
    protected void initView() {
        initRootView();
        initDialogView();
    }

    private void initRootView() {
        //初始化无网络视图
        noNetLl = (LinearLayout) rootView.findViewById(R.id.ll_no_net);
        noNetTv = (TextView) rootView.findViewById(R.id.tv_no_net_refresh);
        //初始化无数据视图
        noDataLl = (LinearLayout) rootView.findViewById(R.id.ll_no_data);
        //初始化刷新加载布局
        pTrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl_frag_discount);
        //初始化刷新加载ListView
        pLv = (PullableListView) rootView.findViewById(R.id.plv_frag_discount);
    }

    private void initDialogView() {
        //初始化加载对话框
        cPd = new CProgressDialog(getActivity(), R.style.dialog_cprogress);
    }

    @Override
    protected void initData() {
        //初始化优惠数据类集合
        dcList = new ArrayList<>();
        //初始化优惠适配器
        dcAdapter = new DcAdapter(getActivity(), dcList);
        //初始化okHttpClient
        okHttpClient = new OkHttpClient();
        //初始化加载状态
        state = StateConfig.LOAD_DONE;
    }

    @Override
    protected void setData() {
        //绑定优惠适配器
        pLv.setAdapter(dcAdapter);
    }

    @Override
    protected void setListener() {
        //绑定刷新加载ListView项点击监听
        pLv.setOnItemClickListener(this);
        //刷新加载布局监听
        pTrl.setOnRefreshListener(this);
        //无网络刷新视图点击事件
        noNetTv.setOnClickListener(this);
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

    private void saveLocalData(String json) {

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
                        dcList.clear();
                    }
                    String result = response.body().string();
                    saveLocalData(result);
                    parseJson(result);
                }
            }
        });
    }

    private void parseJson(String json) {
        try {
            JSONObject objBean = new JSONObject(json);
            if (objBean.optInt("code") == 200) {
                for (int i = 0; i < 3; i++) {
                    Dc d = new Dc();
                    d.setTitle("百度");
                    d.setUrl("https://www.baidu.com/");
                    dcList.add(d);
                }
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
                if (dcList.size() == 0) {
                    noNetLl.setVisibility(View.VISIBLE);
                    noDataLl.setVisibility(View.GONE);
                }
                break;
            case StateConfig.LOAD_REFRESH:
                pTrl.refreshFinish(PullToRefreshLayout.FAIL);
                break;
            case StateConfig.LOAD_LOAD:
                pTrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                break;
            default:
                break;
        }
    }

    private void notifyData() {
        switch (state) {
            case StateConfig.LOAD_DONE:
                cPd.dismiss();
                if (dcList.size() == 0) {
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
        dcAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            //刷新加载ListView项点击事件
            case R.id.plv_frag_discount:
                Utils.toast(getActivity(), dcList.get(position).getUrl());
                break;
            default:
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_no_net_refresh:
                noNetLl.setVisibility(View.GONE);
                loadNetData();
                break;
            default:
                break;
        }
    }
}
