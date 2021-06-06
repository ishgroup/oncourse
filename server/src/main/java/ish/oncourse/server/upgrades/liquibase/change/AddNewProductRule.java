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
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.ArrayList;
import java.util.List;

public class AddNewProductRule extends IshTaskChange {

    private final static String SCHEMA_NAME = "Product";

    @Override
    public void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService;
        DataContext context = cayenneService.getNewContext();

        List<FieldConfigurationScheme> productSchemes = ObjectSelect.query(FieldConfigurationScheme.class)
                .where(FieldConfigurationScheme.NAME.eq(SCHEMA_NAME))
                .select(context);

        if (productSchemes.size() == 0) {
            FieldConfigurationScheme scheme = context.newObject(FieldConfigurationScheme.class);

            scheme.setName(SCHEMA_NAME);

            List<FieldConfiguration> configurations = new ArrayList<>();
            configurations.add(ObjectSelect.query(EnrolmentFieldConfiguration.class).where(EnrolmentFieldConfiguration.ID.eq(-1L)).selectOne(context));
            configurations.add(getFirstFieldConfiguration(context, ApplicationFieldConfiguration.class));
            configurations.add(getFirstFieldConfiguration(context, WaitingListFieldConfiguration.class));
            configurations.add(getFirstFieldConfiguration(context, ArticleFieldConfiguration.class));
            configurations.add(getFirstFieldConfiguration(context, MembershipFieldConfiguration.class));
            configurations.add(getFirstFieldConfiguration(context, VoucherFieldConfiguration.class));

            configurations.forEach( fieldConfiguration -> {
                FieldConfigurationLink link = context.newObject(FieldConfigurationLink.class);

                link.setFieldConfiguration(fieldConfiguration);
                link.setFieldConfigurationScheme(scheme);
            });

            context.commitChanges();

            ObjectSelect.query(Product.class)
                    .select(context).forEach(product -> {
                product.setFieldConfigurationScheme(scheme);
            });

            context.commitChanges();
        }
    }

    private FieldConfiguration getFirstFieldConfiguration(ObjectContext context, Class<? extends FieldConfiguration> clzz) {
        return ObjectSelect.query(clzz).orderBy(FieldConfiguration.ID.asc()).selectFirst(context);
    }
}
