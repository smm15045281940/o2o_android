package worker.presenter;

import android.os.Handler;

import java.util.List;

import bean.PositionBean;
import worker.bean.WorkerBean;
import worker.listener.WorkerListener;
import worker.module.IWorkerModule;
import worker.module.WorkerModule;
import worker.view.IWorkerActivity;

public class WorkerPresenter implements IWorkerPresenter {

    private IWorkerActivity iWorkerActivity;
    private IWorkerModule iWorkerModule;
    private Handler mHandler = new Handler();

    public WorkerPresenter(IWorkerActivity iWorkerActivity) {
        this.iWorkerActivity = iWorkerActivity;
        iWorkerModule = new WorkerModule();
    }

    @Override
    public void load(String workerKindId, PositionBean positionBean) {
        iWorkerModule.load(workerKindId, positionBean, new WorkerListener() {
            @Override
            public void success(final List<WorkerBean> workerBeanList) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iWorkerActivity.success(workerBeanList);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iWorkerActivity.failure(failure);
                    }
                });
            }
        });
    }

    @Override
    public void destroy() {
        iWorkerModule.cancelTask();
        if (iWorkerModule != null)
            iWorkerModule = null;
        if (iWorkerActivity != null)
            iWorkerActivity = null;
        if (mHandler != null)
            mHandler = null;

    }
}
