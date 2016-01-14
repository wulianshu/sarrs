/**
 *
 */
package com.chaojishipin.sarrs.bean;

import android.content.Context;
import android.util.Log;

import com.chaojishipin.sarrs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JohnnyShieh
 * @ClassName: CommentData
 * @Description:TODO
 * @date Feb 14, 2014		3:56:27 PM
 */
public class CommentData {

    private Context context;

    public CommentData(Context cnt) {
        context = cnt;
    }

    private Comment createComment(int arrayId) {
        String[] cmts = context.getResources().getStringArray(arrayId);
        long parentId = Long.parseLong(cmts[0]);
        long userId = Long.parseLong(cmts[1]);
        long id = Long.parseLong(cmts[2]);
        Log.d("date", cmts[5]);
//        Date date = DateFormatUtils.parse(cmts[5]);
        int floorNum = Integer.parseInt(cmts[6]);
        return new Comment(parentId, userId, id, cmts[3], cmts[4], null, floorNum);
    }

    public List<Comment> getComments() {
        ArrayList<Comment> list = new ArrayList<Comment>();
        list.add(createComment(R.array.comment1));
        list.add(createComment(R.array.comment2));
        list.add(createComment(R.array.comment3));
        list.add(createComment(R.array.comment4));
        list.add(createComment(R.array.comment5));
        list.add(createComment(R.array.comment6));
        list.add(createComment(R.array.comment7));
        list.add(createComment(R.array.comment8));
        list.add(createComment(R.array.comment9));
        list.add(createComment(R.array.comment10));
//        Collections.sort(list, CommentComparator.getInstance());
        return list;
    }
}
