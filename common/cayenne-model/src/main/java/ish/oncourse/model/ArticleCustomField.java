package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._ArticleCustomField;

public class ArticleCustomField extends _ArticleCustomField {

    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((Article) relatedObject);
    }
}
