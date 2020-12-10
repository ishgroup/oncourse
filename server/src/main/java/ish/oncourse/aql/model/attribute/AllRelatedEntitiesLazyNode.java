package ish.oncourse.aql.model.attribute;

import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.ExpressionUtil;
import ish.oncourse.aql.impl.LazyExpressionNode;
import ish.oncourse.server.cayenne.Course;
import ish.oncourse.server.cayenne.EntityRelation;
import ish.oncourse.server.cayenne.Product;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.ASTPath;
import org.apache.cayenne.exp.parser.SimpleNode;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

public class AllRelatedEntitiesLazyNode extends LazyExpressionNode {

    Class<? extends Persistent> fromEntity;
    Class<? extends Persistent> toEntity;
    Property<Long> fromProperty;
    Property<Long> toProperty;

    public AllRelatedEntitiesLazyNode(Class<? extends Persistent> fromEntity, Class<? extends Persistent> toEntity, Property<Long> fromProperty, Property<Long> toProperty) {
        this.fromEntity = fromEntity;
        this.toEntity = toEntity;
        this.fromProperty = fromProperty;
        this.toProperty = toProperty;
    }

    @Override
    public SimpleNode resolveParent(SimpleNode parent, List<SimpleNode> args, CompilationContext ctx) {
        if (ctx.hasErrors()) {
            return null;
        }
        if (args.get(0) != this || !(args.get(1) instanceof ASTPath)) {
            ctx.reportError(-1, -1, "Invalid arguments in allRelatedProducts property resolution.");
        }

        var basePath = ((ASTPath) args.get(1)).getPath();

        var idx = 0;
        var tempParent = parent;

        args.set(1, (SimpleNode) ExpressionFactory.pathExp(getSearchProperty(basePath)));

        for (var child : args.subList(1, args.size())) {
            if (child instanceof LazyExpressionNode) {
                tempParent = ((LazyExpressionNode) child).resolveParent(tempParent, args.subList(1, args.size()), ctx);
            } else {
                ExpressionUtil.addChild(tempParent, child, idx++);
            }
        }

        List<Long> relatedEntityIds = ObjectSelect.columnQuery(fromEntity, fromProperty).where(tempParent).select(ctx.getContext());
        List<Long> toIds = ObjectSelect
                .columnQuery(EntityRelation.class, EntityRelation.TO_ENTITY_ANGEL_ID)
                .where(EntityRelation.FROM_ENTITY_ANGEL_ID.in(relatedEntityIds))
                .and(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(fromEntity.getSimpleName()))
                .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(toEntity.getSimpleName()))
                .select(ctx.getContext());

        List<Long> fromIds = ObjectSelect
                .columnQuery(EntityRelation.class, EntityRelation.FROM_ENTITY_ANGEL_ID)
                .where(EntityRelation.TO_ENTITY_ANGEL_ID.in(relatedEntityIds))
                .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(fromEntity.getSimpleName()))
                .and(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(toEntity.getSimpleName()))
                .select(ctx.getContext());


        Expression searchEntities = toProperty.in(fromIds).orExp(toProperty.in(toIds));

        return (SimpleNode) searchEntities;
    }

    @Override
    public SimpleNode resolveSelf(CompilationContext ctx) {
        return this;
    }

    private String getSearchProperty(String path) {
        if (path.isEmpty()) {
            return "id";
        }

        var pathComponents = path.split("\\.");
        return pathComponents[pathComponents.length-1];
    }
}
