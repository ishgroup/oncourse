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

package ish.oncourse.aql.impl.converter;

import ish.oncourse.aql.impl.Op;

import ish.oncourse.server.cayenne.Contact;
import ish.util.StringUtil;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;
import org.apache.commons.lang3.StringUtils;

public class LazyContactAttributeComparisonNode extends LazyEntityComparisonNode {

    private Property<Contact> property;

    LazyContactAttributeComparisonNode(Op op, Property<Contact> property) {
        super(op);
        this.property = property;
    }

    @Override
    protected SimpleNode createNode() {
        var searchString = ((ASTScalar)this.jjtGetChild(1)).getValue().toString();
        return (SimpleNode) ContactSearchExpressionCreator.valueOf(searchString, property).createContactSearchExpression();
    }

    public static class ContactSearchExpressionCreator {

        private String searchString;
        private Property prefix;
        private String[] names;

        public ContactSearchExpressionCreator() {
        }

        public static String[] splitName(String entryValue) {
            String firstname, lastname;

            var commaPosition = entryValue.indexOf(StringUtil.COMMA_CHARACTER);
            if (commaPosition > 0) {
                // consider "lastname, firstname"
                firstname = entryValue.substring(commaPosition).replace(StringUtil.COMMA_CHARACTER, StringUtils.EMPTY).trim();
                lastname = entryValue.substring(0, commaPosition).replace(StringUtil.COMMA_CHARACTER, StringUtils.EMPTY).trim();
                return new String[] { firstname, lastname };

            }

            var spacePosition = entryValue.lastIndexOf(" ");
            if (spacePosition > 0) {
                // consider the string "firstname lastname"
                firstname = entryValue.substring(0, spacePosition).trim();
                lastname = entryValue.substring(spacePosition).trim();
                return new String[] { firstname, lastname };
            }

            // no firstname
            return new String[] {StringUtils.EMPTY, entryValue.trim() };
        }

        public static ContactSearchExpressionCreator valueOf(String searchString, Property prefix) {
            var contactSearchExpressionCreator = new ContactSearchExpressionCreator();
            contactSearchExpressionCreator.searchString = searchString;
            contactSearchExpressionCreator.prefix = prefix;

            return contactSearchExpressionCreator;
        }

        public Expression createContactSearchExpression() {

            if (StringUtils.trimToNull(searchString) == null) {
                return null;
            }

            names = splitName(searchString);

            var companyExp = createCompanyExpression();

            var nameExp = getProperty(Contact.IS_COMPANY).eq(false);
            if (searchString.contains(StringUtil.COMMA_CHARACTER)) {
                nameExp = nameExp
                        .andExp(expForCommaSeparatedFullName());
            } else if (searchString.contains(" ")) {
                nameExp = nameExp
                        .andExp(expForSpaceSeparatedFullName());
            } else {
                nameExp = nameExp.andExp(expForOnlyLastName());
            }
            return companyExp.orExp(nameExp);
        }

        private Property getProperty(Property property) {
            if (prefix == null) {
                return property;
            }
            return prefix.dot(property);
        }

        private Expression expForCommaSeparatedFullName() {
            return getProperty(Contact.LAST_NAME).likeIgnoreCase(names[1])
                    .andExp(getProperty(Contact.FIRST_NAME).likeIgnoreCase(names[0] + "%"));
        }

        private Expression expForSpaceSeparatedFullName() {
            return getProperty(Contact.LAST_NAME).likeIgnoreCase(names[1] + "%")
                    .andExp(getProperty(Contact.FIRST_NAME).likeIgnoreCase(names[0]));
        }

        private Expression expForOnlyLastName() {
            return getProperty(Contact.LAST_NAME).likeIgnoreCase(names[1] + "%");
        }

        private Expression createCompanyExpression() {
            return getProperty(Contact.IS_COMPANY).eq(true).
                    andExp(getProperty(Contact.LAST_NAME).likeIgnoreCase(searchString + "%"));
        }
    }

}
