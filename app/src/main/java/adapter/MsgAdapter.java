package adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjzg.R;

import java.util.List;

import bean.Msg;

/**
 * 创建日期：2017/8/22 on 15:52
 * 作者:孙明明
 * 描述:工作邀约-系统消息适配器
 */

public class MsgAdapter extends CommonAdapter<Msg> {

    public MsgAdapter(Context context, List<Msg> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_msg, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Msg msg = list.get(position);
        if (msg != null) {
            holder.titleTv.setText(msg.getTitle());
            holder.dateTv.setText(msg.getDate());
            holder.desTv.setText(msg.getDes());
            if (msg.isArrowShow()) {
                holder.arrowIv.setVisibility(View.VISIBLE);
            } else {
                holder.arrowIv.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private class ViewHolder {

        private TextView titleTv, dateTv, desTv;
        private ImageView arrowIv;

        public ViewHolder(View itemView) {
            titleTv = (TextView) itemView.findViewById(R.id.tv_item_msg_title);
            dateTv = (TextView) itemView.findViewById(R.id.tv_item_msg_date);
            desTv = (TextView) itemView.findViewById(R.id.tv_item_msg_des);
            arrowIv = (ImageView) itemView.findViewById(R.id.iv_item_msg_arrow);
        }
    }
}
