/**
 *
 */
package com.chaojishipin.sarrs.widget;


import com.chaojishipin.sarrs.bean.Comment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author JohnnyShieh
 * @ClassName: SubComments
 * @Description:TODO
 * @date Feb 20, 2014		10:03:22 AM
 */
public class SubComments {

    private List<Comment> list;

    public SubComments(List<Comment> cmts) {
        if (cmts != null) {
            list = new ArrayList<Comment>(cmts);
        } else {
            list = null;
        }
    }

    public int size() {
        return list == null ? 0 : list.size();
    }

    public int getFloorNum() {
        return list.get(list.size() - 1).getFloorNum();
    }

    public Comment get(int index) {
        return list.get(index);
    }

    public Iterator<Comment> iterator() {
        return list == null ? null : list.iterator();
    }
}
