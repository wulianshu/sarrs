package com.chaojishipin.sarrs.http.parser;

import org.json.JSONObject;

import com.chaojishipin.sarrs.bean.HtmlDataBean;
import com.letv.http.exception.DataIsErrException;
import com.letv.http.exception.DataIsNullException;
import com.letv.http.exception.DataNoUpdateException;
import com.letv.http.exception.JsonCanNotParseException;
import com.letv.http.exception.ParseException;

public class HtmlParser extends ResponseBaseParser <HtmlDataBean>{

   @Override
    public HtmlDataBean initialParse(String data) throws JsonCanNotParseException,
           DataIsNullException, ParseException, DataIsErrException, DataNoUpdateException {
          HtmlDataBean htmlDataBean = new HtmlDataBean();
          htmlDataBean.setHtmlData(data);
         return  htmlDataBean;
    }


    @Override
    public HtmlDataBean parse(JSONObject data) throws Exception {
        HtmlDataBean htmlDataBean = new HtmlDataBean();
        if(data.has("code")){
            htmlDataBean.setCode(data.getString("code"));
        }

      return htmlDataBean;
    }

}
