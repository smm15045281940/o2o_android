package collectjob.view;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import collectjob.adapter.CollectJobAdapter;
import collectjob.bean.CollectJobBean;
import collectjob.presenter.CollectJobPresenter;
import collectjob.presenter.ICollectJobPresenter;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import utils.UserUtils;
import utils.Utils;
import view.CProgressDialog;

public class CollectJobFragment extends Fragment implements ICollectJobFragment, PullToRefreshLayout.OnRefreshListener {

    private View rootView;
    private CProgressDialog cpd;
    private ICollectJobPresenter collectJobPresenter;

    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private List<CollectJobBean> list;
    private CollectJobAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case 1:
                        cpd.dismiss();
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    };

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (collectJobPresenter != null) {
            collectJobPresenter.destroy();
            collectJobPresenter = null;
        }
        if (handler != null) {
            handler.removeMessages(1);
            handler = null;
        }
    }

    private void initView() {
        cpd = Utils.initProgressDialog(getActivity(), cpd);
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
    }

    private void initData() {
        collectJobPresenter = new CollectJobPresenter(this);
        list = new ArrayList<>();
        adapter = new CollectJobAdapter(getActivity(), list);
    }

    private void setData() {
        plv.setAdapter(adapter);
    }

    private void setListener() {
        ptrl.setOnRefreshListener(this);
    }

    private void loadData() {
        cpd.show();
        collectJobPresenter.load(UserUtils.readUserData(getActivity()).getId());
    }

    @Override
    public void showLoadSuccess(List<CollectJobBean> collectJobBeanList) {
        list.addAll(collectJobBeanList);
        handler.sendEmptyMessage(1);
    }

    @Override
    public void showLoadFailure(String failure) {
        Log.e("CollectJob", "failure=" + failure);
        cpd.dismiss();
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
