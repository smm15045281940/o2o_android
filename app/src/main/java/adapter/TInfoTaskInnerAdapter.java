package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjzg.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import bean.TInfoOrderBean;
import listener.IdPosClickHelp;
import view.CImageView;

/**
 * Created by Administrator on 2017/11/5.
 */
//任务详情内部适配器
public class TInfoTaskInnerAdapter extends BaseAdapter {

    private Context context;
    private List<TInfoOrderBean> list;
    private IdPosClickHelp idPosClickHelp;

    public TInfoTaskInnerAdapter(Context context, List<TInfoOrderBean> list, IdPosClickHelp idPosClickHelp) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tinfotask_inner, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TInfoOrderBean tInfoOrderBean = list.get(position);
        if (tInfoOrderBean != null) {
            Picasso.with(context).load(tInfoOrderBean.getU_img()).into(holder.iconIv);
            String sex = tInfoOrderBean.getU_sex();
            if (sex.equals("0")) {
                holder.sexIv.setImageResource(R.mipmap.female);
            } else if (sex.equals("1")) {
                holder.sexIv.setImageResource(R.mipmap.male);
            }
            String status = tInfoOrderBean.getU_task_status();
            if (status.equals("0")) {
                holder.statusIv.setImageResource(R.mipmap.worker_leisure);
            } else if (status.equals("1")) {
                holder.statusIv.setImageResource(R.mipmap.worker_mid);
            }
            holder.nameTv.setText(tInfoOrderBean.getU_true_name());
            //TODO skill
        }
        return convertView;
    }

    private class ViewHolder {

        private CImageView iconIv, mobileIv;
        private ImageView sexIv, statusIv;
        private TextView nameTv, skillTv;

        public ViewHolder(View itemView) {
            iconIv = (CImageView) itemView.findViewById(R.id.iv_item_tinfotask_inner_icon);
            mobileIv = (CImageView) itemView.findViewById(R.id.iv_item_tinfotask_inner_mobile);
            sexIv = (ImageView) itemView.findViewById(R.id.iv_item_tinfotask_inner_sex);
            statusIv = (ImageView) itemView.findViewById(R.id.iv_item_tinfotask_inner_status);
            nameTv = (TextView) itemView.findViewById(R.id.tv_item_tinfotask_inner_name);
            skillTv = (TextView) itemView.findViewById(R.id.tv_item_tinfotask_inner_skill);
        }
    }
}
