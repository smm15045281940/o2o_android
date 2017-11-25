package adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjzg.R;

import java.util.List;

import com.gjzg.bean.MessageBean;
import utils.DataUtils;

/**
 * 创建日期：2017/8/22 on 15:52
 * 作者:孙明明
 * 描述:工作邀约-系统消息适配器
 */

public class MsgAdapter extends BaseAdapter {

    private Context context;
    private List<MessageBean> list;

    public MsgAdapter(Context context, List<MessageBean> list) {
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_msg, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MessageBean messageBean = list.get(position);
        if (messageBean != null) {
            String status = messageBean.getUm_status();
            if (status == null || status.equals("null") || TextUtils.isEmpty(status)) {
            } else {
                if (status.equals("0")) {
                    holder.pointTipIv.setVisibility(View.VISIBLE);
                } else {
                    holder.pointTipIv.setVisibility(View.INVISIBLE);
                }
            }
            String title = messageBean.getWm_title();
            if (title == null || title.equals("null") || TextUtils.isEmpty(title)) {
            } else {
                holder.titleTv.setText(title);
            }
            String date = messageBean.getUm_in_time();
            if (date == null || date.equals("null") || TextUtils.isEmpty(date)) {
            } else {
                holder.dateTv.setText(DataUtils.times(date));
            }
            String content = messageBean.getWm_desc();
            if (content == null || content.equals("null") || TextUtils.isEmpty(content)) {
            } else {
                holder.contentTv.setText(content);
            }
        }
        return convertView;
    }

    private class ViewHolder {

        private ImageView pointTipIv;
        private TextView titleTv, dateTv, contentTv;

        public ViewHolder(View itemView) {
            pointTipIv = (ImageView) itemView.findViewById(R.id.iv_item_msg_point_tip);
            titleTv = (TextView) itemView.findViewById(R.id.tv_item_msg_title);
            dateTv = (TextView) itemView.findViewById(R.id.tv_item_msg_date);
            contentTv = (TextView) itemView.findViewById(R.id.tv_item_msg_content);
        }
    }
}
