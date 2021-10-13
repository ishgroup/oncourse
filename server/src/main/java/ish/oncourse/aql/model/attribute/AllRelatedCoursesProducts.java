package ish.oncourse.aql.model.attribute;

import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.Course;
import ish.oncourse.server.cayenne.Product;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.Optional;

public class AllRelatedCoursesProducts implements SyntheticAttributeDescriptor {

    private final EntityFactory factory;

    public AllRelatedCoursesProducts(EntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return Course.class;
    }

    @Override
    public String getAttributeName() {
        return "allRelatedProducts";
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(factory.createEntity(Product.class));
    }

    @Override
    public SimpleNode spawnNode() {
        return new AllRelatedEntitiesLazyNode(Product.class, Course.class, Product.ID, Course.ID);
    }
}
