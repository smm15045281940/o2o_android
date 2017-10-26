package talk.module;

import talk.listener.LoadSkillListener;
import talk.listener.TalkEmployerListener;

/**
 * Created by Administrator on 2017/10/26.
 */

public interface ITalkEmployerModule {

    void load(String url, TalkEmployerListener talkEmployerListener);

    void loadSkill(String url, LoadSkillListener loadSkillListener);

    void cancelTask();
}
