package changeprice.presenter;

import android.os.Handler;

import changeprice.module.ChangePriceModule;
import changeprice.module.IChangePriceModule;
import changeprice.view.IChangePriceActivity;
import listener.JsonListener;

/**
 * Created by Administrator on 2017/10/31.
 */

public class ChangePricePresenter implements IChangePricePresenter {

    private IChangePriceActivity changePriceActivity;
    private IChangePriceModule changePriceModule;
    private Handler handler;

    public ChangePricePresenter(IChangePriceActivity changePriceActivity) {
        this.changePriceActivity = changePriceActivity;
        changePriceModule = new ChangePriceModule();
        handler = new Handler();
    }

    @Override
    public void load(String url) {
        changePriceModule.load(url, new JsonListener() {
            @Override
            public void success(final String json) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changePriceActivity.loadSuccess(json);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changePriceActivity.loadFailure(failure);
                    }
                });
            }
        });
    }

    @Override
    public void change(String url) {
        changePriceModule.change(url, new JsonListener() {
            @Override
            public void success(final String json) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changePriceActivity.changeSuccess(json);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changePriceActivity.changeFailure(failure);
                    }
                });
            }
        });
    }

    @Override
    public void destroy() {
        if (changePriceModule != null) {
            changePriceModule.cancelTask();
            changePriceModule = null;
        }
        if (handler != null) {
            handler = null;
        }
        if (changePriceActivity != null) {
            changePriceActivity = null;
        }
    }
}
