package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.GenderInfo;
import com.chaojishipin.sarrs.bean.VideoItem;
import com.chaojishipin.sarrs.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xll on 2015/6/6.
 *
 * @des @des 修改用户资料界面--性别选择适配器
 */
public class ModifyUserListViewAdapter extends BaseAdapter {

    private Context context;
    private int clickId=2;
    private List<GenderInfo> listInfo=new ArrayList<>();
   public void setGenderList(List<GenderInfo> genders){
       this.listInfo=genders;
   }
    public ModifyUserListViewAdapter(Context context){
        this.context=context;
        //init();
    }
   public void setClicId(int id){
       this.clickId=id;
   }
    public List<GenderInfo> getGenderList(){
        return listInfo;
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
        ModifyUserListViewHolder holder = null;
        if (convertView == null) {
            holder = new ModifyUserListViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.modifyactivity_listview_item, parent, false);
            holder.mText = (TextView) convertView.findViewById(R.id.mdetail_activity_listview_title);
            holder.mImg = (ImageView) convertView.findViewById(R.id.mdetail_activity_listview_img);
            convertView.setTag(holder);
        } else {
            holder = (ModifyUserListViewHolder) convertView.getTag();
        }
            if( listInfo.get(position).isClick()) {
                holder.mImg.setVisibility(View.VISIBLE);
                LogUtil.e("isClick ","click "+position);
            }else {
                holder.mImg.setVisibility(View.GONE);
                LogUtil.e("isClick ", "unclick " + position);
            }
         holder.mText.setText(listInfo.get(position).getGender());
        return convertView;
    }


    class ModifyUserListViewHolder {
        public TextView mText;
        public ImageView mImg;


    }


}
