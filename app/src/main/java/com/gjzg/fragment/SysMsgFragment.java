package com.gjzg.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gjzg.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.MsgAdapter;
import com.gjzg.bean.MessageBean;
import config.NetConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.DataUtils;
import utils.UserUtils;
import utils.Utils;
import view.CProgressDialog;

//系统消息
public class SysMsgFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View rootView, emptyView, msgPopView;
    private TextView titleTv, timeTv, contentTv;
    private PopupWindow msgPop;
    private ListView listView;
    private CProgressDialog cProgressDialog;
    private List<MessageBean> messageBeanList = new ArrayList<>();
    private MsgAdapter msgAdapter;
    private int clickPosition;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case 1:
                        notifyData();
                        break;
                    case 2:
                        messageBeanList.get(clickPosition).setUm_status("1");
                        notifyData();
                        cProgressDialog.dismiss();
                        pop();
                        break;
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_listview, null);
        initView();
        initData();
        setData();
        setListener();
        loadData();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(1);
            handler = null;
        }
    }

    private void initView() {
        initRootView();
        initEmptyView();
        initPopView();
    }

    private void initRootView() {
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView = (ListView) rootView.findViewById(R.id.listview);
        cProgressDialog = Utils.initProgressDialog(getActivity(), cProgressDialog);
    }

    private void initEmptyView() {
        emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_data, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setEmptyView(emptyView);
    }

    private void initPopView() {
        msgPopView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_msg, null);
        titleTv = (TextView) msgPopView.findViewById(R.id.tv_pop_msg_title);
        timeTv = (TextView) msgPopView.findViewById(R.id.tv_pop_msg_time);
        contentTv = (TextView) msgPopView.findViewById(R.id.tv_pop_msg_content);
        msgPop = new PopupWindow(msgPopView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        msgPop.setFocusable(true);
        msgPop.setTouchable(true);
        msgPop.setOutsideTouchable(true);
        msgPop.setBackgroundDrawable(new BitmapDrawable());
        msgPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
    }

    private void initData() {
        msgAdapter = new MsgAdapter(getActivity(), messageBeanList);
    }

    private void setData() {
        listView.setAdapter(msgAdapter);
    }

    private void setListener() {
        listView.setOnItemClickListener(this);
    }

    private void loadData() {
        String url = NetConfig.msgListUrl +
                "?u_id=" + UserUtils.readUserData(getActivity()).getId() +
                "&wm_type=0";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Utils.log(getActivity(), "sysMsgJson\n" + json);
                    messageBeanList.clear();
                    messageBeanList.addAll(DataUtils.getMessageBeanList(json));
                }
            }
        });
    }

    private void notifyData() {
        msgAdapter.notifyDataSetChanged();
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
        layoutParams.alpha = f;
        getActivity().getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clickPosition = position;
        MessageBean messageBean = messageBeanList.get(clickPosition);
        String status = messageBean.getUm_status();
        if (status == null || status.equals("null") || TextUtils.isEmpty(status)) {
        } else {
            if (status.equals("0")) {
                read();
            } else {
                pop();
            }
        }
    }

    private void read() {
        cProgressDialog.show();
        MessageBean messageBean = messageBeanList.get(clickPosition);
        String url = NetConfig.msgEditUrl +
                "?um_id=" + messageBean.getUm_id();
        Request request = new Request.Builder().url(url).get().build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject beanObj = new JSONObject(json);
                        if (beanObj.optInt("code") == 1) {
                            handler.sendEmptyMessage(2);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void pop() {
        MessageBean messageBean = messageBeanList.get(clickPosition);
        if (messageBean != null) {
            Utils.log(getActivity(), "messageBean\n" + messageBean.toString());
            String title = messageBean.getWm_title();
            if (title == null || title.equals("null") || TextUtils.isEmpty(title)) {
            } else {
                titleTv.setText(title);
            }
            String time = messageBean.getUm_in_time();
            if (time == null || time.equals("null") || TextUtils.isEmpty(time)) {
            } else {
                timeTv.setText(DataUtils.msgTimes(time));
            }
            String content = messageBean.getWm_desc();
            if (content == null || content.equals("null") || TextUtils.isEmpty(content)) {
            } else {
                contentTv.setText(content);
            }
            if (!msgPop.isShowing()) {
                backgroundAlpha(0.5f);
                msgPop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
            }
        }
    }
}
