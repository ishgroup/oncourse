/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.liquibase.IshTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateCreatedOnModifiedOnColumns extends IshTaskChange {

    private static Logger logger = LogManager.getLogger();

    private final String[] entities = {
            "Site",
            "EntityRelationType",
            "Report",
            "InvoiceLine_Discount",
            "Message",
            "PriorLearning",
            "Payslip",
            "Voucher_PaymentIn",
            "ProductItem",
            "PaymentOutLine",
            "Banking",
            "UnavailableRuleRelation",
            "SessionCourseClassTutor",
            "CourseClassPaymentPlanLine",
            "ContactRelationType",
            "VoucherProduct_Course",
            "Lead",
            "PaymentInLine",
            "Enrolment",
            "CourseClass",
            "CourseSession",
            "Outcome",
            "EntityRelation",
            "ACLAccessKey",
            "NodeRelation",
            "Language",
            "Discount_CourseClass",
            "DocumentVersion",
            "Tutor",
            "EmailTemplate",
            "Discount_Membership_RelationType",
            "GradingItem",
            "InvoiceDueDate",
            "IntegrationProperty",
            "NodeRequirement",
            "Script",
            "Course",
            "Module",
            "Invoice",
            "ACLRole",
            "Qualification",
            "CourseClassTutor",
            "ExportTemplate",
            "CorporatePass_Product",
            "Node",
            "CorporatePass_CourseClass",
            "WaitingList",
            "Student",
            "IntegrationConfiguration",
            "ContactRelation",
            "Checkout",
            "Contact",
            "Discount_ConcessionType",
            "TrainingPackage",
            "Module_Course",
            "CorporatePass_Discount",
            "Account",
            "Attendance",
            "Room",
            "Certificate_Outcome",
            "ClassCost",
            "BinaryRelation",
            "StudentConcession",
            "Country",
            "UnavailableRule",
            "Preference",
            "Discount_Membership",
            "PayLine",
            "SystemUser",
            "GradingType",
            "Product",
            "Certificate",
            "Discount",
            "CorporatePass",
            "Import",
            "Lead_Site",
            "Session_Module",
            "InvoiceLine",
            "Document",
            "LeadItem",
            "Survey"
    };

    @Override
    public void execute(Database database) throws CustomChangeException {
        logger.warn("Running upgrade...");

        for (var entity : entities) {
            JdbcConnection connection = (JdbcConnection) database.getConnection();
            try (var statement = connection.createStatement()) {
                logger.warn("Upgrade " + entity);
                statement.execute(String.format("UPDATE %s SET createdOn = IF(modifiedOn is null, NOW(), modifiedOn) where createdOn IS NULL", entity));
                statement.execute(String.format("UPDATE %s SET modifiedOn = IF(createdOn is null, NOW(), createdOn) where modifiedOn IS NULL", entity));
                connection.commit();
            } catch (Exception e) {
                logger.catching(e);
                throw new RuntimeException(e);
            }
        }

    }
}