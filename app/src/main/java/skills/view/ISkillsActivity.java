package skills.view;


import java.util.List;

import skills.bean.SkillsBean;

public interface ISkillsActivity {

    void success(List<SkillsBean> skillsBeanList);

    void failure(String failure);

}
