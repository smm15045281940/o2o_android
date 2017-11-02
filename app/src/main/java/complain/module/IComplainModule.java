package complain.module;


import bean.ToComplainBean;
import listener.JsonListener;

public interface IComplainModule {

    void userInfo(String url, JsonListener jsonListener);

    void userSkill(String url, JsonListener jsonListener);

    void userIssue(String url, JsonListener jsonListener);

    void submit(String url, ToComplainBean toComplainBean, JsonListener jsonListener);

    void cancelTask();
}
