package com.chaojishipin.sarrs.thirdparty;


import com.letv.http.bean.LetvBaseBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeiboInfo implements LetvBaseBean {

    /**
     *
     */
    private static final long serialVersionUID = -1072257181413444174L;
    private WeiboUser user;
    private String imageUrl;
    private String title;
    private String vedioUrl = "";
    /**
     * 毫秒
     */
    private long create_at = 0;

    public String getVedioUrl() {
        return vedioUrl;
    }

    public void setVedioUrl(String vedioUrl) {
        this.vedioUrl = vedioUrl;
    }

    public WeiboUser getUser() {
        return user;
    }

    public void setUser(WeiboUser user) {
        this.user = user;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        String regex = "http://([\\w-]+\\.)+[\\w-]+(/[\\w-\\./?%&=]*)?";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(title);
        if (m.find()) {
            setVedioUrl(m.group());
        }
    }

    public long getCreate_at() {
        return create_at;
    }

    public void setCreate_at(long create_at) {
        this.create_at = create_at;
    }


}
