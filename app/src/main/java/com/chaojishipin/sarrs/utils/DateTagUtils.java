package com.chaojishipin.sarrs.utils;

import com.chaojishipin.sarrs.bean.DateTag;
import com.chaojishipin.sarrs.bean.Favorite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xll on 2015/10/15.
 */
public class DateTagUtils {


    // list-->tags-->addTag

    public static List<Favorite> addTags(List<Favorite> initList,List<String> allDateTags,List<DateTag>distincttags){
       for(int i=0;i< distincttags.size();i++){
           int index=allDateTags.indexOf(distincttags.get(i).dateTag);
          if(index>=0){
              if(initList.size()>index&&index>=0){
                  initList.get(index).setIsShowTag(true);
                  LogUtil.e("xll", "tags index " + index);
              }

          }

       }
     return initList;

    }





}
