package talk.presenter;

import android.os.Handler;

import talk.listener.LoadSkillListener;
import talk.listener.TalkEmployerListener;
import talk.module.ITalkEmployerModule;
import talk.module.TalkEmployerModule;
import talk.view.ITalkActivity;

/**
 * Created by Administrator on 2017/10/26.
 */

public class TalkEmployerPresenter implements ITalkEmployerPresenter {

    private ITalkActivity talkActivity;
    private ITalkEmployerModule talkEmployerModule;
    private Handler handler;

    public TalkEmployerPresenter(ITalkActivity talkActivity) {
        this.talkActivity = talkActivity;
        talkEmployerModule = new TalkEmployerModule();
        handler = new Handler();
    }

    @Override
    public void load(String url) {
        talkEmployerModule.load(url, new TalkEmployerListener() {
            @Override
            public void success(final String json) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        talkActivity.success(json);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        talkActivity.failure(failure);
                    }
                });
            }
        });
    }

    @Override
    public void loadSkill(String url) {
        talkEmployerModule.loadSkill(url, new LoadSkillListener() {
            @Override
            public void success(final String json) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        talkActivity.skillSuccess(json);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        talkActivity.skillFailure(failure);
                    }
                });
            }
        });
    }

    @Override
    public void destroy() {
        if (talkEmployerModule != null) {
            talkEmployerModule.cancelTask();
            talkEmployerModule = null;
        }
        if (handler != null) {
            handler = null;
        }
        if (talkActivity != null) {
            talkActivity = null;
        }
    }
}
