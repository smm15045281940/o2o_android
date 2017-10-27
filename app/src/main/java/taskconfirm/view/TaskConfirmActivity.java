package taskconfirm.view;

import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bean.PublishBean;
import bean.PublishWorkerBean;
import config.NetConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taskconfirm.adapter.InputPasswordAdapter;
import taskconfirm.adapter.SelectVoucherAdapter;
import taskconfirm.adapter.TaskConfirmAdapter;
import taskconfirm.bean.InputPasswordBean;
import taskconfirm.bean.SelectVoucherBean;
import utils.DataUtils;
import utils.UserUtils;
import utils.Utils;
import view.CProgressDialog;

public class TaskConfirmActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private View rootView, selectVoucherView;

    private View inputPasswordView;
    private GridView inputPasswordGv;
    private List<InputPasswordBean> inputPasswordBeanList;
    private InputPasswordAdapter inputPasswordAdapter;
    private PopupWindow inputPasswordPop;
    private TextView inputPasswordCloseTv, inputPasswordForgetTv;
    private ImageView inputPasswordPoint0Iv, inputPasswordPoint1Iv, inputPasswordPoint2Iv, inputPasswordPoint3Iv, inputPasswordPoint4Iv, inputPasswordPoint5Iv;

    private RelativeLayout returnRl, sureRl;
    private TextView beforeSumTv;
    private PopupWindow selectVoucherPop;
    private ListView lv, selectVoucherLv;
    private SelectVoucherAdapter selectVoucherAdapter;
    private TaskConfirmAdapter adapter;
    private List<SelectVoucherBean> selectVoucherBeanList;

    private StringBuilder inputPasswordSb;

    private PublishBean publishBean;

    private String sumTotal;

    private CProgressDialog cpd;

    private final int SUM_TOTAL_SUCCESS = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case SUM_TOTAL_SUCCESS:
                        cpd.dismiss();
                        beforeSumTv.setText(sumTotal);
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(TaskConfirmActivity.this).inflate(R.layout.activity_task_confirm, null);
        setContentView(rootView);
        initView();
        initData();
        setData();
        setListener();
        loadData();
    }

    private void initView() {
        initRootView();
        initPopView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_task_confirm_return);
        sureRl = (RelativeLayout) rootView.findViewById(R.id.rl_task_confirm_sure);
        beforeSumTv = (TextView) rootView.findViewById(R.id.tv_task_confirm_sum_before);
        lv = (ListView) rootView.findViewById(R.id.lv_task_confirm);
        cpd = Utils.initProgressDialog(TaskConfirmActivity.this, cpd);
    }

    private void initPopView() {
        selectVoucherView = LayoutInflater.from(TaskConfirmActivity.this).inflate(R.layout.pop_select_voucher, null);
        selectVoucherLv = (ListView) selectVoucherView.findViewById(R.id.lv_pop_select_voucher);
        selectVoucherPop = new PopupWindow(selectVoucherView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        selectVoucherPop.setFocusable(true);
        selectVoucherPop.setTouchable(true);
        selectVoucherPop.setOutsideTouchable(true);
        selectVoucherPop.setBackgroundDrawable(new PaintDrawable());
        selectVoucherPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
                selectVoucherBeanList.clear();
            }
        });
        inputPasswordView = LayoutInflater.from(TaskConfirmActivity.this).inflate(R.layout.pop_input_password, null);
        inputPasswordGv = (GridView) inputPasswordView.findViewById(R.id.gv_pop_input_password);
        inputPasswordCloseTv = (TextView) inputPasswordView.findViewById(R.id.tv_pop_input_password_close);
        inputPasswordForgetTv = (TextView) inputPasswordView.findViewById(R.id.tv_pop_input_password_forget);
        inputPasswordPoint0Iv = (ImageView) inputPasswordView.findViewById(R.id.iv_pwd_point_0);
        inputPasswordPoint1Iv = (ImageView) inputPasswordView.findViewById(R.id.iv_pwd_point_1);
        inputPasswordPoint2Iv = (ImageView) inputPasswordView.findViewById(R.id.iv_pwd_point_2);
        inputPasswordPoint3Iv = (ImageView) inputPasswordView.findViewById(R.id.iv_pwd_point_3);
        inputPasswordPoint4Iv = (ImageView) inputPasswordView.findViewById(R.id.iv_pwd_point_4);
        inputPasswordPoint5Iv = (ImageView) inputPasswordView.findViewById(R.id.iv_pwd_point_5);
        inputPasswordPop = new PopupWindow(inputPasswordView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        inputPasswordPop.setFocusable(true);
        inputPasswordPop.setTouchable(true);
        inputPasswordPop.setOutsideTouchable(true);
        inputPasswordPop.setBackgroundDrawable(new PaintDrawable());
        inputPasswordPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
                inputPasswordBeanList.clear();
            }
        });
    }

    private void initData() {
        publishBean = new PublishBean();
        publishBean = (PublishBean) getIntent().getSerializableExtra("publishBean");
        adapter = new TaskConfirmAdapter(TaskConfirmActivity.this, publishBean);
        selectVoucherBeanList = new ArrayList<>();
        selectVoucherAdapter = new SelectVoucherAdapter(TaskConfirmActivity.this, selectVoucherBeanList);
        inputPasswordBeanList = new ArrayList<>();
        inputPasswordAdapter = new InputPasswordAdapter(TaskConfirmActivity.this, inputPasswordBeanList);
    }

    private void setData() {
        lv.setAdapter(adapter);
        selectVoucherLv.setAdapter(selectVoucherAdapter);
        inputPasswordGv.setAdapter(inputPasswordAdapter);
        inputPasswordGv.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        sureRl.setOnClickListener(this);
        selectVoucherLv.setOnItemClickListener(this);
        inputPasswordGv.setOnItemClickListener(this);
        inputPasswordCloseTv.setOnClickListener(this);
        inputPasswordForgetTv.setOnClickListener(this);
    }

    private void loadData() {
        cpd.show();
        String testStr = DataUtils.getPublishJson(publishBean, false);
        Log.e("TAG", "testStr=" + testStr);
        String base64Str = Base64.encodeToString(testStr.getBytes(), Base64.DEFAULT);
        Log.e("TAG", "base64Str" + base64Str);
        OkHttpClient okhttpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("data", base64Str)
                .build();
        Request request = new Request.Builder()
                .url(NetConfig.subTotalUrl)
                .post(body)
                .build();
        okhttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject beanObj = new JSONObject(json);
                        if (beanObj.optInt("code") == 200) {
                            sumTotal = beanObj.optString("data");
                            handler.sendEmptyMessage(SUM_TOTAL_SUCCESS);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void submitOrder() {
        String str = DataUtils.getPublishJson(publishBean, true);
        String baseStr = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        OkHttpClient okhttpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("data", baseStr)
                .build();
        Request request = new Request.Builder()
                .url(NetConfig.publishUrl)
                .post(body)
                .build();
        okhttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Log.e("TAG", "submitOrder=" + json);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_task_confirm_return:
                finish();
                break;
            case R.id.rl_task_confirm_sure:
                inputPasswordSb = new StringBuilder();
                InputPasswordBean inputPasswordBean0 = new InputPasswordBean(0, 1, "");
                InputPasswordBean inputPasswordBean1 = new InputPasswordBean(0, 2, "ABC");
                InputPasswordBean inputPasswordBean2 = new InputPasswordBean(0, 3, "DEF");
                InputPasswordBean inputPasswordBean3 = new InputPasswordBean(0, 4, "GHI");
                InputPasswordBean inputPasswordBean4 = new InputPasswordBean(0, 5, "JKL");
                InputPasswordBean inputPasswordBean5 = new InputPasswordBean(0, 6, "MNO");
                InputPasswordBean inputPasswordBean6 = new InputPasswordBean(0, 7, "PQRS");
                InputPasswordBean inputPasswordBean7 = new InputPasswordBean(0, 8, "TUV");
                InputPasswordBean inputPasswordBean8 = new InputPasswordBean(0, 9, "WXYZ");
                InputPasswordBean inputPasswordBean9 = new InputPasswordBean(1, 0, "");
                InputPasswordBean inputPasswordBean10 = new InputPasswordBean(0, 0, "");
                InputPasswordBean inputPasswordBean11 = new InputPasswordBean(2, 0, "");
                inputPasswordBeanList.add(inputPasswordBean0);
                inputPasswordBeanList.add(inputPasswordBean1);
                inputPasswordBeanList.add(inputPasswordBean2);
                inputPasswordBeanList.add(inputPasswordBean3);
                inputPasswordBeanList.add(inputPasswordBean4);
                inputPasswordBeanList.add(inputPasswordBean5);
                inputPasswordBeanList.add(inputPasswordBean6);
                inputPasswordBeanList.add(inputPasswordBean7);
                inputPasswordBeanList.add(inputPasswordBean8);
                inputPasswordBeanList.add(inputPasswordBean9);
                inputPasswordBeanList.add(inputPasswordBean10);
                inputPasswordBeanList.add(inputPasswordBean11);
                inputPasswordAdapter.notifyDataSetChanged();
                Utils.setGridViewHeight(inputPasswordGv, 3);
                backgroundAlpha(0.5f);
                inputPasswordPop.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.tv_pop_input_password_close:
                inputPasswordPop.dismiss();
                break;
            case R.id.tv_pop_input_password_forget:
                Utils.log(TaskConfirmActivity.this, "forget");
                break;
        }
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gv_pop_input_password:
                InputPasswordBean inputPasswordBean = inputPasswordBeanList.get(position);
                if (inputPasswordBean != null) {
                    switch (inputPasswordBean.getType()) {
                        case 0:
                            if (inputPasswordSb.length() < 6) {
                                inputPasswordSb.append(inputPasswordBean.getNumber());
                                notifyPoints(inputPasswordSb.length());
                                Utils.log(TaskConfirmActivity.this, "password=" + inputPasswordSb.toString());
                            }
                            if (inputPasswordSb.length() == 6) {
                                publishBean.setPass(inputPasswordSb.toString());
                                submitOrder();
                            }
                            break;
                        case 1:
                            Utils.log(TaskConfirmActivity.this, "null");
                            break;
                        case 2:
                            if (inputPasswordSb.length() != 0) {
                                inputPasswordSb.deleteCharAt(inputPasswordSb.length() - 1);
                                notifyPoints(inputPasswordSb.length());
                                Utils.log(TaskConfirmActivity.this, "password=" + inputPasswordSb.toString());
                            }
                            break;
                    }
                }
                break;
        }
    }

    private void notifyPoints(int num) {
        switch (num) {
            case 0:
                inputPasswordPoint0Iv.setVisibility(View.GONE);
                inputPasswordPoint1Iv.setVisibility(View.GONE);
                inputPasswordPoint2Iv.setVisibility(View.GONE);
                inputPasswordPoint3Iv.setVisibility(View.GONE);
                inputPasswordPoint4Iv.setVisibility(View.GONE);
                inputPasswordPoint5Iv.setVisibility(View.GONE);
                break;
            case 1:
                inputPasswordPoint0Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint1Iv.setVisibility(View.GONE);
                inputPasswordPoint2Iv.setVisibility(View.GONE);
                inputPasswordPoint3Iv.setVisibility(View.GONE);
                inputPasswordPoint4Iv.setVisibility(View.GONE);
                inputPasswordPoint5Iv.setVisibility(View.GONE);
                break;
            case 2:
                inputPasswordPoint0Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint1Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint2Iv.setVisibility(View.GONE);
                inputPasswordPoint3Iv.setVisibility(View.GONE);
                inputPasswordPoint4Iv.setVisibility(View.GONE);
                inputPasswordPoint5Iv.setVisibility(View.GONE);
                break;
            case 3:
                inputPasswordPoint0Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint1Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint2Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint3Iv.setVisibility(View.GONE);
                inputPasswordPoint4Iv.setVisibility(View.GONE);
                inputPasswordPoint5Iv.setVisibility(View.GONE);
                break;
            case 4:
                inputPasswordPoint0Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint1Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint2Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint3Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint4Iv.setVisibility(View.GONE);
                inputPasswordPoint5Iv.setVisibility(View.GONE);
                break;
            case 5:
                inputPasswordPoint0Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint1Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint2Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint3Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint4Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint5Iv.setVisibility(View.GONE);
                break;
            case 6:
                inputPasswordPoint0Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint1Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint2Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint3Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint4Iv.setVisibility(View.VISIBLE);
                inputPasswordPoint5Iv.setVisibility(View.VISIBLE);
                break;
        }
    }
}