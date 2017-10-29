package collectworker.presenter;


import android.os.Handler;

import java.util.List;

import collectworker.module.CollectWorkerModule;
import collectworker.module.ICollectWorkerModule;
import collectworker.view.ICollectWorkerFragment;

public class CollectWorkerPresenter implements ICollectWorkerPresenter {

    private ICollectWorkerFragment iCollectWorkerFragment;
    private ICollectWorkerModule iCollectWorkerModule;
    private Handler mHandler;

    public CollectWorkerPresenter(ICollectWorkerFragment iCollectWorkerFragment) {
        this.iCollectWorkerFragment = iCollectWorkerFragment;
        iCollectWorkerModule = new CollectWorkerModule();
        mHandler = new Handler();
    }

    @Override
    public void load(String url) {

    }

    @Override
    public void cancelCollect(String url) {

    }

    @Override
    public void destroy() {
        if (iCollectWorkerModule != null) {
            iCollectWorkerModule.cancelTask();
            iCollectWorkerModule = null;
        }
        if (mHandler != null) {
            mHandler = null;
        }
        if (iCollectWorkerFragment != null) {
            iCollectWorkerFragment = null;
        }
    }
}
