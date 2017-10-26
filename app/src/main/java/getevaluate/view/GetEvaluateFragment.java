package getevaluate.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import getevaluate.presenter.GetEvaluatePresenter;
import getevaluate.presenter.IGetEvaluatePresenter;
import myevaluate.adapter.MyEvaluateAdapter;
import myevaluate.bean.MyEvaluateBean;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;

public class GetEvaluateFragment extends Fragment implements IGetEvaluateFragment, PullToRefreshLayout.OnRefreshListener {

    private View rootView;
    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private List<MyEvaluateBean> list;
    private MyEvaluateAdapter adapter;
    private IGetEvaluatePresenter getEvaluatePresenter;

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
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
    }

    private void initData() {
        list = new ArrayList<>();
        adapter = new MyEvaluateAdapter(getActivity(), list);
        getEvaluatePresenter = new GetEvaluatePresenter(this);
    }

    private void setData() {
        plv.setAdapter(adapter);
    }

    private void setListener() {
        ptrl.setOnRefreshListener(this);
    }

    private void loadData() {
        getEvaluatePresenter.load("http://api.gangjianwang.com/Users/otherCommentUser?tc_u_id=2&page=1");
    }

    @Override
    public void success(List<MyEvaluateBean> myEvaluateBeanList) {

    }

    @Override
    public void failure(String failure) {

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        ptrl.hideHeadView();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        ptrl.hideFootView();
    }
}
