package com.chaojishipin.sarrs.manager;

import com.chaojishipin.sarrs.bean.HistoryRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wulianshu on 2015/11/26.
 */
public  class HistoryRecordManager {
//    private static HistoryRecordManager historyrecordmanager;
    private static ArrayList<HistoryRecord> historyRecordList;
//    public static  HistoryRecordManager getInstance(){
//        if(historyrecordmanager == null){
//                historyrecordmanager = new HistoryRecordManager();
//        }
//        return historyrecordmanager;
//    }

    public static ArrayList<HistoryRecord> getHisToryRecordFromServer(){
            if( historyRecordList==null){
                historyRecordList = new ArrayList<HistoryRecord>();
            }
            return historyRecordList;
    }
    public static void setHisToryRecordFromServer(ArrayList<HistoryRecord>  list){
         historyRecordList = list;
    }
    public static boolean removeallHisToryRecords(ArrayList<HistoryRecord>  removelist){
       return historyRecordList.removeAll(removelist);
    }
    public static synchronized void addHisToryRecords(HistoryRecord historyRecord){

        for(int i=0;i<historyRecordList.size();i++) {
            if (historyRecord.getId() != null) {
                if (historyRecord.getId().equals(historyRecordList.get(i).getId())) {
                    historyRecordList.remove(i);
                    historyRecordList.add(0,historyRecord);
                    break;
                }
            }else{
                if (historyRecord.getGvid().equals(historyRecordList.get(i).getGvid())) {
                    historyRecordList.remove(i);
                    historyRecordList.add(0,historyRecord);
                    break;
                }
            }
            if(i== historyRecordList.size()-1){
                historyRecordList.add(0, historyRecord);
            }
        }
    }
    public static void clear(){
        historyRecordList.clear();
    }
}
