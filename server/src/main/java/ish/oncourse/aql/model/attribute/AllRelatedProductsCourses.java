package ish.oncourse.aql.model.attribute;

import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.Course;
import ish.oncourse.server.cayenne.Product;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.Optional;

public class AllRelatedProductsCourses implements SyntheticAttributeDescriptor {

    private EntityFactory entityFactory;

    public AllRelatedProductsCourses(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public Class<? extends Persistent> getEntityType() {
        return Product.class;
    }

    @Override
    public String getAttributeName() {
        return "allRelatedCourses";
    }

    @Override
    public Optional<Entity> nextEntity() {
        return Optional.of(entityFactory.createEntity(Course.class));
    }

    @Override
    public SimpleNode spawnNode() {
        return new AllRelatedEntitiesLazyNode(Course.class, Product.class, Course.ID, Product.ID);
    }
}
