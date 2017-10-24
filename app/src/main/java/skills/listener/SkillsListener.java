package skills.listener;


import java.util.List;

import skills.bean.SkillsBean;

public interface SkillsListener {

    void success(List<SkillsBean> skillsBeanList);

    void failure(String failure);
}
