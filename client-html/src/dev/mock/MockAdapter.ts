/**
 * Service for mock backend functionality, used in dev mode
 * Library: axios-mock-adapter - https://github.com/ctimmerm/axios-mock-adapter
 * */

import axiosMockAdapter from "axios-mock-adapter";
import { defaultAxios } from "../../js/common/services/DefaultHttpClient";
import { CreateMockDB, MockDB } from "./MockDB";
import { preferenceApiMock } from "./api/preference/PreferenceApiMock";
import { holidayApiMock } from "./api/preference/HolidayApiMock";
import { IntegrationsApiMock } from "./api/preference/IntegrationsApiMock";
import { DataCollectionFormsApiMock } from "./api/preference/DataCollectionFormsApiMock";
import { DataCollectionRulesApiMock } from "./api/preference/DataCollectionRulesApiMock";
import { auditApiMock } from "./api/audit/AuditApiMock";
import { listApiMock } from "./api/common/ListApiMock";
import { TagApiMock } from "./api/common/TagApiMock";
import { PaymentApiMock } from "./api/preference/PaymentApiMock";
import { TaxTypesApiMock } from "./api/preference/TaxTypesApiMock";
import { ConcessionTypesApiMock } from "./api/preference/ConcessionTypesApiMock";
import { ContactRelationTypesApiMock } from "./api/preference/ContactRelationTypesApiMock";
import { CustomFieldApiMock } from "./api/preference/CustomFieldApiMock";
import { filterApiMock } from "./api/common/FilterApiMock";
import { AvetmissExportApiMock } from "./api/export/AvetmissExportApiMock";
import { dashboardApiMock } from "./api/dashboard/DashboardApiMock";
import { UserPreferenceApiMock } from "./api/preference/UserPreferenceApiMock";
import { WaitingListApiMock } from "./api/entities/WaitingListApiMock";
import { SiteApiMock } from "./api/entities/SiteApiMock";
import { RoomApiMock } from "./api/entities/RoomApiMock";
import { AccountApiMock } from "./api/entities/AccountApiMock";
import { CourseClassApiMock } from "./api/entities/CourseClassApiMock";
import { AssessmentApiMock } from "./api/entities/AssessmentApiMock";
import { OutcomeApiMock } from "./api/entities/OutcomeApiMock";
import { TimetableApiMock } from "./api/timetable/TimetableApiMock";
import { CourseApiMock } from "./api/entities/CourseApiMock";
import { QualificationApiMock } from "./api/entities/QualificationApiMock";
import { ModuleApiMock } from "./api/entities/ModuleApiMock";
import { EnrolmentApiMock } from "./api/entities/EnrolmentApiMock";
import { ArticleProductApiMock } from "./api/entities/ArticleProductApiMock";
import { InvoiceApiMock } from "./api/entities/InvoiceApiMock";
import { PaymentInApiMock } from "./api/entities/PaymentInApiMock";
import { PaymentOutApiMock } from "./api/entities/PaymentOutApiMock";
import { CorporatePassApiMock } from "./api/entities/CorporatePassApiMock";
import { AccountTransactionApiMock } from "./api/entities/AccountTransactionApiMock";
import { PayslipApiMock } from "./api/entities/PayslipApiMock";
import { BankingApiMock } from "./api/entities/BankingApiMock";
import { DiscountApiMock } from "./api/entities/DiscountApiMock";
import { ApplicationApiMock } from "./api/entities/ApplicationApiMock";
import { CertificateApiMock } from "./api/entities/CertificateApiMock";
import { SurveyApiMock } from "./api/entities/SurveyApiMock";
import { MessageApiMock } from "./api/entities/MessageApiMock";
import { VoucherProductApiMock } from "./api/entities/VoucherProductApiMock";
import { ContactApiMock } from "./api/entities/ContactApiMock";
import { MembershipProductApiMock } from "./api/entities/MembershipProductApiMock";
import { SalesApiMock } from "./api/entities/SalesApiMock";
import { ScriptApiMock } from "./api/automation/ScriptApiMock";
import { ExportTemplateApiMock } from "./api/automation/ExportTemplateApiMock";
import { ReportApiMock } from "./api/automation/ReportApiMock";
import { ReportOverlayApiMock } from "./api/automation/ReportOverlayApiMock";
import { AccessApiMock } from "./api/common/AccessApiMock";
import { EmailTemplateApiMock } from "./api/automation/EmailTemplateApiMock";
import { ImportTemplateApiMock } from "./api/automation/ImportTemplateApiMock";
import { EntityRelationTypesApiMock } from "./api/preference/EntityRelationTypesApiMock";
import { DocumentApiMock } from "./api/entities/DocumentApiMock";
import { PriorLearningApiMock } from "./api/entities/PriorLearningApiMock";
import { TutorRoleApiMock } from "./api/preference/TutorRoleApiMock";
import { FundingContractsApiMock } from "./api/preference/FundingContractsApiMock";
import { CheckoutApiMock } from "./api/checkout/CheckoutApiMock";
import { GradingTypesApiMock } from "./api/preference/GradingTypesApiMock";

export const initMockDB = () => new MockAdapter();

export class MockAdapter {
  public api = new axiosMockAdapter(defaultAxios);

  public db: MockDB = CreateMockDB();

  constructor() {
    // Include mock services

    // Preferences
    DataCollectionRulesApiMock.apply(this);
    DataCollectionFormsApiMock.apply(this);
    preferenceApiMock.apply(this);
    holidayApiMock.apply(this);
    IntegrationsApiMock.apply(this);
    PaymentApiMock.apply(this);
    TaxTypesApiMock.apply(this);
    ConcessionTypesApiMock.apply(this);
    ContactRelationTypesApiMock.apply(this);
    EntityRelationTypesApiMock.apply(this);
    CustomFieldApiMock.apply(this);
    TutorRoleApiMock.apply(this);
    FundingContractsApiMock.apply(this);
    GradingTypesApiMock.apply(this);

    // User Preferences
    UserPreferenceApiMock.apply(this);

    // Export
    AvetmissExportApiMock.apply(this);

    // Audit
    auditApiMock.apply(this);

    // Common
    listApiMock.apply(this);
    filterApiMock.apply(this);
    TagApiMock.apply(this);
    AccessApiMock.apply(this);

    // Dashboard
    dashboardApiMock.apply(this);

    // Entities
    WaitingListApiMock.apply(this);
    SiteApiMock.apply(this);
    RoomApiMock.apply(this);
    AccountApiMock.apply(this);
    CourseClassApiMock.apply(this);
    CourseApiMock.apply(this);
    QualificationApiMock.apply(this);
    AssessmentApiMock.apply(this);
    OutcomeApiMock.apply(this);
    ModuleApiMock.apply(this);
    EnrolmentApiMock.apply(this);
    ArticleProductApiMock.apply(this);
    InvoiceApiMock.apply(this);
    PaymentInApiMock.apply(this);
    PaymentOutApiMock.apply(this);
    CorporatePassApiMock.apply(this);
    AccountTransactionApiMock.apply(this);
    PayslipApiMock.apply(this);
    BankingApiMock.apply(this);
    DiscountApiMock.apply(this);
    DocumentApiMock.apply(this);
    ApplicationApiMock.apply(this);
    CertificateApiMock.apply(this);
    SurveyApiMock.apply(this);
    MessageApiMock.apply(this);
    VoucherProductApiMock.apply(this);
    ContactApiMock.apply(this);
    MembershipProductApiMock.apply(this);
    SalesApiMock.apply(this);
    PriorLearningApiMock.apply(this);

    //  Automation
    ScriptApiMock.apply(this);
    ExportTemplateApiMock.apply(this);
    ReportApiMock.apply(this);
    ReportOverlayApiMock.apply(this);
    EmailTemplateApiMock.apply(this);
    ImportTemplateApiMock.apply(this);

    // Timetable
    TimetableApiMock.apply(this);

    // Checkout
    CheckoutApiMock.apply(this);

    // Handle get login request
    this.api.onGet("/").reply(config => promiseResolve(config));

    // Handle all other requests
    this.api.onAny().reply(config => {
      console.warn("UNHANDLED REQUEST");
      console.log(config);
      console.log(this.api);
      return promiseReject(config);
    });
  }
}

// Resolve function with logger
export const promiseResolve = (config, data = {}, headers = {}) => {
  console.log("%c ----------------", "color: black");
  console.log(`%c Api request to: /${config.url}`, "color: #bada55");
  console.log(`%c Api request method: ${config.method}`, "color: #bada55");
  console.log(`%c request params:`, "color: #bada55");
  if (config.method === "get") {
    console.log(config.params);
  } else {
    console.log(config.data && [parseJson(config.data)]);
  }
  console.log(`%c response params:`, "color: #bada55");
  console.log([data]);
  console.log("%c ----------------", "color: black");

  return [200, data, headers];
};

// Reject function with logger
export const promiseReject = (config, data = {}, headers = {}) => {
  console.log(`%c Api request ${config.method} to: /${config.url}`, "color: red");
  console.log(`%c request params:`, "color: #bada55");
  if (config.method === "get") {
    console.log(config.params);
  } else {
    console.log(config.data && [parseJson(config.data)]);
  }
  console.log(`%c request params:`, "color: #bada55");
  return [400, data, headers];
};

const parseJson = data => {
  let json;

  try {
    json = JSON.parse(data);
  } catch (e) {
    json = data;
  }

  return json;
};
