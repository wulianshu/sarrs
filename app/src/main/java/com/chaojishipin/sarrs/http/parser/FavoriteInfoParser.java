package com.chaojishipin.sarrs.http.parser;


import com.chaojishipin.sarrs.bean.DateTag;
import com.chaojishipin.sarrs.bean.Favorite;
import com.chaojishipin.sarrs.bean.FavoriteInfos;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xll on 2015/10/10.
 */
public class FavoriteInfoParser extends ResponseBaseParser<FavoriteInfos> {


    @Override
    public FavoriteInfos parse(JSONObject data) throws Exception {
        LogUtil.e("xll","json fs "+data.toString());
        FavoriteInfos fs=new FavoriteInfos();
        if (ConstantUtils.REQUEST_SUCCESS.equals(data.optString("status"))) {
            fs.setStatus(data.optString("status"));
            if(data.has("items")){

                JSONArray root=data.getJSONArray("items");
                 List<Favorite> list=new ArrayList<Favorite>();
                 List<String> idList=new ArrayList<String>();
                 for(int i=0;i<root.length();i++){
                     JSONObject item=(JSONObject)root.get(i);
                     Favorite f=new Favorite();
                     if(item.has("id")){
                         f.setBaseId(item.getString("id"));
                         idList.add(item.getString("id"));
                     }
                     if(item.has("content_type")){
                         String ctype=item.getString("content_type");
                         if(ctype.equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_ALBUM)){
                             if(item.has("id")){
                                 f.setAid(item.getString("id"));
                             }

                         }
//                         else if(ctype.equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_SINGLE)){
//                             if(item.has("id")){
//                                 f.setGvid(item.getString("id"));
//                             }
//                         }
                         else if(ctype.equalsIgnoreCase(ConstantUtils.FavoriteConstant.TYPE_SPECIAL)){
                             if(item.has("id")){
                                 f.setTid(item.getString("id"));
                             }
                         }
                     }
                     if(item.has("gvid")){
                         f.setGvid(item.getString("gvid"));
                     }
                     if(item.has("source")){
                         f.setSource(item.getString("source"));
                     }
                     if(item.has("title")){
                       f.setTitle(item.getString("title"));
                     }
                     if(item.has("episo_num")){
                         f.setTotalepisode(item.getString("episo_num"));
                     }
                     if(item.has("create_time")){
                         f.setCreateTime(item.getString("create_time"));
                         f.setCreateDate(Utils.getVeiwTimeTag(item.getString("create_time")));
                     }
                     if(item.has("category_name")){

                     }
                    /* if(item.has("create_time")){
                         String date=  Utils.getVeiwTimeTag(item.getString("create_time"));
                         f.setCreateTime(date);
                         LogUtil.e("xll", "view Time Tag " + date);

                     }*/
                     if(item.has("category_id")){
                          f.setCid(item.getString("category_id"));
                     }
                     if(item.has("data_count")){
                         f.setDataCount(item.getString("data_count"));
                     }
                     if(item.has("image")){
                         f.setImg(item.getString("image"));
                     }
                     if(item.has("content_type")){
                         f.setType(item.getString("content_type"));
                     }
                     if(item.has("is_end")){
                          f.setIsend(item.getInt("is_end"));
                     }
                     if(item.has("episo_latest")){
                         f.setLatestepisode(item.getString("episo_latest"));
                     }
                     list.add(f);
                     fs.setFs(list);
                     fs.setBaseIdList(idList);
                 }
            }
        }
        return fs;
    }
}
