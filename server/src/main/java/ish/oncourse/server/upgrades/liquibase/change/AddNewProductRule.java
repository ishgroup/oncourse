/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.FieldConfiguration;
import ish.oncourse.server.cayenne.FieldConfigurationLink;
import ish.oncourse.server.cayenne.FieldConfigurationScheme;
import ish.oncourse.server.cayenne.Product;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SQLSelect;

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

            List<FieldConfiguration> configurations = SQLSelect.query(FieldConfiguration.class,
                    "select * from FieldConfiguration fc group by type having max(id)").select(context);

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
}
