package getevaluate.presenter;

import android.os.Handler;

import java.util.List;

import getevaluate.listener.GetEvaluateListener;
import getevaluate.module.GetEvaluateModule;
import getevaluate.module.IGetEvaluateModule;
import getevaluate.view.IGetEvaluateFragment;
import myevaluate.bean.MyEvaluateBean;

/**
 * Created by Administrator on 2017/10/25.
 */

public class GetEvaluatePresenter implements IGetEvaluatePresenter {

    private IGetEvaluateFragment getEvaluateFragment;
    private IGetEvaluateModule getEvaluateModule;
    private Handler handler;

    public GetEvaluatePresenter(IGetEvaluateFragment getEvaluateFragment) {
        this.getEvaluateFragment = getEvaluateFragment;
        getEvaluateModule = new GetEvaluateModule();
        handler = new Handler();
    }

    @Override
    public void load(String url) {
        getEvaluateModule.load(url, new GetEvaluateListener() {
            @Override
            public void success(final List<MyEvaluateBean> myEvaluateBeanList) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getEvaluateFragment.success(myEvaluateBeanList);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getEvaluateFragment.failure(failure);
                    }
                });
            }
        });
    }

    @Override
    public void destroy() {
        if (getEvaluateModule != null) {
            getEvaluateModule.cancelTask();
            getEvaluateModule = null;
        }
        if (handler != null) {
            handler = null;
        }
        if (getEvaluateFragment != null) {
            getEvaluateFragment = null;
        }
    }
}
