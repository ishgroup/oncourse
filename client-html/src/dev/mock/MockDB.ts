import {
  Account,
  Holiday,
  Audit,
  AvetmissExportOutcome,
  AvetmissExportSettings,
  ConcessionType,
  ContactRelationType,
  CustomFieldType,
  DataCollectionForm,
  DataCollectionRule,
  FieldType,
  Filter,
  PaymentType,
  StatisticData,
  Tax,
  UserPreference,
  Currency,
  ColumnWidth,
  WaitingList,
  Country,
  PermissionResponse,
  EmailTemplate,
  ImportModel,
  EntityRelationType,
  Language
} from "@api/model";
import { mockPreferences } from "./data/preferences";
import { mockIntegrations } from "./data/integrations";
import { mockDataCollectionForms } from "./data/dataCollectionForms";
import { mockDataCollectionFormFieldTypes } from "./data/dataCollectionFormFieldTypes";
import { mockDataCollectionRules } from "./data/dataCollectionRules";
import { mockHolidays } from "./data/holidays";
import { mockAccounts } from "./data/accounts";
import { mockAudits } from "./data/audits";
import { mockPaymentTypes } from "./data/paymentTypes";
import { mockTaxTypes } from "./data/taxTypes";
import { mockConcessionTypes } from "./data/concessionTypes";
import { mockContactRelationTypes } from "./data/contactRelationTypes";
import { mockCustomFields } from "./data/customFields";
import { mockScripts } from "./data/automation/scripts";
import { mockListExport } from "./data/listExport";
import { mockAvetmissExport, mockAvetmissExportOutcomes, mockAvetmissExportSettings } from "./data/avetmissExport";
import { mockFilters } from "./data/filters";
import { mockDashboard } from "./data/dashboard";
import { mockUserPreferences } from "./data/userPreferences";
import { mockTags } from "./data/tags";
import { mockWaitingLists } from "./data/entities/waitingLists";
import { mockContacts } from "./data/entities/contacts";
import { mockCourses } from "./data/entities/courses";
import { mockSites } from "./data/entities/sites";
import { mockRooms } from "./data/entities/rooms";
import { mockCourseClasses } from "./data/entities/courseClasses";
import { mockTimetable } from "./data/timetable";

import { mockCurrency } from "./data/currency";
import { mockColumnsSettings } from "./data/columns";
import { mockTimezones } from "./data/timezones";
import { mockCountries } from "./data/countries";
import { mockTraineeships } from "./data/entities/traineeships";
import { mockTraineeshipCourses } from "./data/entities/traineeshipCourses";
import { mockQualifications } from "./data/entities/qualifications";
import { mockAssessments } from "./data/entities/assessments";
import { mockOutcomes } from "./data/entities/outcomes";
import { mockModules } from "./data/entities/modules";
import { mockEnrolments } from "./data/entities/enrolments";
import { mockArticleProducts } from "./data/entities/articleProducts";
import { mockInvoices } from "./data/entities/invoices";
import { mockPaymentsIn } from "./data/entities/paymentsIn";
import { mockPaymentsOut } from "./data/entities/paymentsOut";
import { mockCorporatePasses } from "./data/entities/corporatePasses";
import { mockAccountTransactions } from "./data/entities/accountTransactions";
import { mockPayslips } from "./data/entities/payslips";
import { mockBankings } from "./data/entities/bankings";
import { mockDiscounts } from "./data/entities/discounts";
import { mockApplications } from "./data/entities/applications";
import { mockCertificates } from "./data/entities/certificates";
import { mockSurvey } from "./data/entities/survey";
import { mockMessage } from "./data/entities/messages";
import { mockVoucherProducts } from "./data/entities/voucherProducts";
import { mockMembershipProducts } from "./data/entities/membershipProducts";
import { mockSales } from "./data/entities/sales";
import { mockExportTemplates } from "./data/automation/exportTemplates";
import { mockReports } from "./data/automation/reports";
import { mockReportOverlays } from "./data/automation/reportOverlays";
import { mockAccessApi } from "./data/accessApi";
import { mockEmailTemplates } from "./data/automation/emailTemplates";
import { mockImportTemplates } from "./data/automation/importTemplates";
import { mockNotes } from "./data/notes";
import { mockEntityRelationTypes } from "./data/entityRelationTypes";
import { mockLanguages } from "./data/languages";
import { mockDocuments } from "./data/entities/documents";

export const CreateMockDB = (): MockDB => new MockDB();

export class MockDB {
  integrations: any;

  preference: any;

  userPreferences: UserPreference[];

  scripts: any;

  exportTemplates: any;

  reportOverlays: any;

  reports: any;

  emailTemplates: EmailTemplate[];

  importTemplates: ImportModel[];

  listExport: any;

  dataCollectionFormFieldTypes: FieldType[];

  dataCollectionForms: DataCollectionForm[];

  dataCollectionRules: DataCollectionRule[];

  holiday: Holiday[];

  account: Account[];

  audit: Audit[];

  paymentTypes: PaymentType[];

  taxTypes: Tax[];

  concessionTypes: ConcessionType[];

  contactRelationTypes: ContactRelationType[];

  entityRelationTypes: EntityRelationType[];

  customFields: CustomFieldType[];

  filters: Filter[];

  evetmissExport: any;

  exportOutcomes: AvetmissExportOutcome[];

  exportSettings: AvetmissExportSettings;

  dashboard: any;

  currency: Currency;

  columnsSettings: ColumnWidth;

  timezones: string[];

  tags: any;

  waitingLists: WaitingList[];

  contacts: any;

  courses: any;

  sites: any;

  countries: Country[];

  rooms: any;

  courseClasses: any;

  timetable: any;

  traineeships: any;

  traineeshipCourses: any;

  qualifications: any;

  assessments: any;

  outcomes: any;

  modules: any;

  enrolments: any;

  articleProducts: any;

  invoices: any;

  paymentsIn: any;

  paymentsOut: any;

  corporatePasses: any;

  accountTransactions: any;

  payslips: any;

  bankings: any;

  discounts: any;

  applications: any;

  certificates: any;

  surveys: any;

  messages: any;

  voucherProducts: any;

  membershipProducts: any;

  sales: any;

  accessApi: PermissionResponse;

  notes: any;

  languages: Language[];

  documents: any;

  constructor() {
    this.init();
  }

  init(): void {
    // Preferences App
    this.dataCollectionFormFieldTypes = mockDataCollectionFormFieldTypes.call(this);
    this.dataCollectionForms = mockDataCollectionForms.call(this);
    this.dataCollectionRules = mockDataCollectionRules.call(this);
    this.integrations = mockIntegrations.call(this);
    this.preference = mockPreferences.call(this);
    this.userPreferences = mockPreferences.call(this);
    this.holiday = mockHolidays.call(this);
    this.paymentTypes = mockPaymentTypes.call(this);
    this.taxTypes = mockTaxTypes.call(this);
    this.concessionTypes = mockConcessionTypes.call(this);
    this.contactRelationTypes = mockContactRelationTypes.call(this);
    this.entityRelationTypes = mockEntityRelationTypes.call(this);
    this.customFields = mockCustomFields.call(this);
    this.listExport = mockListExport.call(this);
    this.notes = mockNotes.call(this);
    this.languages = mockLanguages.call(this);

    //  Automation
    this.scripts = mockScripts.call(this);
    this.exportTemplates = mockExportTemplates.call(this);
    this.reports = mockReports.call(this);
    this.reportOverlays = mockReportOverlays.call(this);
    this.emailTemplates = mockEmailTemplates.call(this);
    this.importTemplates = mockImportTemplates.call(this);

    //  Preferences columns
    this.columnsSettings = mockColumnsSettings.call(this);

    // User Preferences
    this.userPreferences = mockUserPreferences.call(this);

    // AVETMISS8 Export
    this.evetmissExport = mockAvetmissExport.call(this);
    this.exportOutcomes = mockAvetmissExportOutcomes.call(this);
    this.exportSettings = mockAvetmissExportSettings.call(this);

    // Audits App
    this.audit = mockAudits.call(this);

    // Accounts
    this.account = mockAccounts.call(this);

    // Common
    this.filters = mockFilters.call(this);
    this.tags = mockTags.call(this);
    this.accessApi = mockAccessApi.call(this);

    // Currency
    this.currency = mockCurrency.call(this);

    // Dashboard
    this.dashboard = mockDashboard.call(this);

    // Timezones
    this.timezones = mockTimezones.call(this);

    // Countries
    this.countries = mockCountries.call(this);

    // Entities
    this.waitingLists = mockWaitingLists.call(this);
    this.contacts = mockContacts.call(this);
    this.courses = mockCourses.call(this);
    this.sites = mockSites.call(this);
    this.rooms = mockRooms.call(this);
    this.courseClasses = mockCourseClasses.call(this);
    this.traineeships = mockTraineeships.call(this);
    this.traineeshipCourses = mockTraineeshipCourses.call(this);
    this.qualifications = mockQualifications.call(this);
    this.assessments = mockAssessments.call(this);
    this.outcomes = mockOutcomes.call(this);
    this.modules = mockModules.call(this);
    this.enrolments = mockEnrolments.call(this);
    this.articleProducts = mockArticleProducts.call(this);
    this.invoices = mockInvoices.call(this);
    this.paymentsIn = mockPaymentsIn.call(this);
    this.paymentsOut = mockPaymentsOut.call(this);
    this.corporatePasses = mockCorporatePasses.call(this);
    this.accountTransactions = mockAccountTransactions.call(this);
    this.payslips = mockPayslips.call(this);
    this.bankings = mockBankings.call(this);
    this.discounts = mockDiscounts.call(this);
    this.documents = mockDocuments.call(this);
    this.applications = mockApplications.call(this);
    this.certificates = mockCertificates.call(this);
    this.surveys = mockSurvey.call(this);
    this.messages = mockMessage.call(this);
    this.voucherProducts = mockVoucherProducts.call(this);
    this.membershipProducts = mockMembershipProducts.call(this);
    this.sales = mockSales.call(this);
    // Timetable
    this.timetable = mockTimetable.call(this);
  }

  getList(entity: string) {
    return this[entity];
  }
}
