package com.chaojishipin.sarrs.feedback;

import android.util.Log;

import com.letv.http.bean.LetvDataHull;

/**
 * Created by wangyemin on 2015/10/10.
 */
public class DataReporter {

    public static DataReportThread reportThread;

    static {
        reportThread = new DataReportThread();
        reportThread.setName("stats-thread");
        reportThread.setPriority(Thread.NORM_PRIORITY - 1);
        reportThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
            }
        });
        reportThread.start();
    }

    /**
     * 负反馈
     *
     * @param id
     * @param source
     * @param cid
     * @param type
     * @param token
     * @param netType
     */
    public static void reportDislike(final String id,
                                     final String source,
                                     final String cid,
                                     final String type,
                                     final String token,
                                     final String netType,
                                     final String bucket,
                                     final String seid) {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                LetvDataHull obj = DataHttpApi.requestDislikeReport(new DataReportParser(), id, source, cid, type, token, netType, bucket, seid);

                Log.d(DataHttpApi.TAG, "dataType is " + obj.getDataType());
                DataReportListener listener = new DataReportListener() {
                    @Override
                    public void reportSucess() {
                        Log.d(DataHttpApi.TAG, "reportDislikeSucess");
                    }

                    @Override
                    public void reportFail() {
                        Log.d(DataHttpApi.TAG, "reportDislikeFail");
                    }
                };
                if (obj.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
                    listener.reportSucess();
                } else
                    listener.reportFail();
            }
        });
    }

    /**
     * 用户兴趣
     *
     * @param id
     * @param cid
     * @param type
     * @param token
     * @param netType
     */
    public static void reportInterest(final String id,
                                      final String cid,
                                      final String type,
                                      final String token,
                                      final String netType,
                                      final String bucket,
                                      final String seid) {
        LetvDataHull obj = DataHttpApi.requestInterestReport(new DataReportParser(), id, cid, type, token, netType, bucket, seid);
        Log.d(DataHttpApi.TAG, "dataType is " + obj.getDataType());
        DataReportListener listener = new DataReportListener() {
            @Override
            public void reportSucess() {
                Log.d(DataHttpApi.TAG, "reportInterestSucess");
            }

            @Override
            public void reportFail() {
                Log.d(DataHttpApi.TAG, "reportInterestFail");
            }
        };
        if (obj.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
            listener.reportSucess();
        } else
            listener.reportFail();
    }

    /**
     * 播放记录
     *
     * @param id
     * @param aid
     * @param source
     * @param cid
     * @param playTime
     * @param token
     * @param netType
     * @param bucket
     * @param seid
     */
    public static void reportPlayRecord(final String id,
                                        final String aid,
                                        final String source,
                                        final String cid,
                                        final int playTime,
                                        final String token,
                                        final String netType,
                                        final String bucket,
                                        final String seid) {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                LetvDataHull obj = DataHttpApi.requestPlayRecordReport(new DataReportParser(), id, aid, source, cid, playTime, token, netType, bucket, seid);

                Log.d(DataHttpApi.TAG, "dataType is " + obj.getDataType());
                DataReportListener listener = new DataReportListener() {
                    @Override
                    public void reportSucess() {
                        Log.d(DataHttpApi.TAG, "reportPlayRecordSucess");
                    }

                    @Override
                    public void reportFail() {
                        Log.d(DataHttpApi.TAG, "reportPlayRecordFail");
                    }
                };
                if (obj.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
                    listener.reportSucess();
                } else
                    listener.reportFail();
            }
        });
    }

    /**
     * 收藏
     *
     * @param id
     * @param source
     * @param cid
     * @param type
     * @param token
     * @param netType
     */
    public static void reportAddCollection(final String id,
                                           final String source,
                                           final String cid,
                                           final String type,
                                           final String token,
                                           final String netType) {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                LetvDataHull obj = DataHttpApi.requestAddCollectionReport(new DataReportParser(), id, source, cid, type, token, netType);

                Log.d(DataHttpApi.TAG, "dataType is " + obj.getDataType());
                DataReportListener listener = new DataReportListener() {
                    @Override
                    public void reportSucess() {
                        Log.d(DataHttpApi.TAG, "reportAddCollectionSucess");
                    }

                    @Override
                    public void reportFail() {
                        Log.d(DataHttpApi.TAG, "reportAddCollectionFail");
                    }
                };
                if (obj.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
                    listener.reportSucess();
                } else
                    listener.reportFail();
            }
        });
    }

    /**
     * 分享
     *
     * @param id
     * @param source 分享专辑、单视频时传，分享专题、排行榜不传
     * @param cid
     * @param type
     * @param token
     * @param netType
     * @param bucket
     * @param seid
     */
    public static void reportAddShare(final String id,
                                      final String source,
                                      final String cid,
                                      final String type,
                                      final String token,
                                      final String netType,
                                      final String bucket,
                                      final String seid) {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                LetvDataHull obj = DataHttpApi.requestAddShare(new DataReportParser(), id, source, cid, type, token, netType, bucket, seid);

                Log.d(DataHttpApi.TAG, "dataType is " + obj.getDataType());
                DataReportListener listener = new DataReportListener() {
                    @Override
                    public void reportSucess() {
                        Log.d(DataHttpApi.TAG, "reportAddShareSucess");
                    }

                    @Override
                    public void reportFail() {
                        Log.d(DataHttpApi.TAG, "reportAddShareFail");
                    }
                };
                if (obj.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
                    listener.reportSucess();
                } else
                    listener.reportFail();
            }
        });
    }

    public static void doInBackground(Runnable r) {
        reportThread.post(r);
    }
}
