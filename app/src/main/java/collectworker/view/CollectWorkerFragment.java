package collectworker.view;

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

import adapter.CollectWorkerAdapter;
import bean.WorkerBean;
import collectworker.presenter.CollectWorkerPresenter;
import collectworker.presenter.ICollectWorkerPresenter;
import config.NetConfig;
import listener.IdPosClickHelp;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import utils.UserUtils;
import utils.Utils;
import view.CProgressDialog;

public class CollectWorkerFragment extends Fragment implements ICollectWorkerFragment, PullToRefreshLayout.OnRefreshListener, IdPosClickHelp {

    private View rootView;
    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private CProgressDialog cpd;
    private List<WorkerBean> list;
    private CollectWorkerAdapter adapter;

    private ICollectWorkerPresenter collectWorkerPresenter;
    private int cancelCollectPosition;

    private final int DONE = 0;
    private final int CANCEL_COLLECT_SUCCESS = 1;
    private final int CANCEL_COLLECT_FAILURE = 2;
    private String tip;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case DONE:
                        adapter.notifyDataSetChanged();
                        break;
                    case CANCEL_COLLECT_SUCCESS:
                        Utils.toast(getActivity(), tip);
                        list.remove(cancelCollectPosition);
                        adapter.notifyDataSetChanged();
                        break;
                    case CANCEL_COLLECT_FAILURE:
                        Utils.toast(getActivity(), tip);
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
        handler.removeMessages(1);
        if (collectWorkerPresenter != null) {
            collectWorkerPresenter.destroy();
            collectWorkerPresenter = null;
        }
    }

    private void initView() {
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
        cpd = Utils.initProgressDialog(getActivity(), cpd);
    }

    private void initData() {
        collectWorkerPresenter = new CollectWorkerPresenter(this);
        list = new ArrayList<>();
        adapter = new CollectWorkerAdapter(getActivity(), list, this);
    }

    private void setData() {
        plv.setAdapter(adapter);
    }

    private void setListener() {
        ptrl.setOnRefreshListener(this);
    }

    private void loadData() {
        collectWorkerPresenter.load(UserUtils.readUserData(getActivity()).getId());
    }

    @Override
    public void loadSuccess(String loadJson) {

    }

    @Override
    public void loadFailure(String failure) {

    }

    @Override
    public void cancelCollectSuccess(String cancelCollectJson) {

    }

    @Override
    public void cancelCollectFailure(String failure) {

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        ptrl.hideHeadView();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        ptrl.hideFootView();
    }

    @Override
    public void onClick(int id, int pos) {
        switch (id) {
            case R.id.ll_item_worker:
                Log.e("TAG", "jump");
                break;
            case R.id.iv_item_worker_collect:
                Log.e("TAG", "cancel");
                cancelCollectPosition = pos;
                collectWorkerPresenter.cancelCollect(NetConfig.favorateDelUrl + "?f_id=" + list.get(cancelCollectPosition).getCollectId());
                break;
        }
    }
}
