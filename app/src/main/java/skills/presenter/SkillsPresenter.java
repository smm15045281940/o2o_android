package skills.presenter;


import android.os.Handler;

import java.util.List;

import skills.bean.SkillsBean;
import skills.listener.SkillsListener;
import skills.module.ISkillsModule;
import skills.module.SkillsModule;
import skills.view.ISkillsActivity;

public class SkillsPresenter implements ISkillsPresenter {

    private ISkillsActivity iSkillsActivity;
    private ISkillsModule iSkillsModule;
    private Handler mHandler = new Handler();

    public SkillsPresenter(ISkillsActivity iSkillsActivity) {
        this.iSkillsActivity = iSkillsActivity;
        iSkillsModule = new SkillsModule();
    }

    @Override
    public void load() {
        iSkillsModule.load(new SkillsListener() {
            @Override
            public void success(final List<SkillsBean> skillsBeanList) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iSkillsActivity.success(skillsBeanList);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iSkillsActivity.failure(failure);
                    }
                });
            }
        });
    }

    @Override
    public void destroy() {
        iSkillsModule.cancelTask();
        if (iSkillsModule != null)
            iSkillsModule = null;
        if (iSkillsActivity != null)
            iSkillsActivity = null;
        if (mHandler != null)
            mHandler = null;
    }
}
