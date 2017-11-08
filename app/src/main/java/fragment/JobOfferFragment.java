package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import adapter.MsgAdapter;
import bean.MsgBean;
import config.StateConfig;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;

public class JobOfferFragment extends Fragment implements PullToRefreshLayout.OnRefreshListener {

    private View rootView;
    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private List<MsgBean> list;
    private MsgAdapter adapter;
    private int state = StateConfig.LOAD_DONE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.common_listview, null);
        initView();
        initData();
        setData();
        setListener();
        loadData();
        return rootView;
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
    }

    private void initData() {
        list = new ArrayList<>();
        adapter = new MsgAdapter(getActivity(), list);
    }

    private void setData() {
        plv.setAdapter(adapter);
    }

    private void setListener() {
        ptrl.setOnRefreshListener(this);
    }

    private void loadData() {
        MsgBean mo0 = new MsgBean();
        mo0.setTitle("工作邀约");
        mo0.setDate("2017/03/07");
        mo0.setDes("有一份适合你的工作，雇主已经向您发起邀约，等待您同意");
        mo0.setArrowShow(true);
        MsgBean mo1 = new MsgBean();
        mo1.setTitle("工作邀约");
        mo1.setDate("2017/03/06");
        mo1.setDes("您发布的工作有工人感兴趣并向您发起邀约，等待您同意");
        mo1.setArrowShow(true);
        list.add(mo0);
        list.add(mo1);
        adapter.notifyDataSetChanged();
        switch (state) {
            case StateConfig.LOAD_REFRESH:
                ptrl.hideHeadView();
                break;
            case StateConfig.LOAD_LOAD:
                ptrl.hideFootView();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        list.clear();
        state = StateConfig.LOAD_REFRESH;
        loadData();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        state = StateConfig.LOAD_LOAD;
        loadData();
    }
}
