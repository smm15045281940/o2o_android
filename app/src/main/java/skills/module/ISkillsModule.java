package skills.module;


import skills.listener.SkillsListener;

public interface ISkillsModule {

    void load(SkillsListener skillsListener);

    void cancelTask();
}
