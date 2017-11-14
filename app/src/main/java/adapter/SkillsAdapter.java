package adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjzg.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import bean.SkillsBean;

public class SkillsAdapter extends BaseAdapter {

    private Context context;
    private List<SkillsBean> list;

    public SkillsAdapter(Context context, List<SkillsBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_skills, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SkillsBean skillsBean = list.get(position);
        if (skillsBean != null) {
            String name = skillsBean.getS_name();
            if (name == null || name.equals("null") || TextUtils.isEmpty(name)) {
            } else {
                holder.nameTv.setText(skillsBean.getS_name());
            }
            String icon = skillsBean.getImg();
            if (icon == null || icon.equals("null") || TextUtils.isEmpty(icon)) {
            } else {
                Picasso.with(context).load(icon).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.iconIv);
            }
        }
        return convertView;
    }

    private class ViewHolder {

        private ImageView iconIv;
        private TextView nameTv;

        public ViewHolder(View itemView) {
            iconIv = (ImageView) itemView.findViewById(R.id.iv_item_skills_icon);
            nameTv = (TextView) itemView.findViewById(R.id.tv_item_skills_name);
        }
    }
}
