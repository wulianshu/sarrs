package com.chaojishipin.sarrs.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.Favorite;
import com.chaojishipin.sarrs.swipe.SwipeMenu;
import com.chaojishipin.sarrs.thirdparty.Constant;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.chaojishipin.sarrs.widget.DeleteRelativelayout;
import com.chaojishipin.sarrs.widget.EqualRatioImageView;
import com.chaojishipin.sarrs.widget.SarrsListItemView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by xll on 2015/6/6.
 *
 * @des 收藏列表选择适配器
 */
public class SaveListAdapter extends BaseAdapter {

    private Context context;
    private List<Favorite> listInfo;
    private boolean isShowCheck;
    private boolean isAllSelect;
    private boolean isDes;
    private int mPostion = -1;

    public int deletecount;
    public List<Boolean> isshowtimelist;

    public SaveListAdapter(Context context, List<Favorite> info) {
        this.context = context;
        this.listInfo = info;
    }

    public void setData(List<Favorite> datas) {
        this.listInfo = datas;
    }

    public void setIsShowCheck(boolean isShow) {
        isShowCheck = isShow;
    }

    public void setIsAllSelect(boolean isAll) {
        this.isAllSelect = isAll;
    }

    public void setIsAllDeSelect(boolean isDese) {
        this.isDes = isDese;
    }

    public void setClickPostion(int po) {
        this.mPostion = po;
    }

    @Override
    public int getCount() {
        return (listInfo == null) ? 0 : listInfo.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return (listInfo == null) ? null : listInfo.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SaveHolder holder = null;
        if (convertView == null) {
            holder = new SaveHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.save_row, parent, false);
            holder.save_line = (DeleteRelativelayout) convertView.findViewById(R.id.save_item);
            holder.mSaveDate = (TextView) convertView.findViewById(R.id.save_date);
            holder.mToggle = (ToggleButton) convertView.findViewById(R.id.save_toggle);
            holder.mBigTitle = (TextView) convertView.findViewById(R.id.save_title);
            holder.mupdate = (TextView) convertView.findViewById(R.id.save_update);
            holder.mHistory = (TextView) convertView.findViewById(R.id.save_history);
            holder.mImg = (EqualRatioImageView) convertView.findViewById(R.id.main_feed_small_poster);
            holder.time_tag = (RelativeLayout) convertView.findViewById(R.id.time_tag);
            holder.mCname = (TextView) convertView.findViewById(R.id.save_cname);

            convertView.setTag(holder);
        } else {
            holder = (SaveHolder) convertView.getTag();
        }
        if (isshowtimelist.get(position)) {
            holder.time_tag.setVisibility(View.VISIBLE);
            String date = listInfo.get(position).getCreateDate();
            String date2 = Utils.getVeiwTimeTag(String.valueOf(System.currentTimeMillis()));
            if (date != null) {
                if (date.equalsIgnoreCase(date2)) {
                    holder.mSaveDate.setText(context.getString(R.string.today));
                } else {
                    holder.mSaveDate.setText(date);
                }
            }


        } else {
            holder.time_tag.setVisibility(View.GONE);
        }


        if (isShowCheck) {
            holder.mToggle.setVisibility(View.VISIBLE);
            if (listInfo.get(position).isCheck()) {
                holder.mToggle.setBackgroundResource(R.drawable.radiobutton_red_bg);
            } else {
                holder.mToggle.setBackgroundResource(R.drawable.radiobutton_white_bg);
            }
        } else {
            holder.mToggle.setVisibility(View.GONE);
        }


        if (listInfo != null && listInfo.size() > 0) {
            holder.mBigTitle.setText(listInfo.get(position).getTitle());
            holder.mHistory.setText(listInfo.get(position).getHistory());

            DisplayImageOptions options1= new DisplayImageOptions.Builder()
                    .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565).showImageOnFail(R.drawable.sarrs_main_default)
                    .showImageForEmptyUri(R.drawable.sarrs_main_default)
                    .showImageOnLoading(R.drawable.sarrs_main_default)
                    .build();

            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(listInfo.get(position).getImg(), holder.mImg,options1);
            // TODO 拼接更新剧集显示
            if (!TextUtils.isEmpty(listInfo.get(position).getCid())) {
                // 电视剧
                if (listInfo.get(position).getCid().equalsIgnoreCase(ConstantUtils.TV_SERISE_CATEGORYID)) {
                    holder.mCname.setText(context.getString(R.string.TV_SERIES));
                    if (listInfo.get(position).getIsend() == 0) {
                        // holder.mupdate.setText(context.getString(R.string.save_latest) + listInfo.get(position).getLatestepisode() + context.getString(R.string.save_dsj)+context.getString(R.string.save_total)+listInfo.get(position).getTotalepisode()+context.getString(R.string.save_dsj));
                        holder.mupdate.setText(context.getString(R.string.save_latest) + listInfo.get(position).getLatestepisode() + context.getString(R.string.save_dsj) +context.getString(R.string.save_unend));
                    } else {
                        //holder.mupdate.setText(context.getString(R.string.save_latest) + listInfo.get(position).getLatestepisode() + context.getString(R.string.save_dsj) + context.getString(R.string.save_total) + listInfo.get(position).getTotalepisode() + context.getString(R.string.save_dsj) + context.getString(R.string.save_end));
                        holder.mupdate.setText( context.getString(R.string.save_total) + listInfo.get(position).getTotalepisode() + context.getString(R.string.save_dsj) + context.getString(R.string.save_end));

                    }
                    // 动漫
                } else if (listInfo.get(position).getCid().equalsIgnoreCase(ConstantUtils.CARTOON_CATEGORYID)) {
                    holder.mCname.setText(context.getString(R.string.CARTOON));
                    if (listInfo.get(position).getIsend() == 0) {
                        // holder.mupdate.setText(context.getString(R.string.save_latest) + listInfo.get(position).getLatestepisode() + context.getString(R.string.save_dsj)+context.getString(R.string.save_total)+listInfo.get(position).getTotalepisode()+context.getString(R.string.save_dsj));
                        holder.mupdate.setText(context.getString(R.string.save_latest) + listInfo.get(position).getLatestepisode() + context.getString(R.string.save_dsj) +context.getString(R.string.save_unend));
                    } else {
                        //holder.mupdate.setText(context.getString(R.string.save_latest) + listInfo.get(position).getLatestepisode() + context.getString(R.string.save_dsj) + context.getString(R.string.save_total) + listInfo.get(position).getTotalepisode() + context.getString(R.string.save_dsj) + context.getString(R.string.save_end));
                        holder.mupdate.setText( context.getString(R.string.save_total) + listInfo.get(position).getTotalepisode() + context.getString(R.string.save_dsj) + context.getString(R.string.save_end));

                    }
                    // 电影
                } else if (listInfo.get(position).getCid().equalsIgnoreCase(ConstantUtils.MOVIES_CATEGORYID)) {
                    holder.mCname.setText(context.getString(R.string.MOVIES));
                    holder.mupdate.setVisibility(View.GONE);
                    // 综艺
                } else if (listInfo.get(position).getCid().equalsIgnoreCase(ConstantUtils.VARIETY_CATEGORYID)) {
                    holder.mCname.setText(context.getString(R.string.VARIETY));
                    if (listInfo.get(position).getIsend() == 0) {
                        // holder.mupdate.setText(context.getString(R.string.save_latest) + listInfo.get(position).getLatestepisode() + context.getString(R.string.save_zy)+context.getString(R.string.save_total)+listInfo.get(position).getTotalepisode()+context.getString(R.string.save_zy));
                        holder.mupdate.setText(context.getString(R.string.save_latest) + listInfo.get(position).getLatestepisode() + context.getString(R.string.save_zy) + context.getString(R.string.save_unend));
                    } else {
                        // holder.mupdate.setText(context.getString(R.string.save_latest) + listInfo.get(position).getLatestepisode() + context.getString(R.string.save_zy)+context.getString(R.string.save_total)+listInfo.get(position).getTotalepisode()+context.getString(R.string.save_zy)+context.getString(R.string.save_end));
                        holder.mupdate.setText(context.getString(R.string.save_total) + listInfo.get(position).getTotalepisode() + context.getString(R.string.save_zy) + context.getString(R.string.save_end));

                    }

                    // 纪录片
                } else if (listInfo.get(position).getCid().equalsIgnoreCase(ConstantUtils.DOCUMENTARY_CATEGORYID)) {
                    holder.mCname.setText(context.getString(R.string.DOCUMENTARY));
                    if (listInfo.get(position).getIsend() == 0) {
                        // holder.mupdate.setText(context.getString(R.string.save_latest) + listInfo.get(position).getLatestepisode() + context.getString(R.string.save_zy)+context.getString(R.string.save_total)+listInfo.get(position).getTotalepisode()+context.getString(R.string.save_zy));
                        holder.mupdate.setText(context.getString(R.string.save_latest) + listInfo.get(position).getLatestepisode() + context.getString(R.string.save_zy) + context.getString(R.string.save_unend));
                    } else {
                        // holder.mupdate.setText(context.getString(R.string.save_latest) + listInfo.get(position).getLatestepisode() + context.getString(R.string.save_zy)+context.getString(R.string.save_total)+listInfo.get(position).getTotalepisode()+context.getString(R.string.save_zy)+context.getString(R.string.save_end));
                        holder.mupdate.setText(context.getString(R.string.save_total) + listInfo.get(position).getTotalepisode() + context.getString(R.string.save_zy) + context.getString(R.string.save_end));

                    }

                } else {
                    holder.mCname.setText(context.getString(R.string.OTHER));
                    holder.mupdate.setVisibility(View.GONE);

                }
            } else {
                if (listInfo.get(position).getType().equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_SPECIAL)) {
                    holder.mCname.setText(context.getString(R.string.SPECIAL));
                    if (!TextUtils.isEmpty(listInfo.get(position).getDataCount())) {
                        holder.mupdate.setText(context.getString(R.string.save_total) + listInfo.get(position).getDataCount() + context.getString(R.string.save_tiao));
                    } else {
                        holder.mupdate.setVisibility(View.GONE);
                    }

                } else {
                    holder.mCname.setText(context.getString(R.string.OTHER));
                    holder.mupdate.setVisibility(View.GONE);
                }
            }


        }


        return convertView;
    }


    class SaveHolder {
        public ToggleButton mToggle;
        public TextView mBigTitle;
        public TextView mupdate;
        public TextView mHistory;
        public EqualRatioImageView mImg;
        public TextView mSaveDate;
        public DeleteRelativelayout save_line;
        public RelativeLayout time_tag;
        public TextView mCname;
    }


}
