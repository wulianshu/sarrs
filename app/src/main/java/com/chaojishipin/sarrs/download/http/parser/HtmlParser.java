package com.chaojishipin.sarrs.download.http.parser;

import android.webkit.WebView;

import com.chaojishipin.sarrs.bean.HtmlDataBean;
import com.letv.http.exception.DataIsErrException;
import com.letv.http.exception.DataIsNullException;
import com.letv.http.exception.DataNoUpdateException;
import com.letv.http.exception.JsonCanNotParseException;
import com.letv.http.exception.ParseException;

import org.json.JSONObject;

public class HtmlParser extends LetvMobileParser <HtmlDataBean>{

    @Override
    public HtmlDataBean initialParse(String data) throws JsonCanNotParseException,
            DataIsNullException, ParseException, DataIsErrException, DataNoUpdateException {
        HtmlDataBean htmlDataBean = new HtmlDataBean();
        htmlDataBean.setHtmlData(data);

        return htmlDataBean;
    }
    

    @Override
    public HtmlDataBean parse(JSONObject data) throws Exception {
        return null;
    }

}
