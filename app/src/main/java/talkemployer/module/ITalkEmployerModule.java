package talkemployer.module;

import com.gjzg.listener.JsonListener;

/**
 * Created by Administrator on 2017/10/26.
 */

public interface ITalkEmployerModule {

    void load(String url, JsonListener jsonListener);

    void loadSkill(String url, JsonListener jsonListener);

    void invite(String url,JsonListener jsonListener);

    void cancelTask();
}
