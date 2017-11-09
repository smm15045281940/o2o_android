package adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjzg.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import bean.LonLatBean;
import listener.IdPosClickHelp;
import bean.TaskBean;
import utils.DataUtils;
import utils.UserUtils;

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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TaskBean taskBean = list.get(position);
        Picasso.with(context).load(taskBean.getIcon()).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.iconIv);
        holder.titleTv.setText(taskBean.getTitle());
        holder.infoTv.setText(taskBean.getInfo());
        String status = taskBean.getStatus();
        if (status.equals("0")) {
            holder.statusIv.setImageResource(R.mipmap.worker_wait);
        } else if (status.equals("1")) {
            holder.statusIv.setImageResource(R.mipmap.worker_talk);
        } else if (status.equals("-3") || status.equals("2") || status.equals("5")) {
            holder.statusIv.setImageResource(R.mipmap.worker_mid);
        } else if (status.equals("3") || status.equals("4")) {
            holder.statusIv.setImageResource(R.mipmap.worker_over);
        }
        LonLatBean lonLatBean1 = UserUtils.getLonLat(context);
        LonLatBean lonLatBean2 = new LonLatBean(taskBean.getPosX(), taskBean.getPosY());
        String distance = DataUtils.getDistance(lonLatBean1, lonLatBean2);
        if (TextUtils.isEmpty(distance)) {
            holder.distanceTv.setText("无法获取距离");
        } else {
            holder.distanceTv.setText("离我" + distance + "公里");
        }
        switch (taskBean.getFavorite()) {
            case 0:
                holder.collectIv.setImageResource(R.mipmap.collect_gray);
                break;
            case 1:
                holder.collectIv.setImageResource(R.mipmap.collect_yellow);
                break;
        }
        final int pos = position;
        final int llId = holder.ll.getId();
        final int collectId = holder.collectIv.getId();
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idPosClickHelp.onClick(llId, pos);
            }
        });
        holder.collectIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idPosClickHelp.onClick(collectId, pos);
            }
        });
        return convertView;
    }

    private class ViewHolder {

        private LinearLayout ll;
        private TextView titleTv, infoTv, distanceTv;
        private ImageView iconIv, statusIv, collectIv;

        public ViewHolder(View itemView) {
            ll = (LinearLayout) itemView.findViewById(R.id.ll_item_task);
            titleTv = (TextView) itemView.findViewById(R.id.tv_item_task_name);
            infoTv = (TextView) itemView.findViewById(R.id.tv_item_task_info);
            iconIv = (ImageView) itemView.findViewById(R.id.iv_item_task_icon);
            statusIv = (ImageView) itemView.findViewById(R.id.iv_item_task_status);
            collectIv = (ImageView) itemView.findViewById(R.id.iv_item_task_collect);
            distanceTv = (TextView) itemView.findViewById(R.id.tv_item_task_distance);
        }
    }
}
