package talkworker.view;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.List;

import bean.ToChangePriceBean;
import bean.ToComplainBean;
import bean.ToSelectTaskBean;
import bean.ToTalkWorkerBean;
import changeprice.view.ChangePriceActivity;
import complain.view.ComplainActivity;
import config.IntentConfig;
import config.NetConfig;
import persondetail.view.PersonDetailActivity;
import selecttask.view.SelectTaskActivity;
import talkworker.presenter.ITalkWorkerPresenter;
import talkworker.presenter.TalkWorkerPresenter;
import utils.DataUtils;
import utils.UserUtils;
import utils.Utils;
import bean.WorkerBean;
import view.CImageView;
import view.CProgressDialog;

public class TalkWorkerActivity extends AppCompatActivity implements ITalkWorkerActivity, View.OnClickListener {

    private View rootView;
    private RelativeLayout returnRl, doingRl, complainRl;
    private LinearLayout ll1;
    private TextView cancelWorkerTv, surePriceTv;
    private CProgressDialog cpd;
    private CImageView iconIv, phoneIv;
    private ImageView sexIv, statusIv;
    private TextView nameTv, skillTv, infoTv, addressTv, topTitleTv;
    private MapView mapView;
    private BaiduMap baiduMap;
    private BitmapDescriptor bitmap;

    private ITalkWorkerPresenter talkWorkerPresenter;
    private WorkerBean workerBean;
    private ToTalkWorkerBean toTalkWorkerBean;

    private View cancelWorkerPopView;
    private PopupWindow cancelWorkerPop;

    private View surePricePopView;
    private PopupWindow surePricePop;

    private final int LOAD_SUCCESS = 1;
    private final int LOAD_FAILURE = 2;
    private final int CHECK_HAVE = 3;
    private final int CHECK_NONE = 4;
    private final int CANCEL_WORKER_SUCCESS = 5;
    private final int CANCEL_WORKER_FAILURE = 6;
    private final int AUTHOR_SURE_SUCCESS = 7;
    private final int AUTHOR_SURE_FAILURE = 8;
    private String skill;

    private final int FIND_WORKER = 1;
    private final int WORKER_BUSY = 2;
    private final int WORKER_WAIT = 3;
    private final int WAIT_WORKER = 4;

    //第一条路 雇主招工人 5种状态
    private final int NO_YOURSELF = 1;//您不能招工自己
    private final int TALK_TO_YOU = 2;//此工人正在与您洽谈
    private final int DOING_FOR_YOU = 3;//此工人正在您的任务中工作
    private final int BUSY = 4;//此工人正在工作中
    private final int WAIT = 5;//我要招工
    private RelativeLayout noYourselfRl;
    private RelativeLayout talkToYouRl;
    private RelativeLayout doingForyouRl;
    private RelativeLayout busyRl;
    private RelativeLayout waitRl;
    private int SHOW_STATE;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case LOAD_SUCCESS:
                        notifyData();
                        break;
                    case LOAD_FAILURE:
                        break;
                    case CHECK_HAVE:
                        Intent intent = new Intent(TalkWorkerActivity.this, SelectTaskActivity.class);
                        ToSelectTaskBean toSelectTaskBean = new ToSelectTaskBean();
                        toSelectTaskBean.setSkillId(toTalkWorkerBean.getSkillId());
                        toSelectTaskBean.setWorkerId(toTalkWorkerBean.getWorkerId());
                        intent.putExtra(IntentConfig.toSelectTask, toSelectTaskBean);
                        startActivity(intent);
                        break;
                    case CHECK_NONE:
                        Utils.toast(TalkWorkerActivity.this, "请先去发布工作");
                        break;
                    case CANCEL_WORKER_SUCCESS:
                        cpd.dismiss();
                        Utils.toast(TalkWorkerActivity.this, "取消工人成功");
                        SHOW_STATE = FIND_WORKER;
                        refreshState();
                        break;
                    case CANCEL_WORKER_FAILURE:
                        cpd.dismiss();
                        Utils.toast(TalkWorkerActivity.this, "取消工人失败");
                        break;
                    case AUTHOR_SURE_SUCCESS:
                        cpd.dismiss();
                        surePricePop.dismiss();
                        SHOW_STATE = WAIT_WORKER;
                        refreshState();
                        break;
                    case AUTHOR_SURE_FAILURE:
                        cpd.dismiss();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(TalkWorkerActivity.this).inflate(R.layout.activity_talk_worker, null);
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
        if (talkWorkerPresenter != null) {
            talkWorkerPresenter.destroy();
            talkWorkerPresenter = null;
        }
        if (handler != null) {
            handler.removeMessages(LOAD_SUCCESS);
            handler.removeMessages(LOAD_FAILURE);
            handler.removeMessages(CHECK_HAVE);
            handler.removeMessages(CHECK_NONE);
            handler.removeMessages(CANCEL_WORKER_SUCCESS);
            handler.removeMessages(CANCEL_WORKER_FAILURE);
            handler.removeMessages(AUTHOR_SURE_SUCCESS);
            handler.removeMessages(AUTHOR_SURE_FAILURE);
            handler = null;
        }
    }

    private void initView() {
        initRootView();
        initPopView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_jump_worker_return);
        complainRl = (RelativeLayout) rootView.findViewById(R.id.rl_jump_worker_complain);
        topTitleTv = (TextView) rootView.findViewById(R.id.tv_jump_worker_top_title);

        noYourselfRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_worker_no_yourself);
        talkToYouRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_worker_talk_to_you);
        doingForyouRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_worker_doing_for_you);
        busyRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_worker_busy);
        waitRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_worker_wait);

        ll1 = (LinearLayout) rootView.findViewById(R.id.ll_talk_worker_1);
        cancelWorkerTv = (TextView) rootView.findViewById(R.id.tv_talk_worker_cancel_worker);
        surePriceTv = (TextView) rootView.findViewById(R.id.tv_talk_worker_sure_price);
        cpd = Utils.initProgressDialog(TalkWorkerActivity.this, cpd);
        iconIv = (CImageView) rootView.findViewById(R.id.iv_jump_worker_icon);
        phoneIv = (CImageView) rootView.findViewById(R.id.iv_jump_worker_phone);
        sexIv = (ImageView) rootView.findViewById(R.id.iv_jump_worker_sex);
        statusIv = (ImageView) rootView.findViewById(R.id.iv_jump_worker_status);
        nameTv = (TextView) rootView.findViewById(R.id.tv_jump_worker_name);
        skillTv = (TextView) rootView.findViewById(R.id.tv_jump_worker_skill);
        infoTv = (TextView) rootView.findViewById(R.id.tv_jump_worker_info);
        addressTv = (TextView) rootView.findViewById(R.id.tv_jump_worker_address);
        mapView = (MapView) rootView.findViewById(R.id.mv_jump_worker);
        mapView.showScaleControl(false);
        mapView.showZoomControls(false);
        baiduMap = mapView.getMap();
        UiSettings settings = baiduMap.getUiSettings();
        settings.setAllGesturesEnabled(false);
        settings.setOverlookingGesturesEnabled(false);
        settings.setZoomGesturesEnabled(false);
        bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.point_blue);
    }

    private void initPopView() {
        cancelWorkerPopView = LayoutInflater.from(TalkWorkerActivity.this).inflate(R.layout.pop_dialog_0, null);
        ((TextView) cancelWorkerPopView.findViewById(R.id.tv_pop_dialog_0_content)).setText("确认不想用该工人？\n确认之后您将无法与Ta联系");
        ((TextView) cancelWorkerPopView.findViewById(R.id.tv_pop_dialog_0_cancel)).setText("取消");
        ((TextView) cancelWorkerPopView.findViewById(R.id.tv_pop_dialog_0_sure)).setText("确认");
        cancelWorkerPopView.findViewById(R.id.rl_pop_dialog_0_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelWorkerPop.isShowing()) {
                    cancelWorkerPop.dismiss();
                }
            }
        });
        cancelWorkerPopView.findViewById(R.id.rl_pop_dialog_0_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelWorkerPop.isShowing()) {
                    cancelWorkerPop.dismiss();
                    cpd.show();
                    String url = NetConfig.orderUrl + "?action=cancel&o_id=" + toTalkWorkerBean.getOrderId() + "&tew_id=" + toTalkWorkerBean.getTewId() + "&t_id=" + toTalkWorkerBean.getTaskId() + "&o_worker=" + toTalkWorkerBean.getWorkerId() + "&u_id=" + UserUtils.readUserData(TalkWorkerActivity.this).getId() + "&s_id=" + toTalkWorkerBean.getSkillId();
                    Utils.log(TalkWorkerActivity.this, url);
                    talkWorkerPresenter.cancelWorker(url);
                }
            }
        });
        cancelWorkerPop = new PopupWindow(cancelWorkerPopView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        cancelWorkerPop.setFocusable(true);
        cancelWorkerPop.setTouchable(true);
        cancelWorkerPop.setOutsideTouchable(true);
        cancelWorkerPop.setBackgroundDrawable(new BitmapDrawable());
        cancelWorkerPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });

        surePricePopView = LayoutInflater.from(TalkWorkerActivity.this).inflate(R.layout.pop_employer_sure, null);
        surePricePopView.findViewById(R.id.tv_pop_sure_price_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surePricePop.isShowing()) {
                    surePricePop.dismiss();
                }
            }
        });
        surePricePopView.findViewById(R.id.tv_pop_sure_price_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changePriceIntent = new Intent(TalkWorkerActivity.this, ChangePriceActivity.class);
                ToChangePriceBean toChangePriceBean = new ToChangePriceBean();
                toChangePriceBean.setTaskId(toTalkWorkerBean.getTaskId());
                toChangePriceBean.setTewId(toTalkWorkerBean.getTewId());
                toChangePriceBean.setWorkerId(toTalkWorkerBean.getWorkerId());
                toChangePriceBean.setAuthorId(UserUtils.readUserData(TalkWorkerActivity.this).getId());
                toChangePriceBean.setAmount(toTalkWorkerBean.getAmount());
                toChangePriceBean.setEndTime(toTalkWorkerBean.getEndTime());
                toChangePriceBean.setSkillName(toTalkWorkerBean.getSkillId());
                toChangePriceBean.setPrice(toTalkWorkerBean.getPrice());
                toChangePriceBean.setStartTime(toTalkWorkerBean.getStartTime());
                changePriceIntent.putExtra(IntentConfig.toChangePrice, toChangePriceBean);
                startActivity(changePriceIntent);
                surePricePop.dismiss();
            }
        });
        surePricePopView.findViewById(R.id.tv_pop_sure_price_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.log(TalkWorkerActivity.this, "雇主确认");
                String url = NetConfig.orderUrl + "?action=employerConfirm&o_id=" + toTalkWorkerBean.getOrderId() + "&t_id=" + toTalkWorkerBean.getTaskId() + "&u_id=" + UserUtils.readUserData(TalkWorkerActivity.this).getId();
                Utils.log(TalkWorkerActivity.this, url);
                cpd.show();
                talkWorkerPresenter.authorSure(url);
            }
        });
        surePricePop = new PopupWindow(surePricePopView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        surePricePop.setFocusable(true);
        surePricePop.setTouchable(true);
        surePricePop.setOutsideTouchable(true);
        surePricePop.setBackgroundDrawable(new BitmapDrawable());
    }

    private void initData() {
        talkWorkerPresenter = new TalkWorkerPresenter(this);
        skill = getIntent().getStringExtra(IntentConfig.workerToTalkSkill);
        toTalkWorkerBean = (ToTalkWorkerBean) getIntent().getSerializableExtra(IntentConfig.toTalkWorker);
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        complainRl.setOnClickListener(this);
        iconIv.setOnClickListener(this);
        phoneIv.setOnClickListener(this);
        waitRl.setOnClickListener(this);
        cancelWorkerTv.setOnClickListener(this);
        surePriceTv.setOnClickListener(this);
    }

    private void loadData() {
        cpd.show();
        talkWorkerPresenter.load(NetConfig.workerUrl + "?u_id=" + toTalkWorkerBean.getWorkerId() + "&fu_id=" + UserUtils.readUserData(TalkWorkerActivity.this).getId() + "&o_status=0,-3");
    }

    private void notifyData() {
        Picasso.with(TalkWorkerActivity.this).load(workerBean.getIcon()).placeholder(R.mipmap.person_face_default).error(R.mipmap.person_face_default).into(iconIv);
        String sex = workerBean.getSex();
        if (sex.equals("0")) {
            sexIv.setImageResource(R.mipmap.female);
        } else if (sex.equals("1")) {
            sexIv.setImageResource(R.mipmap.male);
        }
        String u_id = workerBean.getWorkerId();
        if (u_id.equals(UserUtils.readUserData(TalkWorkerActivity.this).getId())) {
            SHOW_STATE = NO_YOURSELF;
        } else {
            int relation = workerBean.getRelation();
            if (relation == 1) {
                int relationType = workerBean.getRelation_type();
                if (relationType == 1) {
                    SHOW_STATE = DOING_FOR_YOU;
                } else if (relationType == 0) {
                    SHOW_STATE = TALK_TO_YOU;
                }
            } else {
                String status = workerBean.getStatus();
                if (status.equals("1")) {
                    SHOW_STATE = BUSY;
                } else if (status.equals("0")) {
                    SHOW_STATE = WAIT;
                }
            }
        }
        refreshState();
        nameTv.setText(workerBean.getTitle());
        skillTv.setText(workerBean.getSkillName());
        infoTv.setText(workerBean.getInfo());
        addressTv.setText(workerBean.getAddress());
        loadMap();
        cpd.dismiss();
    }

    private void refreshState() {
        switch (SHOW_STATE) {
            case NO_YOURSELF:
                noYourselfRl.setVisibility(View.VISIBLE);
                talkToYouRl.setVisibility(View.GONE);
                doingForyouRl.setVisibility(View.GONE);
                busyRl.setVisibility(View.GONE);
                waitRl.setVisibility(View.GONE);
                break;
            case TALK_TO_YOU:
                noYourselfRl.setVisibility(View.GONE);
                talkToYouRl.setVisibility(View.VISIBLE);
                doingForyouRl.setVisibility(View.GONE);
                busyRl.setVisibility(View.GONE);
                waitRl.setVisibility(View.GONE);
                break;
            case DOING_FOR_YOU:
                noYourselfRl.setVisibility(View.GONE);
                talkToYouRl.setVisibility(View.GONE);
                doingForyouRl.setVisibility(View.VISIBLE);
                busyRl.setVisibility(View.GONE);
                waitRl.setVisibility(View.GONE);
                break;
            case BUSY:
                noYourselfRl.setVisibility(View.GONE);
                talkToYouRl.setVisibility(View.GONE);
                doingForyouRl.setVisibility(View.GONE);
                busyRl.setVisibility(View.VISIBLE);
                waitRl.setVisibility(View.GONE);
                break;
            case WAIT:
                noYourselfRl.setVisibility(View.GONE);
                talkToYouRl.setVisibility(View.GONE);
                doingForyouRl.setVisibility(View.GONE);
                busyRl.setVisibility(View.GONE);
                waitRl.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void loadMap() {
        if (TextUtils.isEmpty(workerBean.getPositionX()) || workerBean.getPositionX().equals("null")) {
            workerBean.setPositionX("0");
        }
        if (TextUtils.isEmpty(workerBean.getPositionY()) || workerBean.getPositionY().equals("null")) {
            workerBean.setPositionY("0");
        }
        LatLng latLng = new LatLng(Double.parseDouble(workerBean.getPositionY()), Double.parseDouble(workerBean.getPositionX()));
        OverlayOptions overlayOptions = new MarkerOptions().position(latLng).icon(bitmap);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, Float.parseFloat("19"));
        baiduMap.animateMapStatus(mapStatusUpdate);
        baiduMap.addOverlay(overlayOptions);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_jump_worker_return:
                finish();
                break;
            case R.id.iv_jump_worker_icon:
                Intent intent = new Intent(TalkWorkerActivity.this, PersonDetailActivity.class);
                intent.putExtra(IntentConfig.talkToDetail, workerBean.getWorkerId());
                startActivity(intent);
                break;
            case R.id.iv_jump_worker_phone:
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + workerBean.getMobile()));
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(i);
                }
                break;
            case R.id.rl_talk_worker_busy:
                talkWorkerPresenter.check(NetConfig.taskBaseUrl + "?t_author=" + UserUtils.readUserData(TalkWorkerActivity.this).getId() + "&t_storage=0&t_status=0&skills=" + skill);
                break;
            case R.id.tv_talk_worker_cancel_worker:
                if (!cancelWorkerPop.isShowing()) {
                    backgroundAlpha(0.5f);
                    cancelWorkerPop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.tv_talk_worker_sure_price:
                switch (SHOW_STATE) {
                    case WORKER_WAIT:
                        if (!surePricePop.isShowing()) {
                            surePricePop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                        }
                        break;
                    case WAIT_WORKER:
                        Utils.log(TalkWorkerActivity.this, "wait worker");
                        break;
                }
                break;
            case R.id.rl_jump_worker_complain:
                Intent complainIntent = new Intent(TalkWorkerActivity.this, ComplainActivity.class);
                ToComplainBean toComplainBean = new ToComplainBean();
                toComplainBean.setAgainstId(toTalkWorkerBean.getWorkerId());
                toComplainBean.setCtType("2");
                complainIntent.putExtra(IntentConfig.toComplain, toComplainBean);
                startActivity(new Intent(complainIntent));
                break;
        }
    }

    @Override
    public void loadSuccess(String json) {
        workerBean = DataUtils.getWorkerBeanList(json).get(0);
        talkWorkerPresenter.getSkillJson(NetConfig.skillUrl);
    }

    @Override
    public void loadFailure(String failure) {
        handler.sendEmptyMessage(LOAD_FAILURE);
    }

    @Override
    public void getSkillSuccess(String json) {
        String skill = workerBean.getSkill();
        String[] skillArr = skill.split(",");
        List<String> skillList = new ArrayList<>();
        for (int i = 0; i < skillArr.length; i++) {
            skillList.add(skillArr[i]);
        }
        List<String> skillNameList = DataUtils.getSkillNameList(json, skillList);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < skillNameList.size(); i++) {
            sb.append(skillNameList.get(i));
            if (i != skillNameList.size() - 1) {
                sb.append("、");
            }
        }
        workerBean.setSkillName(sb.toString());
        handler.sendEmptyMessage(LOAD_SUCCESS);
    }

    @Override
    public void getSkillFailure(String failure) {

    }

    @Override
    public void checkSuccess(String json) {
        Utils.log(TalkWorkerActivity.this, json);
        if (DataUtils.getTaskBeanList(json).size() == 0) {
            handler.sendEmptyMessage(CHECK_NONE);
        } else {
            handler.sendEmptyMessage(CHECK_HAVE);
        }
    }

    @Override
    public void checkFailure(String failure) {

    }

    @Override
    public void cancelWorkerSuccess(String json) {
        Utils.log(TalkWorkerActivity.this, "cancelWorkerJson=" + json);
        handler.sendEmptyMessage(CANCEL_WORKER_SUCCESS);
    }

    @Override
    public void cancelWorkerFailure(String failure) {
        handler.sendEmptyMessage(CANCEL_WORKER_FAILURE);
    }

    @Override
    public void authorSureSuccess(String json) {
        Utils.log(TalkWorkerActivity.this, "authorSuccess=" + json);
        handler.sendEmptyMessage(AUTHOR_SURE_SUCCESS);
    }

    @Override
    public void authorSureFailure(String failure) {
        Utils.log(TalkWorkerActivity.this, "authorFailure=" + failure);
        handler.sendEmptyMessage(AUTHOR_SURE_FAILURE);
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = f;
        getWindow().setAttributes(layoutParams);
    }
}
