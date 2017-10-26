package getevaluate.module;

import getevaluate.listener.GetEvaluateListener;

/**
 * Created by Administrator on 2017/10/25.
 */

public interface IGetEvaluateModule {

    void load(String url, GetEvaluateListener getEvaluateListener);

    void cancelTask();
}
