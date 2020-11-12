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

import ish.oncourse.aql.impl.AqlParser;
import ish.oncourse.aql.impl.CompilationContext;
import ish.oncourse.aql.impl.Op;
import ish.oncourse.server.cayenne.Account;
import ish.oncourse.server.cayenne.AccountTransaction;
import ish.oncourse.server.cayenne.Application;
import ish.oncourse.server.cayenne.ArticleProduct;
import ish.oncourse.server.cayenne.Assessment;
import ish.oncourse.server.cayenne.Audit;
import ish.oncourse.server.cayenne.Banking;
import ish.oncourse.server.cayenne.Certificate;
import ish.oncourse.server.cayenne.ConcessionType;
import ish.oncourse.server.cayenne.Contact;
import ish.oncourse.server.cayenne.CorporatePass;
import ish.oncourse.server.cayenne.Course;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.Discount;
import ish.oncourse.server.cayenne.Document;
import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.MembershipProduct;
import ish.oncourse.server.cayenne.Message;
import ish.oncourse.server.cayenne.Module;
import ish.oncourse.server.cayenne.Outcome;
import ish.oncourse.server.cayenne.PaymentOut;
import ish.oncourse.server.cayenne.Payslip;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.oncourse.server.cayenne.Product;
import ish.oncourse.server.cayenne.ProductItem;
import ish.oncourse.server.cayenne.Qualification;
import ish.oncourse.server.cayenne.Room;
import ish.oncourse.server.cayenne.Script;
import ish.oncourse.server.cayenne.Site;
import ish.oncourse.server.cayenne.Student;
import ish.oncourse.server.cayenne.Survey;
import ish.oncourse.server.cayenne.VoucherProduct;
import ish.oncourse.server.cayenne.WaitingList;
import org.apache.cayenne.exp.parser.SimpleNode;

/**
 * Converter that process contact search operator `~` for root of the query.
 * Can be used only if query root is entity named 'Contact', 'Site', 'Room', 'Module', 'Invoice', 'Transaction',
 * 'Document', 'Account' or 'Qualification'.
 *

 */
public class EntityRootSearchConverter implements Converter<AqlParser.EntityRootSearchContext> {

    @Override
    public SimpleNode apply(AqlParser.EntityRootSearchContext search, CompilationContext ctx) {
        var entityName = ctx.getQueryRootEntity().getName();

        ctx.setCurrentPathJavaType(null);

        //please observe alphabetical order
        switch (entityName) {
            case Account.ENTITY_NAME: return new LazyAccountComparisonNode(Op.LIKE);
            case AccountTransaction.ENTITY_NAME: return new LazyTransactionComparisonNode(Op.LIKE);
            case Application.ENTITY_NAME: return new LazyApplicationComparisonNode(Op.LIKE);
            case ArticleProduct.ENTITY_NAME: return new LazyArticleProductComparisonNode(Op.LIKE);
            case Audit.ENTITY_NAME: return new LazyAuditComparisonNode(Op.LIKE);
            case Assessment.ENTITY_NAME: return new LazyAssessmentComparisonNode(Op.LIKE);
            case Banking.ENTITY_NAME: return new LazyBankingComparisonNode(Op.LIKE);
            case ConcessionType.ENTITY_NAME: return new LazyConcessionTypeComparisonNode(Op.LIKE);
            case Certificate.ENTITY_NAME: return new LazyContactAttributeComparisonNode(Op.LIKE, Certificate.STUDENT.dot(Student.CONTACT));
            case Contact.ENTITY_NAME: return new LazyContactComparisionNode(Op.LIKE);
            case CorporatePass.ENTITY_NAME: return new LazyCorporatePassComparisonNode(Op.LIKE);
            case Course.ENTITY_NAME: return new LazyCourseComparisionNode(Op.LIKE);
            case CourseClass.ENTITY_NAME: return new LazyCourseClassComparisionNode(Op.LIKE);
            case Discount.ENTITY_NAME: return new LazyDiscountComparisonNode(Op.LIKE);
            case Document.ENTITY_NAME: return new LazyDocumentComparisonNode(Op.LIKE);
            case Enrolment.ENTITY_NAME: return new LazyEnrolmentComparisionNode(Op.LIKE);
            case Invoice.ENTITY_NAME: return new LazyInvoiceComparisonNode(Op.LIKE);
            case Module.ENTITY_NAME: return new LazyModuleComparisonNode(Op.LIKE);
            case Outcome.ENTITY_NAME: return new LazyOutcomeComparisonNode(Op.LIKE);
            case Room.ENTITY_NAME: return new LazyRoomComparisonNode(Op.LIKE);
            case Script.ENTITY_NAME: return new LazyScriptComparisonNode(Op.LIKE);
            case Site.ENTITY_NAME: return new LazySiteComparisonNode(Op.LIKE);
            case Survey.ENTITY_NAME: return new LazySurveyComparisonNode(Op.LIKE);
            case Payslip.ENTITY_NAME: return new LazyPayslipComparisonNode(Op.LIKE);
            case PaymentIn.ENTITY_NAME: return new LazyContactAttributeComparisonNode(Op.LIKE, PaymentIn.PAYER);
            case Product.ENTITY_NAME:
            case MembershipProduct.ENTITY_NAME: return new LazyMembershipProductComparisonNode(Op.LIKE);
            case VoucherProduct.ENTITY_NAME: return new LazyVoucherProductComparisonNode(Op.LIKE);
            case ProductItem.ENTITY_NAME: return new LazyProductItemComparisonNode(Op.LIKE);
            case Qualification.ENTITY_NAME: return new LazyQualificationComparisonNode(Op.LIKE);
            case WaitingList.ENTITY_NAME: return new LazyWaitingListComparisionNode(Op.LIKE);
            case Message.ENTITY_NAME: return new LazyNameComparisionNode(Op.LIKE, Message.EMAIL_SUBJECT.getName());
            case PaymentOut.ENTITY_NAME: return new LazyContactAttributeComparisonNode(Op.LIKE, PaymentOut.PAYEE);

            default:
                ctx.reportError(search.start.getLine(), search.start.getCharPositionInLine(),
                        "Unable to use quick search for entity '" + entityName + '\'');
                return null;
        }
    }
}
