package ish.oncourse.aql.model.attribute;

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.aql.model.Entity;
import ish.oncourse.aql.model.EntityFactory;
import ish.oncourse.aql.model.SyntheticAttributeDescriptor;
import ish.oncourse.server.cayenne.Course;
import ish.oncourse.server.cayenne.EntityRelation;
import ish.oncourse.server.cayenne.Product;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.SimpleNode;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;
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
