package com.chaojishipin.sarrs.bean;

import java.util.ArrayList;

import android.text.TextUtils;

import com.letv.http.bean.LetvBaseBean;

/**
 * 仅作为demo，需要删除
 */
public class LiveInfos implements LetvBaseBean{

    /**
     * 仅作为demo，需要删除
     */
    private static final long serialVersionUID = 1L;
    
    private ArrayList<LiveClassifyInfos> allInfos;

    public ArrayList<LiveClassifyInfos> getAllInfos() {
        return allInfos;
    }

    public void setAllInfos(ArrayList<LiveClassifyInfos> allInfos) {
        this.allInfos = allInfos;
    }

    /**
     * 仅作为demo，需要删除
     */
    public ArrayList<String> getTabTitle() {
        ArrayList<String> tabTile = null;
        if (null != allInfos) {
            int infoSize = allInfos.size();
            tabTile = new ArrayList<String>(infoSize);
            for (int i = 0; i < infoSize; i++) {
                LiveClassifyInfos classifyInfo = allInfos.get(i);
                tabTile.add(classifyInfo.getCheineseName());
            }
        }
        return tabTile;
    }
    
    public String findIdByName(String chineseName) {
        String id = null;
        if (null != allInfos && !TextUtils.isEmpty(chineseName)) {
            int infoSize = allInfos.size();
            for (int i = 0; i < infoSize; i++) {
                LiveClassifyInfos classifyInfo = allInfos.get(i);
                if (chineseName.equals(classifyInfo.getCheineseName())) {
                    id = classifyInfo.getIdentifier();
                }
            }
        }
        return id;
    }

}
