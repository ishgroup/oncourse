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

import ish.oncourse.aql.model.attribute.*;
import ish.oncourse.server.cayenne.CustomFieldType;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.ObjectSelect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
            Arrays.asList("Application", "Contact", "Course","CourseClass", "Enrolment", "WaitingList", "Survey", "Article", "Membership", "Voucher");

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
            StudentsTutorsAssessments.class
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
                = syntheticAttributes.getOrDefault(entity.getJavaClassName(), Collections.emptyList());
        if(ENTITIES_WITH_CUSTOM_FIELDS.contains(entityName)) {
            return new Entity(this, entity, customFieldLookup(entityName), syntheticAttributesForEntity);
        }
        return new Entity(this, entity, Collections.emptyMap(), syntheticAttributesForEntity);
    }

    private Map<String, Class<?>> customFieldLookup(String entityName) {
        var customFieldsNames = ObjectSelect
                .columnQuery(CustomFieldType.class, CustomFieldType.KEY)
                .where(CustomFieldType.ENTITY_IDENTIFIER.eq(entityName))
                .select(context);

        Map<String, Class<?>> customFields = new HashMap<>();
        customFieldsNames.forEach(field -> customFields.put(field, CustomFieldMarker.class));
        return customFields;
    }

}

