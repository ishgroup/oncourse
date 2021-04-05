import ImportTemplatesService from "../../automation/containers/import-templates/services/ImportTemplatesService";
import ModuleService from "../modules/services/ModuleService";
import QualificationService from "../qualifications/services/QualificationService";
import RoomService from "../rooms/services/RoomService";
import SiteService from "../sites/services/SiteService";
import AccountService from "../accounts/services/AccountService";
import PaymentInService from "../paymentsIn/services/PaymentInService";
import PaymentOutService from "../paymentsOut/services/PaymentOutService";
import PayslipService from "../payslips/services/PayslipService";
import TransactionService from "../transactions/services/TransactionService";
import CorporatePassService from "../corporatePasses/services/CorporatePassService";
import BankingService from "../bankings/services/BankingService";
import ScriptsService from "../../automation/containers/scripts/services/ScriptsService";
import AuditsService from "../../audits/services/AuditsService";
import { appendComponents, ParseScriptBody } from "../../automation/containers/scripts/utils";
import DiscountService from "../discounts/services/DiscountService";
import { ApiMethods } from "../../../model/common/apiHandlers";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import InvoiceService from "../invoices/services/InvoiceService";
import WaitingListService from "../waitingLists/services/WaitingListService";
import ApplicationService from "../applications/service/ApplicationService";
import MembershipProductService from "../membershipProducts/services/MembershipProductService";
import VoucherProductService from "../voucherProducts/services/VoucherProductService";
import CertificateService from "../certificates/services/CertificateService";
import SaleService from "../sales/services/SaleService";
import SurveyService from "../survey/services/SurveyService";
import ArticleProductService from "../articleProducts/service/ArticleProductService";
import MessageService from "../messages/services/MessageService";
import { formatToDateOnly } from "../../../common/utils/dates/datesNormalizing";
import { preformatInvoice } from "../invoices/utils";
import ContactsService from "../contacts/services/ContactsService";
import { PayLineWithDefer } from "../../../model/entities/Payslip";
import CourseClassService from "../courseClasses/services/CourseClassService";
import OutcomeService from "../outcomes/services/OutcomeService";
import CourseService from "../courses/services/CourseService";

export const getEntityItemById = (entity: string, id: number, method?: ApiMethods): Promise<any> => {
  switch (entity) {
    case "Audit": {
      return AuditsService.getAuditItem(id);
    }

    case "Module": {
      return ModuleService.getModule(id);
    }

    case "Qualification": {
      return QualificationService.getQualification(id);
    }

    case "Room": {
      return RoomService.getRoom(id);
    }

    case "Site": {
      return SiteService.getSite(id);
    }

    case "Account": {
      return AccountService.getAccount(id);
    }

    case "Invoice": {
      return InvoiceService.getInvoice(id);
    }

    case "PaymentIn": {
      return PaymentInService.getPaymentIn(id);
    }

    case "PaymentOut": {
      return PaymentOutService.getPaymentOut(id);
    }

    case "Payslip": {
      return PayslipService.getPayslip(id).then(response => {
        response.paylines.forEach((p: PayLineWithDefer) => {
          p.deferred = true;
        });

        return response;
      });
    }

    case "AccountTransaction": {
      return TransactionService.getTransaction(id);
    }

    case "CorporatePass": {
      return CorporatePassService.getCorporatePass(id);
    }

    case "Script": {
      return ScriptsService.getScriptItem(id).then(response => ParseScriptBody(response));
    }

    case "Import": {
      return ImportTemplatesService.get(id);
    }

    case "Banking": {
      return BankingService.getBanking(id);
    }

    case "Discount": {
      return DiscountService.getDiscount(id);
    }

    case "WaitingList": {
      return WaitingListService.getWaitingList(id);
    }

    case "Application": {
      return ApplicationService.getApplication(id);
    }

    case "MembershipProduct": {
      return MembershipProductService.getMembershipProduct(id);
    }

    case "VoucherProduct": {
      return VoucherProductService.getVoucherProduct(id);
    }

    case "ArticleProduct": {
      return ArticleProductService.getArticleProduct(id);
    }

    case "Certificate": {
      return CertificateService.getCertificate(id);
    }

    case "Sale":
    case "ProductItem": {
      return SaleService.getSale(id);
    }

    case "Survey": {
      return SurveyService.getSurveyItem(id);
    }

    case "Message": {
      return MessageService.getMessage(id);
    }

    case "Contact": {
      return ContactsService.getContact(id);
    }

    case "CourseClass": {
      return CourseClassService.getCourseClass(id);
    }

    case "Outcome": {
      return OutcomeService.getOutcome(id);
    }

    case "Course": {
      return CourseService.getCourse(id);
    }

    default:
      return null;
  }
};

export const updateEntityItemById = (entity: string, id: number, item: any, method?: ApiMethods): Promise<any> => {
  switch (entity) {
    case "Module": {
      return ModuleService.updateModule(id, item);
    }

    case "Qualification": {
      return QualificationService.updateQualification(id, item);
    }

    case "Room": {
      return RoomService.updateRoom(id, item);
    }

    case "Site": {
      return SiteService.updateSite(id, item);
    }

    case "Contact": {
      return ContactsService.updateContact(id, item);
    }

    case "Account": {
      return AccountService.updateAccount(id, item);
    }

    case "Invoice": {
      return InvoiceService.updateInvoice(id, preformatInvoice(item));
    }

    case "Payslip": {
      const paylines = JSON.parse(JSON.stringify(item.paylines.filter(p => p.deferred)));

      paylines.forEach(i => {
        delete i.deferred;
      });

      return PayslipService.updatePayslip(id, { ...item, paylines });
    }

    case "CorporatePass": {
      return CorporatePassService.updateCorporatePass(id, item);
    }

    case "Banking": {
      return BankingService.updateBanking(id, item);
    }

    case "Script": {
      if (method === "PATCH") {
        return ScriptsService.patchScriptItem(id, appendComponents(item));
      }
      return ScriptsService.saveScriptItem(id, appendComponents(item));
    }

    case "Discount": {
      return DiscountService.updateDiscount(id, item);
    }

    case "WaitingList": {
      return WaitingListService.updateWaitingList(id, item);
    }

    case "Application": {
      return ApplicationService.updateApplication(id, item);
    }

    case "MembershipProduct": {
      return MembershipProductService.updateMembershipProduct(id, item);
    }

    case "VoucherProduct": {
      return VoucherProductService.updateVoucherProduct(id, item);
    }

    case "ArticleProduct": {
      return ArticleProductService.updateArticleProduct(id, item);
    }

    case "Certificate": {
      return CertificateService.updateCertificate(id, item);
    }

    case "Sale":
    case "ProductItem": {
      return SaleService.updateSale(id, item);
    }

    case "Survey": {
      return SurveyService.updateSurveyItem(id, item);
    }

    case "PaymentIn": {
      return PaymentInService.updatePaymentIn(id, formatToDateOnly(item.dateBanked), item.administrationCenterId);
    }

    case "PaymentOut": {
      return PaymentOutService.updatePaymentOut(id, item);
    }

    case "Course": {
      return CourseService.update(id, item);
    }

    default:
      return null;
  }
};

export const updateEntityItemByIdErrorHandler = (response: any, entity: string, form: string, item: any) => {
  switch (entity) {
    default: {
      return FetchErrorHandler(response, `${entity} was not updated`);
    }
  }
};
