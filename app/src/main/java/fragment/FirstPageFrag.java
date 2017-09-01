package fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gjzg.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import activity.CityActivity;
import activity.JobActivity;
import activity.KindActivity;
import activity.LeftRightActivity;
import activity.SendJobActivity;
import adapter.FirstPageAdapter;
import config.IntentConfig;
import config.NetConfig;
import config.StateConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import view.CProgressDialog;

/**
 * 创建日期：2017/7/28 on 13:52
 * 作者:孙明明
 * 描述:首页
 */

public class FirstPageFrag extends CommonFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    //根视图
    private View rootView;
    //城市视图
    private RelativeLayout cityRl;
    //消息视图
    private RelativeLayout msgRl;
    //ListView视图
    private ListView listView;
    //加载对话框
    private CProgressDialog cPd;
    //首页数据类集合
    private List<String> firstPageList;
    //首页数据适配器
    private FirstPageAdapter firstPageAdapter;
    //okHttpClient
    private OkHttpClient okHttpClient;

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
        //初始化根视图
        return rootView = LayoutInflater.from(getActivity()).inflate(R.layout.frag_first_page, null);
    }

    @Override
    protected void initView() {
        initRootView();
        initDialogView();
    }

    private void initRootView() {
        //初始化城市视图
        cityRl = (RelativeLayout) rootView.findViewById(R.id.rl_frag_first_page_city);
        //初始化消息视图
        msgRl = (RelativeLayout) rootView.findViewById(R.id.rl_frag_first_page_msg);
        //初始化ListView
        listView = (ListView) rootView.findViewById(R.id.lv_frag_first_page);
    }

    private void initDialogView() {
        //初始化加载对话框视图
        cPd = new CProgressDialog(getActivity(), R.style.dialog_cprogress);
    }

    @Override
    protected void initData() {
        //初始化首页集合
        firstPageList = new ArrayList<>();
        //初始化首页适配器
        firstPageAdapter = new FirstPageAdapter(getActivity(), firstPageList);
        //初始化okHttpClient
        okHttpClient = new OkHttpClient();
    }

    @Override
    protected void setData() {
        //绑定
        listView.setAdapter(firstPageAdapter);
    }

    @Override
    protected void setListener() {
        //城市视图监听
        cityRl.setOnClickListener(this);
        //消息视图监听
        msgRl.setOnClickListener(this);
        //ListView项点击监听
        listView.setOnItemClickListener(this);
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

    private void saveLocalData(String json) {
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
                    parseJson(result);
                }
            }
        });
    }

    private void parseJson(String json) {
        try {
            JSONObject objBean = new JSONObject(json);
            if (objBean.optInt("code") == 200) {
                firstPageList.add("");
                firstPageList.add("");
                firstPageList.add("");
                handler.sendEmptyMessage(StateConfig.LOAD_DONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void notifyNoNet() {
        cPd.dismiss();
        firstPageList.add("");
        firstPageList.add("");
        firstPageList.add("");
        firstPageAdapter.notifyDataSetChanged();
    }

    private void notifyData() {
        cPd.dismiss();
        firstPageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_frag_first_page_city:
                Intent cityIntent = new Intent(getActivity(), CityActivity.class);
                startActivity(cityIntent);
                break;
            case R.id.rl_frag_first_page_msg:
                Intent msgIntent = new Intent(getActivity(), LeftRightActivity.class);
                msgIntent.putExtra(IntentConfig.intentName, IntentConfig.MESSAGE);
                startActivity(msgIntent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_frag_first_page:
                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), KindActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), JobActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getActivity(), SendJobActivity.class));
                        break;
                }
                break;
        }
    }
}
