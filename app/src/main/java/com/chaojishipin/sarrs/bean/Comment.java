/**
 *
 */
package com.chaojishipin.sarrs.bean;

import java.util.Date;

/**
 * @author JohnnyShieh
 * @ClassName: Comment
 * @Description:The basic class of Comment
 * @date Feb 14, 2014		3:14:16 PM
 */
public class Comment {

    public static final long NULL_PARENT = (long) -1;

    private long parentId;
    private long userId;
    private long id;
    private String content;
    private String userName;
    private Date date;
    /*private int floorCount ;    //楼层数目
*/    private int floorNum;      //楼层数目

    public Comment(long parent_id, long user_id, long ID, String content, String userName, Date date, int floorNum) {
        this.parentId = parent_id;
        this.userId = user_id;
        this.id = ID;
        this.content = content;
        this.userName = userName;
        this.date = date;
        this.floorNum = floorNum;
    }

    public long getParentId() {
        return parentId;
    }

    public long getUserId() {
        return userId;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getUserName() {
        return userName;
    }

    public Date getDate() {
        return date;
    }

    public int getFloorNum() {
        return floorNum;
    }
}
