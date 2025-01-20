/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.model;

import ish.common.types.DataType;
import ish.oncourse.aql.model.attribute.*;
import ish.oncourse.aql.model.attribute.tagging.*;
import ish.oncourse.aql.model.attribute.tagging.relations.*;
import ish.oncourse.server.api.v1.function.TagRequirementFunctions;
import ish.oncourse.server.cayenne.CustomFieldType;
import ish.oncourse.server.cayenne.glue.TaggableCayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.ObjectSelect;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Factory that produces {@link Entity} objects.
 *
 */
public class EntityFactory {

    /**
     * List of types that supports custom fields.
     */
    private static final List<String> ENTITIES_WITH_CUSTOM_FIELDS =
            Arrays.asList("Application", "Contact", "Course","CourseClass", "Enrolment", "WaitingList", "Survey", "Article", "Membership", "Voucher", "ProductItem", "ArticleProduct", "VoucherProduct", "MembershipProduct");

    /**
     * List of synthetic attributes.
     * Must be in sync with the data for client here:
     *    /buildSrc/apidoc/src/main/resources/au/com/ish/queryLanguageModel/syntheticAttributes.xml
     */
    private static final List<Class<?  extends SyntheticAttributeDescriptor>> SYNTHETIC_ATTRIBUTES = Arrays.asList(
            AccountTransactionBanking.class,
            AccountTransactionPaymentIn.class,
            AccountTransactionPaymentOut.class,
            AccountTransactionInvoice.class,
            AccountTransactionContact.class,
            ContactAccountTransactions.class,
            CourseClassEnrolmentCount.class,
            CourseClassEnrolmentMax.class,
            CourseClassEnrolmentMin.class,
            CourseClassSessionsCount.class,
            CourseClassIsDistantLearningCourse.class,
            EnrolmentIsClassCompleted.class,
            SessionTutor.class,
            PaymentInBanking.class,
            PaymentInReversalOfId.class,
            PaymentInReversedById.class,
            PaymentInAccountTransactions.class,
            VoucherInvoice.class,
            VoucherPaymentIn.class,
            VoucherEnrolment.class,
            PaymentOutAccountTransactions.class,
            InvoiceAccountTransactions.class,
            ProductItemPurchasedBy.class,
            ProductItemRedeemableBy.class,
            CourseClassesInvoice.class,
            AllRelatedCoursesProducts.class,
            AllRelatedCoursesCourses.class,
            AllRelatedProductsCourses.class,
            AllRelatedProductsProducts.class,
            AllRelatedContactsContacts.class,
            FromRelatedContactsContacts.class,
            ToRelatedContactsContacts.class,
            FromRelationTypeContact.class,
            ToRelationTypeContact.class,
            StudentCourseClassContact.class,
            TutorCourseClassContact.class,
            StudentEnrolmentsContact.class,
            BankingInvoice.class,
            ContactOutcomes.class,
            OutcomeStudent.class,
            VetOutcomes.class,
            ProductProductType.class,
            ProductItemProductType.class,
            FundingSourceName.class,
            StudentsTutorsCourses.class,
            StudentsTutorsAssessments.class,
            TaggingRelationsAbstractInvoice.class,
            TaggingRelationsApplication.class,
            TaggingRelationsArticleProduct.class,
            TaggingRelationsAssessment.class,
            TaggingRelationsContact.class,
            TaggingRelationsCourse.class,
            TaggingRelationsCourseClass.class,
            TaggingRelationsDocument.class,
            TaggingRelationsEnrolment.class,
            TaggingRelationsFaculty.class,
            TaggingRelationsLead.class,
            TaggingRelationsMembershipProduct.class,
            TaggingRelationsPayslip.class,
            TaggingRelationsProductItem.class,
            TaggingRelationsReport.class,
            TaggingRelationsRoom.class,
            TaggingRelationsSite.class,
            TaggingRelationsVoucherProduct.class,
            EnrolmentAbstractInvoiceLines.class,
            CourseClassAbstractInvoiceLines.class,
            TaxAbstractInvoiceLines.class,
            AccountAbstractInvoiceLines.class,
            TaggingRelationsWaitingList.class,
            TagsAttribute.class,
            CheckedTasksAttribute.class,
            UncheckedTasksAttribute.class,
            CompletedChecklistsAttribute.class,
            UncompletedChecklistsAttribute.class
    );

    private final ObjectContext context;

    private final Map<String, Collection<SyntheticAttributeDescriptor>> syntheticAttributes;

    public EntityFactory(ObjectContext context) {
        this.context = Objects.requireNonNull(context);
        this.syntheticAttributes = new ConcurrentHashMap<>(SYNTHETIC_ATTRIBUTES.stream()
                .map(clazz -> {
                    try {
                        var constructor = clazz.getConstructor(EntityFactory.class);
                        return constructor.newInstance(EntityFactory.this);
                    } catch (Exception ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(d -> d.getEntityType().getName())));

        PathAliasFactory pathAliasFactory = new PathAliasFactory(this);
        pathAliasFactory.init();
    }

    void addSyntheticAttribute(Class<? extends Persistent> entityType, SyntheticAttributeDescriptor descriptor) {
        syntheticAttributes
                .computeIfAbsent(entityType.getName(), type -> new ArrayList<>())
                .add(descriptor);
    }

    public Entity createEntity(Class<?> queryRoot) {
        var root = context.getEntityResolver().getObjEntity(queryRoot);
        return createEntity(root);
    }

    Entity createEntity(ObjEntity entity) {
        var entityName = entity.getName();
        var syntheticAttributesForEntity
                = syntheticAttributes.getOrDefault(entity.getJavaClassName(), new ArrayList<>());
        if(entityIsTaggable(entityName)){
            var taggableAttributes = syntheticAttributes.getOrDefault(TaggableCayenneDataObject.class.getName(), new ArrayList<>());
            syntheticAttributesForEntity.addAll(taggableAttributes);
        }
        if(ENTITIES_WITH_CUSTOM_FIELDS.contains(entityName)) {
            return new Entity(this, entity, customFieldLookup(entityName), syntheticAttributesForEntity);
        }
        return new Entity(this, entity, Collections.emptyMap(), syntheticAttributesForEntity);
    }

    private Map<String, Class<?>> customFieldLookup(String entityName) {
        List<String> entityNames = entityName.equals("ProductItem")
                ? List.of("Article","Voucher","Membership")
                : List.of(entityName);
        var customFieldsNames = ObjectSelect
                .columnQuery(CustomFieldType.class, CustomFieldType.KEY, CustomFieldType.DATA_TYPE)
                .where(CustomFieldType.ENTITY_IDENTIFIER.in(entityNames))
                .select(context);

        Map<String, Class<?>> customFields = new HashMap<>();
        customFieldsNames.forEach(field -> {
            Class<? extends CustomFieldMarker> marker;
            if(field[1] == DataType.DATE)
                marker = CustomFieldDateMarker.class;
            else if(field[1] == DataType.DATE_TIME)
                marker = CustomFieldDateTimeMarker.class;
            else
                marker = CustomFieldMarker.class;
            customFields.put((String) field[0], marker);
        });
        return customFields;
    }

    private boolean entityIsTaggable(String entityName){
        return TagRequirementFunctions.taggableClassesBidiMap.containsKey(entityName);
    }

}

