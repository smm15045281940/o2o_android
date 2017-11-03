package editinfo.listener;


import java.util.List;

import bean.SkillsBean;

public interface AddSkillListener {

    void success(List<SkillsBean> skillsBeanList);

    void failure(String failure);
}
