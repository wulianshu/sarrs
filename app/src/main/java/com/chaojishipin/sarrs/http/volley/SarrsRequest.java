package com.chaojishipin.sarrs.http.volley;

import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.chaojishipin.sarrs.exception.SarrsParseError;
import com.chaojishipin.sarrs.http.parser.ResponseBaseParser;
import com.chaojishipin.sarrs.http.volley.multipartrequest.MultipartRequestParams;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.letv.http.LetvHttpLog;
import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.exception.DataIsErrException;
import com.letv.http.exception.DataIsNullException;
import com.letv.http.exception.DataNoUpdateException;
import com.letv.http.exception.JsonCanNotParseException;
import com.letv.http.exception.ParseException;

import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.Map;

/**
 * Sarrs 请求类封装
 */
public class SarrsRequest<T extends LetvBaseBean> extends Request<LetvDataHull<T>> {
     private static final String TAG = "SarrsRequest";

     private ResponseBaseParser<T> mParser;
     private RequestListener<T> mRequestListener;
     private Map<String, String> mRequestParams;
     private Map<String,String> mRequestHeaders;


     /**
     * @param method 请求谓词
     * @param url    请求URL
     * @param parser 解析器
     * @param params 参数
     */
    public SarrsRequest(int method, String url, @Nullable ResponseBaseParser<T> parser
            , Map<String, String> params,Map<String,String> headers) {
        super(method, url, null);
        this.mRequestParams = params;
        this.mRequestHeaders=headers;
        this.mParser = parser;

        // 增加超时时间
        RetryPolicy policy = new DefaultRetryPolicy(ConstantUtils.HTTP_REQUEST_DEFAULT_TIMEOUT_MS
                , DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                , DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        setRetryPolicy(policy);
    }

    public SarrsRequest(int method, String url, Response.ErrorListener errorListener, @Nullable ResponseBaseParser<T> parser) {
        super(method, url, errorListener);
        this.mParser = parser;
        // 增加超时时间
        RetryPolicy policy = new DefaultRetryPolicy(ConstantUtils.HTTP_REQUEST_DEFAULT_TIMEOUT_MS
                , DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                , DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        setRetryPolicy(policy);
    }


    public void start(@Nullable RequestListener<T> requestListener) {
        this.mRequestListener = requestListener;
        LogUtil.d(ConstantUtils.HTTP_REQUEST_TAG, getUrl());
        HttpManager.getInstance().postToQueue(this);
    }

    public void start(@Nullable RequestListener<T> requestListener, String tag) {
        setTag(tag);
        start(requestListener);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mRequestParams;
    }

//    @Override
//    public Map<String, String> getHeaders() throws AuthFailureError {
//        return mRequestHeaders;
//    }

    @Override
    protected Response<LetvDataHull<T>> parseNetworkResponse(NetworkResponse response) {
        LetvDataHull<T> dataHull = new LetvDataHull<T>();
        String json="";
        try {

             json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers, HTTP.UTF_8));
            LogUtil.d(ConstantUtils.HTTP_REQUEST_TAG,"result_josn :"+json );
//            JSONObject jsonObject = new JSONObject(json);
            if (mParser != null) {
                dataHull.setDataEntity(mParser.initialParse(json));
                dataHull.setDataType(LetvDataHull.DataType.DATA_IS_INTEGRITY);
                dataHull.setSourceData(json);
            }
            return Response.success(dataHull, HttpHeaderParser.parseCacheHeaders(response));
        }

        catch (IOException e) {
            dataHull.setDataType(LetvDataHull.DataType.CONNECTION_FAIL);
            LetvHttpLog.Err("connected is fail");
            LogUtil.w(TAG, "connected is fail", e);
            return Response.error(new ServerError());
        } catch (ParseException e) {
            dataHull.setDataType(LetvDataHull.DataType.DATA_PARSE_EXCEPTION);
            LetvHttpLog.Err("parse error");
            LogUtil.w(TAG, "parse error", e);
            return Response.error(new SarrsParseError(LetvDataHull.DataType.DATA_PARSE_EXCEPTION));
        } catch (DataIsNullException e) {
            dataHull.setDataType(LetvDataHull.DataType.DATA_IS_NULL);
            LetvHttpLog.Err("data is null");
            LogUtil.w(TAG, "data is null", e);
            return Response.error(new SarrsParseError(LetvDataHull.DataType.DATA_IS_NULL));
        } catch (JsonCanNotParseException e) {
            dataHull.setDataType(LetvDataHull.DataType.DATA_CAN_NOT_PARSE);
            dataHull.setErrMsg(mParser.getErrorMsg());
            LetvHttpLog.Err("canParse is false");
            LogUtil.w(TAG, "canParse is false", e);
            return Response.error(new SarrsParseError(LetvDataHull.DataType.DATA_CAN_NOT_PARSE));
        } catch (DataIsErrException e) {
            dataHull.setDataType(LetvDataHull.DataType.DATA_IS_ERR);
            dataHull.setSourceData(json);
            LetvHttpLog.Err("data is err");
            LogUtil.w(TAG, "data is err", e);
            return Response.error(new SarrsParseError(LetvDataHull.DataType.DATA_IS_ERR));
        } catch (DataNoUpdateException e) {
            dataHull.setDataType(LetvDataHull.DataType.DATA_NO_UPDATE);
            LetvHttpLog.Err("data has not update");
            LogUtil.w(TAG, "data has not update", e);
            return Response.error(new SarrsParseError(LetvDataHull.DataType.DATA_NO_UPDATE));
        }  catch (Exception e) {
            LogUtil.w(TAG,"parseException" , e);
            dataHull.setDataType(LetvDataHull.DataType.DATA_CAN_NOT_PARSE);
            return Response.error(new SarrsParseError(RequestListener.ERROR_UNKNOWN));
        }
    }

    @Override
    protected void deliverResponse(LetvDataHull<T> response) {
        if(mRequestListener == null)
            return;
        try{
            int errorCode = response.getDataType();
            switch (errorCode) {
                case LetvDataHull.DataType.DATA_IS_INTEGRITY:
                    mRequestListener.onResponse(response.getDataEntity(), false);
                    break;
                default:
                    mRequestListener.dataErr(errorCode);
                    break;
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        try{
            super.deliverError(error);
            if (mRequestListener == null) {
                return;
            }
            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                mRequestListener.netErr(RequestListener.ERROR_NET_ERROR);
            } else if (error instanceof AuthFailureError) {
                mRequestListener.dataErr(RequestListener.ERROR_DATA_ERROR);
            } else if (error instanceof ServerError) {
                mRequestListener.netErr(RequestListener.ERROR_SERVER_ERROR);
            } else if (error instanceof NetworkError) {
                mRequestListener.netErr(RequestListener.ERROR_NET_ERROR);
            } else if (error instanceof SarrsParseError) {
                SarrsParseError httpError = (SarrsParseError) error;
                mRequestListener.dataErr(httpError.getErrorCode());
            } else {
                mRequestListener.dataErr(RequestListener.ERROR_UNKNOWN);
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    protected Map<String, String> getPostParams() throws AuthFailureError {
       return this.mRequestParams;
    }
}
