package editinfo.view;


import java.util.List;

import skills.bean.SkillsBean;

public interface IEditInfoFragment {

    void showLoading();

    void hideLoading();

    void showAddSkillSuccess(List<SkillsBean> skillsBeanList);

    void showAddSkillFailure(String failure);

    void showSubmitSuccess(String success);

    void showSubmitFailure(String failure);
}
