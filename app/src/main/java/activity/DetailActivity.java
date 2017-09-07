package activity;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.DetailAdapter;
import bean.DetailBean;
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

public class DetailActivity extends CommonActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener {

    //根视图
    private View rootView;
    //返回视图
    private RelativeLayout returnRl;
    //菜单视图
    private LinearLayout menuLl;
    //弹窗视图
    private View menuPopView;
    private PopupWindow menuPopWindow;
    private RelativeLayout menuAllRl, menuOutRl, menuInRl;
    private TextView menuContentTv;
    //无网络视图
    private LinearLayout noNetLl;
    private TextView noNetTv;
    //无数据视图
    private LinearLayout noDataLl;
    //加载对话框视图
    private CProgressDialog cPd;
    //刷新加载布局
    private PullToRefreshLayout pTrl;
    //刷新加载ListView
    private PullableListView pLv;
    //明细数据类集合
    private List<DetailBean> detailBeanList;
    //明细数据适配器
    private DetailAdapter detailAdapter;
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
        //初始化根布局
        return rootView = LayoutInflater.from(this).inflate(R.layout.activity_detail, null);
    }

    @Override
    protected void initView() {
        initRootView();
        initDialogView();
        initPopWindowView();
    }

    private void initRootView() {
        //初始化返回视图
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_detail_return);
        //初始化菜单视图
        menuLl = (LinearLayout) rootView.findViewById(R.id.ll_detail_menu);
        menuContentTv = (TextView) rootView.findViewById(R.id.tv_detail_menu_content);
        //初始化无网络视图
        noNetLl = (LinearLayout) rootView.findViewById(R.id.ll_no_net);
        noNetTv = (TextView) rootView.findViewById(R.id.tv_no_net_refresh);
        //初始化无数据视图
        noDataLl = (LinearLayout) rootView.findViewById(R.id.ll_no_data);
        //初始化刷新加载布局
        pTrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        //初始化刷新加载ListView
        pLv = (PullableListView) rootView.findViewById(R.id.plv);
    }

    private void initDialogView() {
        //初始化加载对话框视图
        cPd = new CProgressDialog(this, R.style.dialog_cprogress);
    }

    private void initPopWindowView() {
        //初始化弹窗视图
        menuPopView = LayoutInflater.from(this).inflate(R.layout.popwindow_detail_menu, null);
        menuAllRl = (RelativeLayout) menuPopView.findViewById(R.id.rl_popwindow_detail_menu_all);
        menuOutRl = (RelativeLayout) menuPopView.findViewById(R.id.rl_popwindow_detail_menu_out);
        menuInRl = (RelativeLayout) menuPopView.findViewById(R.id.rl_popwindow_detail_menu_in);
        menuPopWindow = new PopupWindow(menuPopView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        menuPopWindow.setAnimationStyle(R.style.popwin_anim_style);
        menuPopWindow.setFocusable(true);
        menuPopWindow.setTouchable(true);
        menuPopWindow.setOutsideTouchable(true);
        menuPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
    }

    @Override
    protected void initData() {
        //初始化明细数据类集合
        detailBeanList = new ArrayList<>();
        //初始化明细数据适配器
        detailAdapter = new DetailAdapter(this, detailBeanList);
        //初始化okHttpClient
        okHttpClient = new OkHttpClient();
        //初始化加载状态
        state = StateConfig.LOAD_DONE;
    }

    @Override
    protected void setData() {
        //绑定明细数据适配器
        pLv.setAdapter(detailAdapter);
    }

    @Override
    protected void setListener() {
        //返回视图监听
        returnRl.setOnClickListener(this);
        //菜单视图监听
        menuLl.setOnClickListener(this);
        //菜单全部监听
        menuAllRl.setOnClickListener(this);
        //菜单支出监听
        menuOutRl.setOnClickListener(this);
        //菜单收入监听
        menuInRl.setOnClickListener(this);
        //无网络刷新监听
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
                        detailBeanList.clear();
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
                DetailBean d0 = new DetailBean();
                d0.setTitle("支出");
                d0.setDes("dfdfd");
                d0.setTime("derer");
                d0.setBalance("gtfbrt");
                detailBeanList.add(d0);
                handler.sendEmptyMessage(StateConfig.LOAD_DONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveToLocalData(String json) {

    }

    private void notifyNoNet() {
        switch (state) {
            case StateConfig.LOAD_DONE:
                cPd.dismiss();
                if (detailBeanList.size() == 0) {
                    noNetLl.setVisibility(View.VISIBLE);
                    noDataLl.setVisibility(View.GONE);
                    pTrl.setVisibility(View.GONE);
                }
                break;
            case StateConfig.LOAD_REFRESH:
                break;
            case StateConfig.LOAD_LOAD:
                break;
        }
    }

    private void notifyData() {
        switch (state) {
            case StateConfig.LOAD_DONE:
                cPd.dismiss();
                if (detailBeanList.size() == 0) {
                    noDataLl.setVisibility(View.VISIBLE);
                    noNetLl.setVisibility(View.GONE);
                    pTrl.setVisibility(View.GONE);
                }
                break;
            case StateConfig.LOAD_REFRESH:
                break;
            case StateConfig.LOAD_LOAD:
                break;
        }
        detailAdapter.notifyDataSetChanged();
    }


    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回视图点击事件
            case R.id.rl_detail_return:
                finish();
                break;
            //菜单视图点击事件
            case R.id.ll_detail_menu:
                if (!menuPopWindow.isShowing()) {
                    menuPopWindow.showAsDropDown(menuLl, -10, 10);
                    backgroundAlpha(0.8f);
                }
                break;
            //菜单全部点击事件
            case R.id.rl_popwindow_detail_menu_all:
                if (menuPopWindow.isShowing()) {
                    menuPopWindow.dismiss();
                    menuContentTv.setText("全部");
                }
                break;
            //菜单支出点击事件
            case R.id.rl_popwindow_detail_menu_out:
                if (menuPopWindow.isShowing()) {
                    menuPopWindow.dismiss();
                    menuContentTv.setText("支出");
                }
                break;
            //菜单收入点击事件
            case R.id.rl_popwindow_detail_menu_in:
                if (menuPopWindow.isShowing()) {
                    menuPopWindow.dismiss();
                    menuContentTv.setText("收入");
                }
                break;
            //无网络刷新点击事件
            case R.id.tv_no_net_refresh:
                noNetLl.setVisibility(View.GONE);
                pTrl.setVisibility(View.VISIBLE);
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
