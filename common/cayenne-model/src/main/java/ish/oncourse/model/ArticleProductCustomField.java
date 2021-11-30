package ish.oncourse.model;

import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._ArticleProductCustomField;

public class ArticleProductCustomField extends _ArticleProductCustomField {

    @Override
    public void setRelatedObject(IExpandable relatedObject) {
        setRelatedObject((ArticleProduct) relatedObject);
    }
}
