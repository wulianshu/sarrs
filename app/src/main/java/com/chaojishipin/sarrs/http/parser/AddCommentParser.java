package com.chaojishipin.sarrs.http.parser;

import com.chaojishipin.sarrs.bean.AddComment;

import org.json.JSONObject;

/**
 * 评论解析
 * Created by wangyemin on 2015/10/8.
 */
public class AddCommentParser extends ResponseBaseParser<AddComment> {
    @Override
    public AddComment parse(JSONObject data) throws Exception {
        AddComment addComment = new AddComment();
        if (data.has("code") && data.optString("code").equalsIgnoreCase("0")) {
            addComment.setState(1);
        }
        return addComment;
    }
}
