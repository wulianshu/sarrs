package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.bean.AddComment;
import com.chaojishipin.sarrs.bean.AddFavorite;
import com.chaojishipin.sarrs.bean.CheckFavorite;
import com.chaojishipin.sarrs.bean.Favorite;
import com.letv.http.exception.DataIsErrException;
import com.letv.http.exception.DataIsNullException;
import com.letv.http.exception.DataNoUpdateException;
import com.letv.http.exception.JsonCanNotParseException;
import com.letv.http.exception.ParseException;

import org.json.JSONObject;

/**
 * Created by xll on 2015/10/10.
 */
public class AddFavoriteParser extends ResponseBaseParser<AddFavorite> {

    @Override
    public AddFavorite parse(JSONObject data) throws Exception {
        AddFavorite c=new AddFavorite();
        if(data.has("code")){
            c.setCode(data.getInt("code"));

        }


        return c;
    }
}
