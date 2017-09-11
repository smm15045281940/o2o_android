package adapter;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gjzg.R;

import java.util.List;

public class ComplainImageAdapter extends CommonAdapter<String> {

    private BitmapFactory.Options options;

    public ComplainImageAdapter(Context context, List<String> list) {
        super(context, list);
        options = new BitmapFactory.Options();
        options.inSampleSize = 10;
        options.inTempStorage = new byte[1024];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_complain_image, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageIv.setImageBitmap(BitmapFactory.decodeFile(list.get(position), options));
        return convertView;
    }

    private class ViewHolder {

        private ImageView imageIv;

        public ViewHolder(View itemView) {
            imageIv = (ImageView) itemView.findViewById(R.id.iv_item_complain_image);
        }
    }
}
