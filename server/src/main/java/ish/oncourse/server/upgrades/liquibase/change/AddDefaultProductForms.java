/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.*;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDefaultProductForms extends IshTaskChange {

    @Override
    public void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService;
        DataContext context = cayenneService.getNewContext();

        FieldConfiguration enrolmentForm = ObjectSelect.query(FieldConfiguration.class)
                .where(FieldConfiguration.ID.eq(-1L))
                .prefetch(FieldConfiguration.FIELD_HEADINGS.joint())
                .prefetch(FieldConfiguration.FIELD_HEADINGS.dot(FieldHeading.FIELDS).joint())
                .selectOne(context);

        Map<Integer, Class<? extends FieldConfiguration>> formConfigurations = new HashMap<>() {{
            put(7, ArticleFieldConfiguration.class);
            put(8, MembershipFieldConfiguration.class);
            put(9, VoucherFieldConfiguration.class);
        }};

        formConfigurations.forEach((type, formConfiguration) -> {

            List<FieldConfiguration> currentForms = ObjectSelect.query(FieldConfiguration.class)
                    .where(FieldConfiguration.INT_TYPE.eq(type)).select(context);

            if (currentForms.size() == 0) {
                FieldConfiguration newForm = context.newObject(formConfiguration);

                newForm.setName(String.format("Default Field form (%s)", newForm.getType().getDisplayName()));

                enrolmentForm.getFieldHeadings().forEach(defaultFieldHeading -> {
                    FieldHeading newFieldHeading = context.newObject(FieldHeading.class);

                    newFieldHeading.setName(defaultFieldHeading.getName());
                    newFieldHeading.setDescription(defaultFieldHeading.getDescription());
                    newFieldHeading.setFieldOrder(defaultFieldHeading.getFieldOrder());
                    newFieldHeading.setFieldConfiguration(newForm);

                    defaultFieldHeading.getFields().forEach(defaultField -> {
                        Field newField = context.newObject(Field.class);

                        newField.setName(defaultField.getName());
                        newField.setDescription(defaultField.getDescription());
                        newField.setDefaultValue(defaultField.getDefaultValue());
                        newField.setMandatory(defaultField.getMandatory());
                        newField.setProperty(defaultField.getProperty());
                        newField.setOrder(defaultField.getOrder());
                        newField.setFieldHeading(newFieldHeading);
                        newField.setFieldConfiguration(newForm);
                    });
                });

                context.commitChanges();

                ObjectSelect.query(FieldConfigurationScheme.class)
                        .select(context).forEach(schema -> {
                    FieldConfigurationLink schemaProductLink = context.newObject(FieldConfigurationLink.class);

                    schemaProductLink.setFieldConfiguration(newForm);
                    schemaProductLink.setFieldConfigurationScheme(schema);
                });

                context.commitChanges();
            }
        });
    }
}
