package com.gjzg.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import accountdetail.adapter.DetailAdapter;
import accountdetail.bean.AccountDetailBean;
import accountdetail.presenter.AccountDetailPresenter;
import accountdetail.presenter.IAccountDetailPresenter;
import accountdetail.view.IAccountDetailActivity;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import utils.UrlUtils;
import utils.Utils;
import view.CProgressDialog;

public class AccountDetailActivity extends AppCompatActivity implements IAccountDetailActivity, View.OnClickListener, PullToRefreshLayout.OnRefreshListener {

    private View rootView, emptyView, netView;
    private RelativeLayout returnRl;
    private LinearLayout menuLl;
    private View menuPopView;
    private FrameLayout fl;
    private TextView netTv;
    private PopupWindow menuPopWindow;
    private RelativeLayout menuAllRl, menuOutRl, menuInRl;
    private TextView menuContentTv;
    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private List<AccountDetailBean> list;
    private DetailAdapter adapter;
    private CProgressDialog cProgressDialog;

    private final int ALL = 0, WITHDRAW = 1, RECHARGE = 2;
    private int LOG_STATE = ALL;
    private final int FIRST = 0, REFRESH = 1;
    private int STATE = FIRST;
    private IAccountDetailPresenter accountDetailPresenter;

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
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_detail, null);
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
            handler.removeMessages(1);
            handler = null;
        }
    }

    private void initView() {
        initRootView();
        initEmptyView();
        initPopWindowView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_detail_return);
        menuLl = (LinearLayout) rootView.findViewById(R.id.ll_detail_menu);
        menuContentTv = (TextView) rootView.findViewById(R.id.tv_detail_menu_content);
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
        cProgressDialog = Utils.initProgressDialog(AccountDetailActivity.this, cProgressDialog);
    }

    private void initEmptyView() {
        fl = (FrameLayout) rootView.findViewById(R.id.fl);
        emptyView = LayoutInflater.from(this).inflate(R.layout.empty_data, null);
        fl.addView(emptyView);
        emptyView.setVisibility(View.GONE);
        netView = LayoutInflater.from(this).inflate(R.layout.empty_net, null);
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

    private void initPopWindowView() {
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

    private void initData() {
        accountDetailPresenter = new AccountDetailPresenter(this);
        list = new ArrayList<>();
        adapter = new DetailAdapter(this, list);
    }

    private void setData() {
        plv.setAdapter(adapter);
    }

    private void setListener() {
        ptrl.setOnRefreshListener(this);
        returnRl.setOnClickListener(this);
        menuLl.setOnClickListener(this);
        menuAllRl.setOnClickListener(this);
        menuOutRl.setOnClickListener(this);
        menuInRl.setOnClickListener(this);
    }

    private void loadData() {
        switch (STATE) {
            case FIRST:
                cProgressDialog.show();
                break;
        }
        String url = UrlUtils.getUsersFundsLogUrl(AccountDetailActivity.this, LOG_STATE);
        accountDetailPresenter.load(url);
    }

    private void notifyData() {
        switch (STATE) {
            case FIRST:
                cProgressDialog.dismiss();
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
        adapter.notifyDataSetChanged();
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_detail_return:
                finish();
                break;
            case R.id.ll_detail_menu:
                if (!menuPopWindow.isShowing()) {
                    menuPopWindow.showAsDropDown(menuLl, -20, -10);
                    backgroundAlpha(0.8f);
                }
                break;
            case R.id.rl_popwindow_detail_menu_all:
                if (menuPopWindow.isShowing()) {
                    menuPopWindow.dismiss();
                    menuContentTv.setText("全部");
                    LOG_STATE = ALL;
                    STATE = FIRST;
                    loadData();
                }
                break;
            case R.id.rl_popwindow_detail_menu_out:
                if (menuPopWindow.isShowing()) {
                    menuPopWindow.dismiss();
                    menuContentTv.setText("支出");
                    LOG_STATE = RECHARGE;
                    STATE = FIRST;
                    loadData();
                }
                break;
            case R.id.rl_popwindow_detail_menu_in:
                if (menuPopWindow.isShowing()) {
                    menuPopWindow.dismiss();
                    menuContentTv.setText("收入");
                    LOG_STATE = WITHDRAW;
                    STATE = FIRST;
                    loadData();
                }
                break;
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

    @Override
    public void showSuccess(List<AccountDetailBean> accountDetailBeanList) {
        Utils.log(AccountDetailActivity.this, accountDetailBeanList.toString());
        list.clear();
        list.addAll(accountDetailBeanList);
        handler.sendEmptyMessage(1);
    }

    @Override
    public void showFailure(String failure) {

    }
}
