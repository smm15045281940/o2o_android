package jumpworker.view;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bean.SkillBean;
import bean.ToComplainBean;
import bean.ToJumpWorkerBean;
import bean.WorkerBean;
import complain.view.ComplainActivity;
import config.IntentConfig;
import config.NetConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import persondetail.view.PersonDetailActivity;
import utils.DataUtils;
import utils.UserUtils;
import utils.Utils;
import view.CImageView;

public class JumpWorkerActivity extends AppCompatActivity implements View.OnClickListener {

    private View rootView;
    private RelativeLayout returnRl, complainRl;
    private MapView mapView;
    private CImageView iconIv, phoneIv;
    private ImageView sexIv, statusIv;
    private TextView topTitleTv, nameTv, skillNameTv, infoTv, addressTv;
    private LinearLayout surePriceLl, waitWorkerLl, fireWorkerLl;
    private final int SURE_PRICE = 1, WAIT_WORKER = 2, FIRE_WORKER = 3;
    private int SHOW_STATE;
    private ToJumpWorkerBean toJumpWorkerBean;
    private OkHttpClient okHttpClient;
    private WorkerBean workerBean;

    private TextView cancelWorkerTv, surePriceTv;
    private View cancelWorkerPopView;
    private PopupWindow cancelWorkerPop;

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
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(JumpWorkerActivity.this).inflate(R.layout.activity_jump_worker, null);
        setContentView(rootView);
        initView();
        initData();
        setListener();
        loadData();
    }

    private void initView() {
        initRootView();
        initPopView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_jump_worker_return);
        complainRl = (RelativeLayout) rootView.findViewById(R.id.rl_jump_worker_complain);
        mapView = (MapView) rootView.findViewById(R.id.mv_jump_worker);
        iconIv = (CImageView) rootView.findViewById(R.id.iv_jump_worker_icon);
        phoneIv = (CImageView) rootView.findViewById(R.id.iv_jump_worker_phone);
        sexIv = (ImageView) rootView.findViewById(R.id.iv_jump_worker_sex);
        statusIv = (ImageView) rootView.findViewById(R.id.iv_jump_worker_status);
        topTitleTv = (TextView) rootView.findViewById(R.id.tv_jump_worker_top_title);
        nameTv = (TextView) rootView.findViewById(R.id.tv_jump_worker_name);
        skillNameTv = (TextView) rootView.findViewById(R.id.tv_jump_worker_skill);
        infoTv = (TextView) rootView.findViewById(R.id.tv_jump_worker_info);
        addressTv = (TextView) rootView.findViewById(R.id.tv_jump_worker_address);
        surePriceLl = (LinearLayout) rootView.findViewById(R.id.ll_jump_talk_sure_price);
        waitWorkerLl = (LinearLayout) rootView.findViewById(R.id.ll_jump_talk_wait_worker);
        fireWorkerLl = (LinearLayout) rootView.findViewById(R.id.ll_jump_talk_fire_worker);

        cancelWorkerTv = (TextView) rootView.findViewById(R.id.tv_jump_worker_cancel_worker);
        surePriceTv = (TextView) rootView.findViewById(R.id.tv_jump_worker_sure_price);
    }

    private void initPopView() {
        cancelWorkerPopView = LayoutInflater.from(JumpWorkerActivity.this).inflate(R.layout.pop_dialog_0, null);
        ((TextView) cancelWorkerPopView.findViewById(R.id.tv_pop_dialog_0_content)).setText("确认不想用该工人?");
        ((TextView) cancelWorkerPopView.findViewById(R.id.tv_pop_dialog_0_cancel)).setText("取消");
        ((TextView) cancelWorkerPopView.findViewById(R.id.tv_pop_dialog_0_sure)).setText("确认");
        cancelWorkerPopView.findViewById(R.id.rl_pop_dialog_0_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelWorkerPop.dismiss();
            }
        });
        cancelWorkerPopView.findViewById(R.id.rl_pop_dialog_0_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelWorker();
            }
        });
        cancelWorkerPop = new PopupWindow(cancelWorkerPopView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        cancelWorkerPop.setFocusable(true);
        cancelWorkerPop.setTouchable(true);
        cancelWorkerPop.setOutsideTouchable(true);
        cancelWorkerPop.setBackgroundDrawable(new BitmapDrawable());
    }

    private void initData() {
        toJumpWorkerBean = (ToJumpWorkerBean) getIntent().getSerializableExtra(IntentConfig.toJumpWorker);
        okHttpClient = new OkHttpClient();
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        complainRl.setOnClickListener(this);
        iconIv.setOnClickListener(this);
        cancelWorkerTv.setOnClickListener(this);
        surePriceTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_jump_worker_return:
                finish();
                break;
            case R.id.rl_jump_worker_complain:
                ToComplainBean toComplainBean = new ToComplainBean();
                toComplainBean.setAgainstId(workerBean.getWorkerId());
                toComplainBean.setCtType("2");
                Intent i = new Intent(JumpWorkerActivity.this, ComplainActivity.class);
                i.putExtra(IntentConfig.toComplain, toComplainBean);
                startActivity(i);
                break;
            case R.id.iv_jump_worker_icon:
                Intent intent = new Intent(JumpWorkerActivity.this, PersonDetailActivity.class);
                intent.putExtra(IntentConfig.talkToDetail, workerBean.getWorkerId());
                startActivity(intent);
                break;
            case R.id.tv_jump_worker_cancel_worker:
                if (!cancelWorkerPop.isShowing()) {
                    cancelWorkerPop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.tv_jump_worker_sure_price:
                Utils.log(JumpWorkerActivity.this, "sure");
                break;
        }
    }

    private void loadData() {
        String url = NetConfig.workerUrl + "?u_id=" + toJumpWorkerBean.getWorkerId() + "&fu_id=" + UserUtils.readUserData(JumpWorkerActivity.this).getId();
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    workerBean = DataUtils.getWorkerBeanList(response.body().string()).get(0);
                    handler.sendEmptyMessage(1);
                }
            }
        });

    }

    private void loadSkill() {
        Request request = new Request.Builder().url(NetConfig.skillUrl + "?s_id=" + workerBean.getSkill()).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    List<SkillBean> skillBeanList = new ArrayList<>();
                    skillBeanList.addAll(DataUtils.getSkillBeanList(response.body().string()));
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < skillBeanList.size(); i++) {
                        sb.append(skillBeanList.get(i).getName());
                        if (i != skillBeanList.size() - 1) {
                            sb.append("、");
                        }
                    }
                    workerBean.setSkillName(sb.toString());
                    Utils.log(JumpWorkerActivity.this, workerBean.toString());
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }


    private void notifyData() {
        Picasso.with(JumpWorkerActivity.this).load(workerBean.getIcon()).into(iconIv);
        if (workerBean.getSex().equals("0")) {
            sexIv.setImageResource(R.mipmap.female);
        } else if (workerBean.getSex().equals("1")) {
            sexIv.setImageResource(R.mipmap.male);
        }
        if (workerBean.getStatus().equals("0")) {
            statusIv.setImageResource(R.mipmap.worker_leisure);
        } else if (workerBean.getStatus().equals("1")) {
            statusIv.setImageResource(R.mipmap.worker_mid);
        }
        nameTv.setText(workerBean.getTitle());
        skillNameTv.setText(workerBean.getSkillName());
        infoTv.setText(workerBean.getInfo());
        addressTv.setText(workerBean.getAddress());
        map();
        SHOW_STATE = SURE_PRICE;
        refreshState();
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
        LatLng point = new LatLng(Double.parseDouble("0"), Double.parseDouble("0"));
        OverlayOptions overlayOptions = new MarkerOptions().position(point).icon(bitmap);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(point, Float.parseFloat("19"));
        baiduMap.animateMapStatus(mapStatusUpdate);
        baiduMap.addOverlay(overlayOptions);
    }

    private void refreshState() {
        switch (SHOW_STATE) {
            case SURE_PRICE:
                surePriceLl.setVisibility(View.VISIBLE);
                waitWorkerLl.setVisibility(View.GONE);
                fireWorkerLl.setVisibility(View.GONE);
                topTitleTv.setText("待工人就位");
                break;
            case WAIT_WORKER:
                surePriceLl.setVisibility(View.GONE);
                waitWorkerLl.setVisibility(View.VISIBLE);
                fireWorkerLl.setVisibility(View.GONE);
                topTitleTv.setText("待工人就位");
                break;
            case FIRE_WORKER:
                surePriceLl.setVisibility(View.GONE);
                waitWorkerLl.setVisibility(View.GONE);
                fireWorkerLl.setVisibility(View.VISIBLE);
                topTitleTv.setText("已开工");
                break;
        }
    }

    private void cancelWorker() {
        //?action=cancel&o_id=8&tew_id=4&t_id=2&o_worker=9&u_id=1&s_id=2
        String url = NetConfig.orderUrl + "?action=cancel&o_id" + "&tew_id=" + "t_id=" + "&o_worker=" + toJumpWorkerBean.getWorkerId() + "u_id=" + UserUtils.readUserData(JumpWorkerActivity.this).getId() + "&s_id=";
        Utils.log(JumpWorkerActivity.this, url);
    }
}
