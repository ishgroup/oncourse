package ish.oncourse.services.tag;

import ish.oncourse.model.Tag;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class GetTagLeafs {

    private Tag tag;

    private List<Tag> res = new ArrayList<>();

    private GetTagLeafs() {}

    public static GetTagLeafs valueOf(Tag tag) {
        GetTagLeafs obj = new GetTagLeafs();
        obj.tag = tag;
        return obj;
    }

    public List<Tag> get() {
        tag.getWebVisibleTags().forEach(t -> fetchChildrens(t));
        return res;
    }

    private void fetchChildrens(Tag t) {
        if (t.getWebVisibleTags() == null || t.getWebVisibleTags().isEmpty()) {
            res.add(t);
        } else {
            t.getWebVisibleTags().forEach(c -> fetchChildrens(c));
        }
    }

}
