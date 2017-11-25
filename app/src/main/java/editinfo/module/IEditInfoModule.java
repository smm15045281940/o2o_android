package editinfo.module;


import editinfo.listener.AddSkillListener;
import editinfo.listener.SubmitListener;
import com.gjzg.bean.UserInfoBean;
import listener.JsonListener;

public interface IEditInfoModule {

    void skill(String url, JsonListener jsonListener);

    void load(String url, AddSkillListener addSkillListener);

    void submit(UserInfoBean userInfoBean, SubmitListener submitListener);

    void cancelTask();
}
