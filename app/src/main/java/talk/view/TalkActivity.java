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
    private double latitude = 39.963175, longitude = 116.400244;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_talk, null);
        setContentView(rootView);
        initView();
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

    private void setListener() {
        returnRl.setOnClickListener(this);
    }

    private void loadData() {
        baiduMap = mapView.getMap();
        UiSettings settings = baiduMap.getUiSettings();
        settings.setAllGesturesEnabled(false);
        settings.setOverlookingGesturesEnabled(false);
        settings.setZoomGesturesEnabled(false);
        point = new LatLng(latitude, longitude);
        bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.point_blue);
        option = new MarkerOptions().position(point).icon(bitmap);
        mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(point, Float.parseFloat("19"));
        baiduMap.animateMapStatus(mapStatusUpdate);
        baiduMap.addOverlay(option);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i + "");
        }
        talkLv.setAdapter(new ArrayAdapter<String>(TalkActivity.this, android.R.layout.simple_list_item_1, list));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_talk_return:
                finish();
                break;
        }
    }
}
