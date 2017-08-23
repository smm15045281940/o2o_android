package fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gjzg.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.SysMsgAdapter;
import bean.SysMsg;
import config.NetConfig;
import config.StateConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.Utils;
import view.CProgressDialog;

/**
 * 创建日期：2017/8/8 on 11:06
 * 作者:孙明明
 * 描述:工作邀约
 */

public class JobOfferFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private View rootView, emptyNoNetView, emptyNoDataView;
    private TextView noNetRefreshTv;
    private ListView jobOfferLv;
    private CProgressDialog progressDialog;
    private List<SysMsg> jobOfferList;
    private SysMsgAdapter jobOfferAdapter;
    private OkHttpClient okHttpClient;
    private int LOAD_STATE;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                progressDialog.dismiss();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message, null);
        initView();
        initData();
        setData();
        setListener();
        loadData();
        return rootView;
    }

    private void initView() {
        initRootView();
        initEmptyView();
        initDialogView();
    }

    private void initRootView() {
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        jobOfferLv = (ListView) rootView.findViewById(R.id.lv_message_offer);
    }

    private void initEmptyView() {
        emptyNoDataView = View.inflate(getActivity(), R.layout.empty_no_data, null);
        emptyNoDataView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((ViewGroup) jobOfferLv.getParent()).addView(emptyNoDataView);
        emptyNoDataView.setVisibility(View.GONE);
        emptyNoNetView = View.inflate(getActivity(), R.layout.empty_no_net, null);
        emptyNoNetView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((ViewGroup) jobOfferLv.getParent()).addView(emptyNoNetView);
        emptyNoNetView.setVisibility(View.GONE);
        noNetRefreshTv = (TextView) emptyNoNetView.findViewById(R.id.tv_empty_no_net_refresh);
    }

    private void initDialogView() {
        progressDialog = new CProgressDialog(getActivity(), R.style.dialog_cprogress);
    }

    private void initData() {
        jobOfferList = new ArrayList<>();
        jobOfferAdapter = new SysMsgAdapter(getActivity(), jobOfferList);
        okHttpClient = new OkHttpClient();
    }

    private void setData() {
        jobOfferLv.setAdapter(jobOfferAdapter);
    }

    private void setListener() {
        noNetRefreshTv.setOnClickListener(this);
        jobOfferLv.setOnItemClickListener(this);
    }

    private void loadData() {
        loadNetData();
    }

    private void loadNetData() {
        progressDialog.show();
        Request request = new Request.Builder().url(NetConfig.testUrl).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOAD_STATE = StateConfig.LOAD_NO_NET;
                handler.sendEmptyMessage(StateConfig.LOAD_NO_NET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    parseJson(json);
                }
            }
        });
    }

    private void parseJson(String json) {
        try {
            JSONObject objBean = new JSONObject(json);
            if (objBean.optInt("code") == 200) {
                SysMsg mo0 = new SysMsg();
                mo0.setTitle("工作邀约");
                mo0.setDate("2017/03/07");
                mo0.setDes("有一份适合你的工作，雇主已经向您发起邀约，等待您同意");
                mo0.setArrowShow(true);
                SysMsg mo1 = new SysMsg();
                mo1.setTitle("工作邀约");
                mo1.setDate("2017/03/06");
                mo1.setDes("您发布的工作有工人感兴趣并向您发起邀约，等待您同意");
                mo1.setArrowShow(true);
                jobOfferList.add(mo0);
                jobOfferList.add(mo1);
                LOAD_STATE = StateConfig.LOAD_DONE;
                handler.sendEmptyMessage(StateConfig.LOAD_DONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void notifyNoNet() {
        switch (LOAD_STATE) {
            case StateConfig.LOAD_NO_NET:
                jobOfferLv.setEmptyView(emptyNoNetView);
                Utils.toast(getActivity(), StateConfig.loadNonet);
                break;
        }
    }

    private void notifyData() {
        jobOfferAdapter.notifyDataSetChanged();
        jobOfferLv.setEmptyView(emptyNoDataView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_empty_no_net_refresh:
                loadNetData();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Utils.toast(getActivity(), "你点击了第" + (position + 1) + "个" + jobOfferList.get(position).getTitle());
    }
}
