/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { formatToDateOnly } from "ish-ui";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import DocumentsService from "../../../common/components/form/documents/services/DocumentsService";
import { setListEditRecord } from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import EntityService from "../../../common/services/EntityService";
import history from '../../../constants/History';
import { EntityName } from "../../../model/entities/common";
import { EnrolmentExtended } from "../../../model/entities/Enrolment";
import { PayLineWithDefer } from "../../../model/entities/Payslip";
import AuditsService from "../../audits/services/AuditsService";
import ImportTemplatesService from "../../automation/containers/import-templates/services/ImportTemplatesService";
import AccountService from "../accounts/services/AccountService";
import ApplicationService from "../applications/service/ApplicationService";
import ArticleProductService from "../articleProducts/service/ArticleProductService";
import AssessmentService from "../assessments/services/AssessmentService";
import AssessmentSubmissionService from "../assessmentSubmissions/service/AssessmentSubmissionService";
import BankingService from "../bankings/services/BankingService";
import CertificateService from "../certificates/services/CertificateService";
import { formatRelationsBeforeSave } from "../contacts/Contacts";
import ContactsService from "../contacts/services/ContactsService";
import { getContactFullName } from "../contacts/utils";
import CorporatePassService from "../corporatePasses/services/CorporatePassService";
import CourseClassService from "../courseClasses/services/CourseClassService";
import CourseService from "../courses/services/CourseService";
import { processCustomFields } from "../customFieldTypes/utils";
import DiscountService from "../discounts/services/DiscountService";
import EnrolmentService from "../enrolments/services/EnrolmentService";
import FacultyService from "../faculties/services/FacultyService";
import InvoiceService from "../invoices/services/InvoiceService";
import { preformatInvoice, processInvoicePaymentPlans, setInvoiceLinesTotal } from "../invoices/utils";
import LeadService from "../leads/services/LeadService";
import MembershipProductService from "../membershipProducts/services/MembershipProductService";
import membershipProductService from "../membershipProducts/services/MembershipProductService";
import MessageService from "../messages/services/MessageService";
import ModuleService from "../modules/services/ModuleService";
import OutcomeService from "../outcomes/services/OutcomeService";
import PaymentInService from "../paymentsIn/services/PaymentInService";
import { getActivePaymentOutMethods } from '../paymentsOut/actions';
import PaymentOutService from "../paymentsOut/services/PaymentOutService";
import { getPaymentOutFromModel } from "../paymentsOut/utils";
import PayslipService from "../payslips/services/PayslipService";
import PriorLearningService from "../priorLearnings/services/PriorLearningService";
import QualificationService from "../qualifications/services/QualificationService";
import RoomService from "../rooms/services/RoomService";
import SaleService from "../sales/services/SaleService";
import SiteService from "../sites/services/SiteService";
import SurveyService from "../survey/services/SurveyService";
import TransactionService from "../transactions/services/TransactionService";
import VoucherProductService from "../voucherProducts/services/VoucherProductService";
import WaitingListService from "../waitingLists/services/WaitingListService";
import { mapEntityDisplayName } from "./utils";

const defaultUnknown = () => {
  console.error("Unknown entity name");
  return null;
};

export const getEntityItemById = (entity: EntityName, id: number): Promise<any> => {
  switch (entity) {
    case "Account":
      return AccountService.getAccount(id);
    case "AccountTransaction":
      return TransactionService.getTransaction(id);
    case "Application":
      return ApplicationService.getApplication(id);
    case "Audit":
      return AuditsService.getAuditItem(id);
    case "Assessment":
      return AssessmentService.getAssessment(id);
    case "AssessmentSubmission":
      return AssessmentSubmissionService.getAssessmentSubmission(id);
    case "ArticleProduct":
      return ArticleProductService.getArticleProduct(id);
    case "Banking":
      return BankingService.getBanking(id);
    case "Certificate":
      return CertificateService.getCertificate(id);
    case "Contact":
      return ContactsService.getContact(id);
    case "CorporatePass":
      return CorporatePassService.getCorporatePass(id);
    case "Course":
      return CourseService.getCourse(id);
    case "MembershipProduct":
      return MembershipProductService.getMembershipProduct(id);
    case "Message":
      return MessageService.getMessage(id);
    case "Module":
      return ModuleService.getModule(id);
    case "Outcome":
      return OutcomeService.getOutcome(id);
    case "PaymentIn":
      return PaymentInService.getPaymentIn(id);
    case "PaymentOut":
      return PaymentOutService.getPaymentOut(id);

    case "Payslip": {
      return PayslipService.getPayslip(id).then(response => {
        response.paylines.forEach((p: PayLineWithDefer) => {
          p.deferred = true;
        });

        return response;
      });
    }

    case "PriorLearning":
      return PriorLearningService.getPriorLearning(id);
    case "Qualification":
      return QualificationService.getQualification(id);
    case "Room":
      return RoomService.getRoom(id);
    case "Sale":
    case "ProductItem":
      return SaleService.getSale(id);
    case "Site":
      return SiteService.getSite(id);
    case "Survey":
      return SurveyService.getSurveyItem(id);
    case "VoucherProduct":
      return VoucherProductService.getVoucherProduct(id);
    case "WaitingList":
      return WaitingListService.getWaitingList(id);
    case "Import":
      return ImportTemplatesService.get(id);
    case "CourseClass":
      return CourseClassService.getCourseClass(id);
    case "Lead":
      return LeadService.getLead(id);

    case "AbstractInvoice":
    case "Invoice": {
      return InvoiceService.getInvoice(id).then(invoice => {
        return setInvoiceLinesTotal({ ...invoice, paymentPlans: processInvoicePaymentPlans(invoice.paymentPlans) });
      });
    }
    
    case "Enrolment": {
      return EnrolmentService.getEnrolment(id).then(async (en: EnrolmentExtended) => {
        for (const a of en.assessments) {
          await EntityService.getPlainRecords(
            "Contact",
            "firstName,lastName",
            `tutor.assessmentClassTutors.assessmentClass.courseClass.id is ${en.courseClassId} and tutor.assessmentClassTutors.assessmentClass.assessment.id is ${a.id}`
          )
            .then(res => {
              a.tutors = res.rows.map(r => ({
                contactId: Number(r.id),
                tutorName: getContactFullName({ firstName: r.values[0], lastName: r.values[1] })
              }));
            });
        }

        if (en.relatedFundingSourceId === null) {
          en.relatedFundingSourceId = -1;
        }

        return en;
      });
    }

    case "Faculty":
      return FacultyService.get(id);

    case "Discount": {
      return DiscountService.getDiscount(id).then(discount => {
        discount.discountMemberships.forEach(el => {
          el.contactRelations = el.contactRelations.sort();
        });
        return discount;
      });
    }

    case "Document":
      return DocumentsService.getDocumentItem(id);

    default:
      return defaultUnknown();
  }
};

export const updateEntityItemById = (entity: EntityName, id: number, item: any): Promise<any> => {
  delete item.notes;

  processCustomFields(item);

  switch (entity) {
    case "Account":
      return AccountService.updateAccount(id, item);
    case "Application":
      return ApplicationService.updateApplication(id, item);
    case "ArticleProduct":
      return ArticleProductService.updateArticleProduct(id, item);
    case "Assessment":
      return AssessmentService.updateAssessment(id, item);
    case "AssessmentSubmission":
      return AssessmentSubmissionService.updateAssessmentSubmission(id, item);
    case "Banking":
      return BankingService.updateBanking(id, item);
    case "Certificate":
      return CertificateService.updateCertificate(id, item);

    case "Contact": {
      const itemToSave = { ...item };

      if (itemToSave.student) {
        itemToSave.student = {
          ...item.student,
          education: null
        };
        delete itemToSave.student.education;
      }

      itemToSave.relations = [...formatRelationsBeforeSave(itemToSave?.relations || [])];

      if (itemToSave.isCompany) delete item.firstName;

      return ContactsService.updateContact(id, itemToSave);
    }

    case "CorporatePass":
      return CorporatePassService.updateCorporatePass(id, item);
    case "Course":
      return CourseService.update(id, item);
    case "Discount":
      return DiscountService.updateDiscount(id, item);
    case "Document":
      return DocumentsService.updateDocumentItem(id, item);
      
    case "Enrolment": {
      const withAssessmenProcessed = {
        ...item,
        assessments: item.assessments.map(({ tutors, ...rest }) => rest)
      };
      
      return EnrolmentService.updateEnrolment(id, withAssessmenProcessed);
    }

    case "Faculty":
      return FacultyService.update(id, item);
      
    case "AbstractInvoice":
    case "Invoice":
      return InvoiceService.updateInvoice(id, preformatInvoice(item));
    case "Lead":
      return LeadService.updateLead(id, item);
    case "MembershipProduct":
      return MembershipProductService.updateMembershipProduct(id, item);

    case "Module": {
      if (item.isOffered === undefined) {
        item.isOffered = false;
      }

      if (item.type === undefined) {
        item.type = "UNIT OF COMPETENCY";
      }
      return ModuleService.updateModule(id, item);
    }

    case "Outcome":
      return OutcomeService.updateOutcome(id, item);
    case "PaymentIn":
      return PaymentInService.updatePaymentIn(id, formatToDateOnly(item.dateBanked), item.administrationCenterId);
    case "PaymentOut":
      return PaymentOutService.updatePaymentOut(id, item);

    case "Payslip": {
      const paylines = [...item?.paylines?.filter(p => p.deferred) || []];

      paylines.forEach(i => {
        delete i.deferred;
      });

      return PayslipService.updatePayslip(id, { ...item, paylines });
    }

    case "PriorLearning":
      return PriorLearningService.updatePriorLearning(id, item);

    case "Qualification": {
      if (item.isOffered === undefined) {
        item.isOffered = false;
      }
      return QualificationService.updateQualification(id, item);
    }

    case "Room":
      return RoomService.updateRoom(id, item);
    case "Sale":
    case "ProductItem":
      return SaleService.updateSale(id, item);

    case "Site": {
      if (item.isAdministrationCentre === undefined) {
        item.isAdministrationCentre = false;
      }

      if (item.isShownOnWeb === undefined) {
        item.isShownOnWeb = false;
      }

      if (item.isVirtual === undefined) {
        item.isVirtual = false;
      }
      return SiteService.updateSite(id, item);
    }

    case "Survey":
      return SurveyService.updateSurveyItem(id, item);
    case "WaitingList":
      return WaitingListService.updateWaitingList(id, item);
    case "VoucherProduct":
      return VoucherProductService.updateVoucherProduct(id, item);

    default:
      return defaultUnknown();
  }
};

export const createEntityItem = (entity: EntityName, item: any): Promise<any> => {
  processCustomFields(item);

  switch (entity) {
    case "Account":
      return AccountService.createAccount(item);
    case "Application":
      return ApplicationService.createApplication(item);
    case "Assessment":
      return AssessmentService.createAssessment(item);
    case "ArticleProduct":
      return ArticleProductService.createArticleProduct(item);
    case "AccountTransaction":
      return TransactionService.createTransaction(item);
    case "Certificate":
      return CertificateService.createCertificate(item);
    case "CorporatePass":
      return CorporatePassService.createCorporatePass(item);
    case "Discount":
      return DiscountService.createDiscount(item);
    case "Invoice":
    case "AbstractInvoice":
      return InvoiceService.createInvoice(preformatInvoice(item));
    case "Lead":
      return LeadService.createLead(item);
    case "MembershipProduct":
      return membershipProductService.createMembershipProduct(item);
    case "Outcome":
      return OutcomeService.create(item);
    case "PriorLearning":
      return PriorLearningService.createPriorLearning(item);
    case "Room":
      return RoomService.createRoom(item);
    case "VoucherProduct":
      return VoucherProductService.createVoucherProduct(item);
    case "WaitingList":
      return WaitingListService.createWaitingList(item);
    case "Course":
      return CourseService.create(item);
    case "PaymentOut": {
      const urlParams = new URLSearchParams(window.location.search);
      urlParams.delete("invoiceId");

      history.replace({
        pathname: history.location.pathname,
        search: decodeURIComponent(urlParams.toString())
      });

      return PaymentOutService.postPaymentOut(getPaymentOutFromModel(item));
    }

    case "Document": {
      const {
        name,
        description,
        shared,
        access,
        content,
        tags,
        versions,
        id
      } = item;
      
      if (id) {
        return DocumentsService.updateDocumentItem(id, item);
      }

      return DocumentsService.createDocument(
        name,
        description,
        shared,
        access,
        content,
        tags?.toString(),
        (versions && versions[0] && versions[0].fileName) || content.name
      );
    }

    case "Site": {
      if (item.isAdministrationCentre === undefined) {
        item.isAdministrationCentre = false;
      }
      if (item.isShownOnWeb === undefined) {
        item.isShownOnWeb = false;
      }
      if (item.isVirtual === undefined) {
        item.isVirtual = false;
      }
      return SiteService.createSite(item);
    }

    case "Qualification": {
      if (item.isOffered === undefined) {
        item.isOffered = false;
      }
      return QualificationService.createQualification(item);
    }

    case "Payslip": {
      const paylines = JSON.parse(JSON.stringify(item.paylines));

      paylines.forEach(i => {
        delete i.deferred;
      });

      const tags = JSON.parse(JSON.stringify(item.tags));

      tags.forEach(t => {
        delete t.active;
      });

      return PayslipService.createPayslip({ ...item, paylines, tags });
    }

    case "Module": {
      if (item.isOffered === undefined) {
        item.isOffered = false;
      }

      if (item.type === undefined) {
        item.type = "UNIT OF COMPETENCY";
      }

      return ModuleService.createModule(item);
    }

    case "Contact": {
      const { student, relations } = item;

      if (student) delete item.student.education;

      item.relations = formatRelationsBeforeSave(relations);

      if (item.isCompany) delete item.firstName;

      return ContactsService.createContact(item);
    }

    case "Banking": {
      const newBanking = { ...item };
      if (item && item.payments) {
        newBanking.payments = item.payments
          // @ts-ignore
          .filter(v => v.selected)
          .map(v => {
            const newPayment = { ...v };
            // @ts-ignore
            delete newPayment.selected;
            return newPayment;
          });
      }
      return BankingService.createBanking(newBanking);
    }

    case "Faculty":
      return FacultyService.create(item);
    
    default:
      return defaultUnknown();
  }
};

export const deleteEntityItemById = (entity: EntityName, id: number): Promise<any> => {
  switch (entity) {
    case "Account":
      return AccountService.removeAccount(id);
    case "Application":
      return ApplicationService.removeApplication(id);
    case "Assessment":
      return AssessmentService.removeAssessment(id);
    case "AssessmentSubmission":
      return AssessmentSubmissionService.removeAssessmentSubmission(id);
    case "Banking":
      return BankingService.removeBanking(id);
    case "Certificate":
      return CertificateService.removeCertificate(id);
    case "Contact":
      return ContactsService.deleteContact(id);
    case "CorporatePass":
      return CorporatePassService.removeCorporatePass(id);
    case "CourseClass":
      return CourseClassService.deleteCourseClass(id);
    case "Course":
      return CourseService.remove(id);
    case "Discount":
      return DiscountService.removeDiscount(id);
    case "Document":
      return DocumentsService.deleteDocumentItem(id);
    case "Invoice":
    case "AbstractInvoice":
      return InvoiceService.deleteQuote(id);
    case "Lead":
      return LeadService.removeLead(id);
    case "Message":
      return MessageService.removeMessage(id);
    case "Module":
      return ModuleService.removeModule(id);
    case "Outcome":
      return OutcomeService.removeOutcome(id);
    case "Payslip":
      return PayslipService.removePayslip(id);
    case "PriorLearning":
      return PriorLearningService.removePriorLearning(id);
    case "Faculty":
      return FacultyService.remove(id);
    case "Qualification":
      return QualificationService.removeQualification(id);
    case "Room":
      return RoomService.removeRoom(id);
    case "Site":
      return SiteService.removeSite(id);
    case "WaitingList":
      return EntityService.bulkDelete("WaitingList", { ids: [id] });
    default:
      return defaultUnknown();
  }
};

export const updateEntityItemByIdErrorHandler = (response: any, entity: EntityName, item: any, form?: string) => [
  ...FetchErrorHandler(response, `${mapEntityDisplayName(entity)} was not updated`),
  initialize(form || LIST_EDIT_VIEW_FORM_NAME, item)
];

export const createEntityItemByIdErrorHandler = (response: any, entity: EntityName, item: any, form?: string) => [
  ...FetchErrorHandler(response, `${mapEntityDisplayName(entity)} was not created`),
  initialize(form || LIST_EDIT_VIEW_FORM_NAME, item)
];

export const deleteEntityItemByIdErrorHandler = (response: any, entity: EntityName) => FetchErrorHandler(response, `${mapEntityDisplayName(entity)} was not deleted`);

export const getEntityItemByIdErrorHandler = (response: any) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];