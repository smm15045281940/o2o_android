package jumpemployer.view;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.gjzg.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bean.JumpEmployerBean;
import bean.SkillsBean;
import bean.ToJumpEmployerBean;
import bean.ToResignBean;
import config.IntentConfig;
import config.NetConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import persondetail.view.PersonDetailActivity;
import resign.view.ResignActivity;
import utils.DataUtils;
import utils.UserUtils;
import utils.Utils;
import view.CImageView;

public class JumpEmployerActivity extends AppCompatActivity implements View.OnClickListener {

    private View rootView;
    private RelativeLayout returnRl, complainRl;

    private MapView mapView;
    private CImageView iconIv, phoneIv;
    private ImageView sexIv;
    private TextView nameTv, skillCountTv, priceTv, addressTv, timeTv;

    private View cancelPopView;
    private PopupWindow cancelPop;

    private View beginDoView;
    private PopupWindow beginDoPop;

    private View resignPopView;
    private PopupWindow resignPop;

    private LinearLayout waitEmployerLl, sureDoLl, resignLl;
    private TextView waitEmployerCancelTv, sureDoCancelTv, sureDoDoTv, resignTv;
    private final int WAIT_EMPLOYER = 1, SURE_DO = 2, RESIGN = 3;
    private int SHOW_STATE;

    private ToJumpEmployerBean toJumpEmployerBean;
    private OkHttpClient okHttpClient;
    private JumpEmployerBean jumpEmployerBean;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case 1:
                        loadSkill();
                        break;
                    case 2:
                        notifyData();
                        break;
                    case 3:
                        finish();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(JumpEmployerActivity.this).inflate(R.layout.activity_jump_employer, null);
        setContentView(rootView);
        initView();
        initData();
        setListener();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void initView() {
        initRootView();
        initPopView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_jump_employer_return);
        complainRl = (RelativeLayout) rootView.findViewById(R.id.rl_jump_employer_complain);

        mapView = (MapView) rootView.findViewById(R.id.mv_jump_employer);
        iconIv = (CImageView) rootView.findViewById(R.id.iv_jump_employer_icon);
        phoneIv = (CImageView) rootView.findViewById(R.id.iv_jump_employer_phone);
        sexIv = (ImageView) rootView.findViewById(R.id.iv_jump_employer_sex);
        nameTv = (TextView) rootView.findViewById(R.id.tv_jump_employer_name);
        skillCountTv = (TextView) rootView.findViewById(R.id.tv_jump_employer_skill_count);
        priceTv = (TextView) rootView.findViewById(R.id.tv_jump_employer_price);
        addressTv = (TextView) rootView.findViewById(R.id.tv_jump_employer_address);
        timeTv = (TextView) rootView.findViewById(R.id.tv_jump_employer_time);

        waitEmployerLl = (LinearLayout) rootView.findViewById(R.id.ll_jump_employer_wait_employer);
        sureDoLl = (LinearLayout) rootView.findViewById(R.id.ll_jump_employer_sure_do);
        resignLl = (LinearLayout) rootView.findViewById(R.id.ll_jump_employer_resign);
        waitEmployerCancelTv = (TextView) rootView.findViewById(R.id.tv_jump_employer_wait_employer_cancel);
        sureDoCancelTv = (TextView) rootView.findViewById(R.id.tv_jump_employer_sure_do_cancel);
        sureDoDoTv = (TextView) rootView.findViewById(R.id.tv_jump_employer_sure_do_do);
        resignTv = (TextView) rootView.findViewById(R.id.tv_jump_employer_resign);
    }

    private void initPopView() {
        cancelPopView = LayoutInflater.from(JumpEmployerActivity.this).inflate(R.layout.pop_dialog_0, null);
        ((TextView) cancelPopView.findViewById(R.id.tv_pop_dialog_0_content)).setText("确认不想接该工作？");
        ((TextView) cancelPopView.findViewById(R.id.tv_pop_dialog_0_cancel)).setText("取消");
        ((TextView) cancelPopView.findViewById(R.id.tv_pop_dialog_0_sure)).setText("确认");
        cancelPopView.findViewById(R.id.rl_pop_dialog_0_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelPop.isShowing()) {
                    cancelPop.dismiss();
                }
            }
        });
        cancelPopView.findViewById(R.id.rl_pop_dialog_0_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelPop.isShowing()) {
                    cancelPop.dismiss();
                    cancel();
                }
            }
        });
        cancelPop = new PopupWindow(cancelPopView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        cancelPop.setFocusable(true);
        cancelPop.setTouchable(true);
        cancelPop.setOutsideTouchable(true);
        cancelPop.setBackgroundDrawable(new BitmapDrawable());

        beginDoView = LayoutInflater.from(JumpEmployerActivity.this).inflate(R.layout.pop_worker_sure, null);
        beginDoView.findViewById(R.id.tv_pop_worker_sure_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (beginDoPop.isShowing()) {
                    beginDoPop.dismiss();
                }
            }
        });
        beginDoView.findViewById(R.id.tv_pop_worker_sure_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (beginDoPop.isShowing()) {
                    beginDoPop.dismiss();
                    beginDo();
                }
            }
        });
        beginDoPop = new PopupWindow(beginDoView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        resignPopView = LayoutInflater.from(JumpEmployerActivity.this).inflate(R.layout.pop_dialog_0, null);
        ((TextView) resignPopView.findViewById(R.id.tv_pop_dialog_0_content)).setText("请与雇主先沟通再进行此项操作\n当日辞职，雇主不需要支付您当日工资\n建议今日工作完毕后，明天再辞职");
        ((TextView) resignPopView.findViewById(R.id.tv_pop_dialog_0_cancel)).setText("取消");
        ((TextView) resignPopView.findViewById(R.id.tv_pop_dialog_0_sure)).setText("确认");
        resignPopView.findViewById(R.id.rl_pop_dialog_0_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resignPop.isShowing()) {
                    resignPop.dismiss();
                }
            }
        });
        resignPopView.findViewById(R.id.rl_pop_dialog_0_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resignPop.isShowing()) {
                    resignPop.dismiss();
                    resign();
                }
            }
        });
        resignPop = new PopupWindow(resignPopView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    private void initData() {
        toJumpEmployerBean = (ToJumpEmployerBean) getIntent().getSerializableExtra(IntentConfig.toJumpEmployer);
        okHttpClient = new OkHttpClient();
        jumpEmployerBean = new JumpEmployerBean();
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        complainRl.setOnClickListener(this);
        iconIv.setOnClickListener(this);
        phoneIv.setOnClickListener(this);
        waitEmployerCancelTv.setOnClickListener(this);
        sureDoCancelTv.setOnClickListener(this);
        sureDoDoTv.setOnClickListener(this);
        resignTv.setOnClickListener(this);
    }

    private void loadData() {
        String url = NetConfig.taskBaseUrl + "?action=info&t_id=" + toJumpEmployerBean.getTaskId() + "&o_worker=" + UserUtils.readUserData(JumpEmployerActivity.this).getId();
        Utils.log(JumpEmployerActivity.this, url);
        Request request = new Request.Builder().url(url).get().build();
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
                        int code = beanObj.optInt("code");
                        switch (code) {
                            case 200:
                                JSONObject dataObj = beanObj.optJSONObject("data");
                                if (dataObj != null) {
                                    jumpEmployerBean.setPosX(dataObj.optString("t_posit_x"));
                                    jumpEmployerBean.setPosY(dataObj.optString("t_posit_y"));
                                    jumpEmployerBean.setIcon(dataObj.optString("u_img"));
                                    jumpEmployerBean.setName(dataObj.optString("u_true_name"));
                                    jumpEmployerBean.setSex(dataObj.optString("u_sex"));
                                    jumpEmployerBean.setMobile(dataObj.optString("u_mobile"));
                                    jumpEmployerBean.setAuthorId(dataObj.optString("t_author"));
                                    JSONArray workerArr = dataObj.optJSONArray("t_workers");
                                    if (workerArr != null) {
                                        for (int i = 0; i < workerArr.length(); i++) {
                                            JSONObject workerObj = workerArr.optJSONObject(i);
                                            if (workerObj != null) {
                                                JSONArray orderArr = workerObj.optJSONArray("orders");
                                                if (orderArr != null) {
                                                    for (int j = 0; j < orderArr.length(); j++) {
                                                        JSONObject o = orderArr.optJSONObject(j);
                                                        if (o != null) {
                                                            if (o.optString("o_worker").equals(UserUtils.readUserData(JumpEmployerActivity.this).getId())) {
                                                                jumpEmployerBean.setSkillId(workerObj.optString("tew_skills"));
                                                                jumpEmployerBean.setCount(workerObj.optString("tew_worker_num"));
                                                                jumpEmployerBean.setPrice(workerObj.optString("tew_price"));
                                                                jumpEmployerBean.setStartTime(workerObj.optString("tew_start_time"));
                                                                jumpEmployerBean.setEndTime(workerObj.optString("tew_end_time"));
                                                                jumpEmployerBean.setAddress(workerObj.optString("tew_address"));
                                                                jumpEmployerBean.setO_status(o.optString("o_status"));
                                                                jumpEmployerBean.setO_confirm(o.optString("o_confirm"));
                                                                jumpEmployerBean.setOrderId(o.optString("o_id"));
                                                                jumpEmployerBean.setTewId(o.optString("tew_id"));
                                                                jumpEmployerBean.setTaskId(o.optString("t_id"));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                handler.sendEmptyMessage(1);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void loadSkill() {
        String url = NetConfig.skillsUrl + "?s_id=" + jumpEmployerBean.getSkillId();
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    List<SkillsBean> skillsBeanList = new ArrayList<>();
                    skillsBeanList.addAll(DataUtils.getSkillBeanList(json));
                    if (skillsBeanList.size() != 0) {
//                        jumpEmployerBean.setSkillName(skillsBeanList.get(0).getName());
                    }
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }

    private void cancel() {
        String url = NetConfig.orderUrl + "?action=cancel&o_id=" + jumpEmployerBean.getOrderId();
        Utils.log(JumpEmployerActivity.this, url);
        Request cancelRequest = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(cancelRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Utils.log(JumpEmployerActivity.this, json);
                    handler.sendEmptyMessage(3);
                }
            }
        });
    }

    private void beginDo() {
        //?action=workerConfirm&o_id=8&t_id=2&o_worker=9
        String url = NetConfig.orderUrl + "?action=workerConfirm&o_id=" + jumpEmployerBean.getOrderId() + "&t_id=" + jumpEmployerBean.getTaskId() + "&o_worker=" + UserUtils.readUserData(JumpEmployerActivity.this).getId();
        Utils.log(JumpEmployerActivity.this, url);
        Request beginRequest = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(beginRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Utils.log(JumpEmployerActivity.this, json);
                    handler.sendEmptyMessage(3);
                }
            }
        });
    }

    private void resign() {
        ToResignBean toResignBean = new ToResignBean();
        toResignBean.setTewId(jumpEmployerBean.getTewId());
        toResignBean.setTaskId(jumpEmployerBean.getTaskId());
        toResignBean.setAuthorId(jumpEmployerBean.getAuthorId());
        toResignBean.setSkillId(jumpEmployerBean.getSkillId());
        Intent resignIntent = new Intent(JumpEmployerActivity.this, ResignActivity.class);
        resignIntent.putExtra(IntentConfig.toResign, toResignBean);
        startActivity(resignIntent);
    }

    private void notifyData() {
        Picasso.with(JumpEmployerActivity.this).load(jumpEmployerBean.getIcon()).into(iconIv);
        if (jumpEmployerBean.getSex().equals("0")) {
            sexIv.setImageResource(R.mipmap.female);
        } else if (jumpEmployerBean.getSex().equals("1")) {
            sexIv.setImageResource(R.mipmap.male);
        }
        nameTv.setText(jumpEmployerBean.getName());
        skillCountTv.setText("招" + jumpEmployerBean.getSkillName() + jumpEmployerBean.getCount() + "人");
        priceTv.setText("工资" + jumpEmployerBean.getPrice() + "元/天");
        addressTv.setText(jumpEmployerBean.getAddress());
        timeTv.setText("工期" + DataUtils.getDateToString(Long.parseLong(jumpEmployerBean.getStartTime())) + "-" + DataUtils.getDateToString(Long.parseLong(jumpEmployerBean.getEndTime())));
        map();
        if (jumpEmployerBean.getO_status().equals("0")) {
            if (jumpEmployerBean.getO_confirm().equals("0")) {
                SHOW_STATE = WAIT_EMPLOYER;
            } else if (jumpEmployerBean.getO_confirm().equals("1")) {
                SHOW_STATE = RESIGN;
            } else if (jumpEmployerBean.getO_confirm().equals("2")) {
                SHOW_STATE = SURE_DO;
            }
        }
        refreshState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_jump_employer_return:
                finish();
                break;
            case R.id.rl_jump_employer_complain:
                Utils.log(JumpEmployerActivity.this, "complain");
                break;
            case R.id.iv_jump_employer_icon:
                Intent i = new Intent(JumpEmployerActivity.this, PersonDetailActivity.class);
                i.putExtra(IntentConfig.talkToDetail, jumpEmployerBean.getAuthorId());
                startActivity(i);
                break;
            case R.id.iv_jump_employer_phone:
                Intent in = new Intent(Intent.ACTION_DIAL);
                in.setData(Uri.parse("tel:" + jumpEmployerBean.getMobile()));
                if (in.resolveActivity(getPackageManager()) != null) {
                    startActivity(in);
                }
                break;
            case R.id.tv_jump_employer_wait_employer_cancel:
                if (!cancelPop.isShowing()) {
                    cancelPop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.tv_jump_employer_sure_do_cancel:
                if (!cancelPop.isShowing()) {
                    cancelPop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.tv_jump_employer_sure_do_do:
                if (!beginDoPop.isShowing()) {
                    beginDoPop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.tv_jump_employer_resign:
                resign();
                break;
        }
    }

    private void map() {
        mapView.showScaleControl(false);
        mapView.showZoomControls(false);
        BaiduMap baiduMap = mapView.getMap();
        UiSettings settings = baiduMap.getUiSettings();
        settings.setAllGesturesEnabled(false);
        settings.setOverlookingGesturesEnabled(false);
        settings.setZoomGesturesEnabled(false);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.point_blue);
        LatLng point = new LatLng(Double.parseDouble(jumpEmployerBean.getPosX()), Double.parseDouble(jumpEmployerBean.getPosY()));
        OverlayOptions overlayOptions = new MarkerOptions().position(point).icon(bitmap);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(point, Float.parseFloat("19"));
        baiduMap.animateMapStatus(mapStatusUpdate);
        baiduMap.addOverlay(overlayOptions);
    }

    private void refreshState() {
        switch (SHOW_STATE) {
            case WAIT_EMPLOYER:
                waitEmployerLl.setVisibility(View.VISIBLE);
                sureDoLl.setVisibility(View.GONE);
                resignLl.setVisibility(View.GONE);
                break;
            case SURE_DO:
                waitEmployerLl.setVisibility(View.GONE);
                sureDoLl.setVisibility(View.VISIBLE);
                resignLl.setVisibility(View.GONE);
                break;
            case RESIGN:
                waitEmployerLl.setVisibility(View.GONE);
                sureDoLl.setVisibility(View.GONE);
                resignLl.setVisibility(View.VISIBLE);
                break;
        }
    }
}
