package complain.presenter;


import bean.ToComplainBean;

public interface IComplainPresenter {

    void userInfo(String url);

    void userSkill(String url);

    void userIssue(String url);

    void submit(String url, ToComplainBean toComplainBean);

    void destory();
}
