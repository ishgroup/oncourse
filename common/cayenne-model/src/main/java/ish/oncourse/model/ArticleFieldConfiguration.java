package ish.oncourse.model;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.auto._ArticleFieldConfiguration;

public class ArticleFieldConfiguration extends _ArticleFieldConfiguration {

    @Override
    public FieldConfigurationType getType() {
        return FieldConfigurationType.PRODUCT;
    }
}
