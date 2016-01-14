package com.chaojishipin.sarrs.http.parser;

import android.util.Log;

import com.chaojishipin.sarrs.bean.CancelFavorite;
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
public class CancelFavoriteParser extends ResponseBaseParser<CancelFavorite> {

    @Override
    public CancelFavorite parse(JSONObject data) throws Exception {
        CancelFavorite c=new CancelFavorite();
        if(data.has("code")){
            c.setCode(data.getInt("code"));

        }
        return c;
    }
}
