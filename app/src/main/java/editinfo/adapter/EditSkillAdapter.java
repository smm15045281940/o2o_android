package editinfo.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gjzg.R;

import java.util.List;

import bean.SkillBean;

public class EditSkillAdapter extends BaseAdapter {

    private Context context;
    private List<SkillBean> list;

    public EditSkillAdapter(Context context, List<SkillBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoler holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_edit_skill, null);
            holder = new ViewHoler(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHoler) convertView.getTag();
        }
        SkillBean skillBean = list.get(position);
        if (skillBean != null) {
            holder.textView.setText(skillBean.getName());
        }
        return convertView;
    }

    private class ViewHoler {

        private TextView textView;

        public ViewHoler(View itemView) {
            textView = (TextView) itemView.findViewById(R.id.tv_item_edit_skill);
        }
    }
}