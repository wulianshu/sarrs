package com.chaojishipin.sarrs.adapter;

import com.chaojishipin.sarrs.bean.Favorite;
import com.chaojishipin.sarrs.thirdparty.Constant;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.JsonUtil;
import com.chaojishipin.sarrs.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xulinlin on 2015/10/11.
 */
public class BatchFavoriteAdapter  {

    static BatchFavoriteAdapter instanse;


    public static final BatchFavoriteAdapter getInstanse(){
         if(instanse==null){
             instanse=new BatchFavoriteAdapter();
         }
        return instanse;

    }

    public  String wrapItems(List<Favorite> list){



          List<BatchItem> items=new ArrayList<BatchItem>();
           for(int i=0;i<list.size();i++){
               Favorite f=list.get(i);
               if(f.isCheck()){
                   BatchItem item=new BatchItem();
                   item.type=f.getType();
                   item.action=1;
                   if(f.getType().equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_ALBUM)){
                       item.relatedId=f.getAid();
                   }else if(f.getType().equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_SPECIAL)) {
                       item.relatedId=f.getTid();
                   }else if(f.getType().equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_SINGLE)){
                       item.relatedId=f.getGvid();
                   }
                   item.time=f.getCreateTime();
                   items.add(item);
               }
           }
//        String json="";
//        String start="[";
//        String end="]";
//        String elementTag=",";
//        String content="";
//        for(int i=0;i<items.size();i++){
//
//                content=content.concat(JsonUtil.toJSONString(items.get(i)));
//                if(i<=items.size()-2){
//                    content=  content.concat(elementTag);
//                }
//
//        }
//        json=start.concat(content).concat(end);
        String json = JsonUtil.toJSONString(items);
        LogUtil.e("wulianshu","收藏上报："+json);
        return json;

    }

    public  String wrapItemsByClick(List<Favorite> list,int position){
            Favorite f=list.get(position);
                BatchItem item=new BatchItem();
                item.type=f.getType();
                item.action=1;
                if(f.getType().equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_ALBUM)){
                    item.relatedId=f.getAid();
                }else if(f.getType().equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_SPECIAL)) {
                    item.relatedId=f.getTid();
                }else if(f.getType().equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_SINGLE)){
                    item.relatedId=f.getGvid();
                 }
                item.time=f.getCreateTime();
              List<BatchItem> jsonlist = new ArrayList<BatchItem>();
              jsonlist.add(item);
              String json = JsonUtil.toJSONString(jsonlist);
//        String json="";
//        String start="[";
//        String end="]";
//        String elementTag=",";
//        String content="";
//            content=JsonUtil.toJSONString(item);
//            json=start.concat(content).concat(end);
//        LogUtil.e("wrap ","json "+json);
        LogUtil.e("wulianshu","收藏数据单个上报："+json);
        return json;

    }
    class BatchItem{

        public  String type;
        public  String relatedId;
        public  String time;
        public  int action;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRelatedId() {
            return relatedId;
        }

        public void setRelatedId(String relatedId) {
            this.relatedId = relatedId;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }
    }


}
