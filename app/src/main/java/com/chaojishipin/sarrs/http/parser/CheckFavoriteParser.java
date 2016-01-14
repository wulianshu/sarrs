package com.chaojishipin.sarrs.http.parser;


import com.chaojishipin.sarrs.bean.CheckFavorite;

import org.json.JSONObject;

/**
 * Created by xll on 2015/10/10.
 */
public class CheckFavoriteParser extends ResponseBaseParser<CheckFavorite> {

    @Override
    public CheckFavorite parse(JSONObject data) throws Exception {
        CheckFavorite c=new CheckFavorite();
        if(data.has("code")){
            c.setCode(data.getInt("code"));

        }
        if(data.has("data")){
            JSONObject datamain=data.getJSONObject("data");
            if(datamain.has("existing")){
                if(datamain.getString("existing").equalsIgnoreCase("0")){
                    c.setIsExists(true);
                }else{
                    c.setIsExists(false);
                }

            }



        }

        return c;
    }
}
