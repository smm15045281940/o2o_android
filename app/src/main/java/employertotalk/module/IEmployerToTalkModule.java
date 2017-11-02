package employertotalk.module;

import listener.JsonListener;

/**
 * Created by Administrator on 2017/10/31.
 */

public interface IEmployerToTalkModule {

    void load(String url, JsonListener jsonListener);

    void skill(String url,JsonListener jsonListener);

    void cancelTask();
}
