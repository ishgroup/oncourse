/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.model.attribute.tagging.relations;

import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.server.cayenne.ArticleProduct;
import ish.oncourse.server.cayenne.ArticleProductTagRelation;
import org.apache.cayenne.Persistent;

import java.util.Optional;

public class TaggingRelationsArticleProduct extends AbstractTaggingRelations {

    public TaggingRelationsArticleProduct(EntityFactory factory) {
        super(factory);
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return ArticleProduct.class;
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(ArticleProductTagRelation.class));
    }
}
