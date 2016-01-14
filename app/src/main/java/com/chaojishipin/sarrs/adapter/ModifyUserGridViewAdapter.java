package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.GenderInfo;
import com.chaojishipin.sarrs.bean.ImageInfo;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xll on 2015/8/5.
 *
 * @des 修改用户资料界面--头像选择适配器
 */
public class ModifyUserGridViewAdapter extends BaseAdapter {

    private Context context;
    private int clickId;
//    private String dirPath;

    private List<ImageInfo> listInfo=new ArrayList<ImageInfo>();

    public ModifyUserGridViewAdapter(Context context){
        this.context=context;

    }

//    public String getDirPath(){
//        return dirPath;
//    }
   public void setData(List<ImageInfo> listInfo){
       this.listInfo=listInfo;
//       this.dirPath=dirPath;
   }


    @Override
    public int getCount() {
        return listInfo.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listInfo.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ModifyUserGridViewHolder holder = null;
        if (convertView == null) {
            holder = new ModifyUserGridViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.modifyactivity_gridview_item, parent, false);
            holder.mImg = (ImageView) convertView.findViewById(R.id.mdetail_activity_gridview_img);
            convertView.setTag(holder);
        } else {
            holder = (ModifyUserGridViewHolder) convertView.getTag();
        }


       if(listInfo.get(position).getType()==0){
            // holder.mImg.setColorFilter(null);
            //holder.mImg.setImageResource(R.drawable.sarrs_pic_no_default);
            holder.mImg.setBackgroundColor(context.getResources().getColor(R.color.color_00000000));
            String imgUrl=listInfo.get(position).getUrl();
            ImageLoader.getInstance().displayImage(imgUrl, holder.mImg);
            LogUtil.e("ImageGridAdapter",""+imgUrl);
       }else{
            //holder.mImg.setColorFilter(null);
            holder.mImg.setBackgroundColor(context.getResources().getColor(R.color.color_999999));
            holder.mImg.setImageResource(R.drawable.sarrs_pic_camera);
        }

        return convertView;
    }


    class ModifyUserGridViewHolder {
        public ImageView mImg;


    }


}

