package worker.presenter;

import android.os.Handler;

import java.util.List;

import listener.JsonListener;
import worker.bean.WorkerBean;
import worker.listener.FavoriteAddListener;
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
    public void load(String url) {
        iWorkerModule.load(url, new WorkerListener() {
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
    public void favoriteAdd(String url) {
        iWorkerModule.favoriteAdd(url, new FavoriteAddListener() {
            @Override
            public void success(final String success) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iWorkerActivity.collectSuccess(success);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iWorkerActivity.collectFailure(failure);
                    }
                });
            }
        });
    }

    @Override
    public void favoriteDel(String url) {
        iWorkerModule.favoriteDel(url, new JsonListener() {
            @Override
            public void success(final String json) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iWorkerActivity.cancelCollectSuccess(json);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iWorkerActivity.cancelCollectFailure(failure);
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
