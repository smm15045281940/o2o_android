package task.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjzg.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import listener.IdPosClickHelp;
import task.bean.TaskBean;

public class TaskAdapter extends BaseAdapter {

    private Context context;
    private List<TaskBean> list;
    private IdPosClickHelp idPosClickHelp;

    public TaskAdapter(Context context, List<TaskBean> list, IdPosClickHelp idPosClickHelp) {
        this.context = context;
        this.list = list;
        this.idPosClickHelp = idPosClickHelp;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TaskBean taskBean = list.get(position);
        holder.titleTv.setText(taskBean.getT_title());
        holder.infoTv.setText(taskBean.getT_info());
        String status = taskBean.getT_status();
        if (status.equals("0")) {
            holder.statusIv.setImageResource(R.mipmap.worker_wait);
        } else if (status.equals("1")) {
            holder.statusIv.setImageResource(R.mipmap.worker_talk);
        } else if (status.equals("2")) {
            holder.statusIv.setImageResource(R.mipmap.worker_mid);
        } else if (status.equals("3")) {
            holder.statusIv.setImageResource(R.mipmap.worker_over);
        }
        switch (taskBean.getFavorate()) {
            case 0:
                holder.collectIv.setImageResource(R.mipmap.collect_gray);
                break;
            case 1:
                holder.collectIv.setImageResource(R.mipmap.collect_yellow);
                break;
        }
        Picasso.with(context).load(taskBean.getU_img()).placeholder(R.mipmap.person_face_default).into(holder.iconIv);

        return convertView;
    }

    private class ViewHolder {

        private LinearLayout ll;
        private TextView titleTv, infoTv;
        private ImageView iconIv, statusIv, collectIv;

        public ViewHolder(View itemView) {
            ll = (LinearLayout) itemView.findViewById(R.id.ll_item_task);
            titleTv = (TextView) itemView.findViewById(R.id.tv_item_task_name);
            infoTv = (TextView) itemView.findViewById(R.id.tv_item_task_info);
            iconIv = (ImageView) itemView.findViewById(R.id.iv_item_task_icon);
            statusIv = (ImageView) itemView.findViewById(R.id.iv_item_task_status);
            collectIv = (ImageView) itemView.findViewById(R.id.iv_item_task_collect);
        }
    }
}
