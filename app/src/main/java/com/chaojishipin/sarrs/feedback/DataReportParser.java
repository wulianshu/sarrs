package com.chaojishipin.sarrs.feedback;


import com.chaojishipin.sarrs.download.http.parser.LetvMobileParser;
import com.letv.http.exception.DataIsErrException;
import com.letv.http.exception.DataIsNullException;
import com.letv.http.exception.DataNoUpdateException;
import com.letv.http.exception.JsonCanNotParseException;
import com.letv.http.exception.ParseException;

import org.json.JSONObject;


/**
 * Created by wangyemin on 2015/10/10.
 */
public class DataReportParser extends LetvMobileParser<DataReport> {
    @Override
    public DataReport parse(JSONObject data) throws Exception {
        return null;
    }

    @Override
    public DataReport initialParse(String data) throws JsonCanNotParseException, DataIsNullException, ParseException, DataIsErrException, DataNoUpdateException {
        DataReport dataReport = new DataReport();
        dataReport.setResult(data);
        return dataReport;
    }
}
