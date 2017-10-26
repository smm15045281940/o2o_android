package talk.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

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

import java.util.ArrayList;
import java.util.List;

import config.NetConfig;
import talk.adapter.TalkEmployerAdapter;
import talk.bean.TalkEmployerBean;
import talk.presenter.TalkEmployerPresenter;
import utils.DataUtils;
import utils.UserUtils;
import utils.Utils;

public class TalkActivity extends AppCompatActivity implements ITalkActivity, View.OnClickListener {

    private View rootView;
    private RelativeLayout returnRl;
    private View headMapView;
    private MapView mapView;
    private ListView talkLv;
    private BaiduMap baiduMap;
    private LatLng point;
    private BitmapDescriptor bitmap;
    private OverlayOptions option;
    private MapStatusUpdate mapStatusUpdate;
    private String t_id;

    private TalkEmployerPresenter talkEmployerPresenter;

    private TalkEmployerBean talkEmployerBean;
    private List<String> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_talk, null);
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
        initHeadView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_return);
        talkLv = (ListView) rootView.findViewById(R.id.lv_talk);
    }

    private void initHeadView() {
        headMapView = LayoutInflater.from(TalkActivity.this).inflate(R.layout.head_map, null);
        talkLv.addHeaderView(headMapView);
        mapView = (MapView) headMapView.findViewById(R.id.mv_head_map);
        mapView.showScaleControl(false);
        mapView.showZoomControls(false);
    }

    private void initData() {
        talkEmployerPresenter = new TalkEmployerPresenter(this);
        idList = new ArrayList<>();
        t_id = getIntent().getStringExtra("t_id");
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
    }

    private void loadData() {
        talkEmployerPresenter.load(NetConfig.taskBaseUrl + "?action=info&o_worker=" + UserUtils.readUserData(TalkActivity.this).getId() + "&t_id=" + t_id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_talk_return:
                finish();
                break;
        }
    }

    @Override
    public void success(String json) {
        Utils.log(TalkActivity.this, json);
        talkEmployerBean = DataUtils.getTalkEmployerBean(json);
        for (int i = 0; i < talkEmployerBean.getTalkEmployerWorkerBeanList().size(); i++) {
            idList.add(talkEmployerBean.getTalkEmployerWorkerBeanList().get(i).getId());
        }
        Utils.log(TalkActivity.this, DataUtils.getTalkEmployerBean(json).toString());
        talkLv.setAdapter(new TalkEmployerAdapter(TalkActivity.this, talkEmployerBean));
        baiduMap = mapView.getMap();
        UiSettings settings = baiduMap.getUiSettings();
        settings.setAllGesturesEnabled(false);
        settings.setOverlookingGesturesEnabled(false);
        settings.setZoomGesturesEnabled(false);
        point = new LatLng(Double.parseDouble(talkEmployerBean.getPosY()), Double.parseDouble(talkEmployerBean.getPosX()));
        bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.point_blue);
        option = new MarkerOptions().position(point).icon(bitmap);
        mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(point, Float.parseFloat("19"));
        baiduMap.animateMapStatus(mapStatusUpdate);
        baiduMap.addOverlay(option);
        talkEmployerPresenter.loadSkill(NetConfig.skillBaseUrl);
    }

    @Override
    public void failure(String failure) {
        Utils.log(TalkActivity.this, failure);
    }

    @Override
    public void skillSuccess(String json) {
        Utils.log(TalkActivity.this, "skillJson=" + json);
        Utils.log(TalkActivity.this, DataUtils.getSkillNameList(json, idList).toString());
    }


    @Override
    public void skillFailure(String failure) {

    }
}
