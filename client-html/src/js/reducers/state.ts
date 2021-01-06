/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Country, Currency, Language } from "@api/model";
import { PreferencesState } from "../containers/preferences/reducers/state";
import { Fetch } from "../model/common/Fetch";
import { Drawer } from "../model/common/drawer/DrawerModel";
import { LoginState } from "../containers/login/reducers/state";
import { SecurityState } from "../containers/security/reducers/state";
import { AvetmissExportState } from "../containers/avetmiss-export/reducers/state";
import { SiteState } from "../containers/entities/sites/reducers/state";
import { GoogleApiResponse } from "../common/components/google-maps/reducers";
import { DocumentsState } from "../common/components/form/documents/reducers/state";
import { TagsState } from "../containers/tags/reducers/state";
import { PayrollsState } from "../containers/entities/payrolls/reducers/state";
import { ProcessState } from "../common/reducers/processReducer";
import { AccessState } from "../common/reducers/accessReducer";
import { DashboardState } from "../containers/dashboard/reducers/state";
import { AccountsState } from "../containers/entities/accounts/reducers/state";
import { UserPreferencesState } from "../common/reducers/userPreferencesReducer";
import { BankingState } from "../containers/entities/bankings/reducers/state";
import { CoursesState } from "../containers/entities/courses/reducers/state";
import { DiscountsState } from "../containers/entities/discounts/reducers/state";
import { SaleState } from "../containers/entities/sales/reducers/state";
import { TimetableState } from "../model/timetable";
import {
  QuickSearchConcessionTypesState,
  QuickSearchCorporatePassState
} from "../containers/entities/common/reducers/state";
import { CustomFieldTypesState } from "../containers/entities/customFieldTypes/reducers/state";
import { TaxesState } from "../containers/entities/taxes/reducers";
import { ListState } from "../model/common/ListView";
import { ContactsState } from "../containers/entities/contacts/reducers";
import { QualificationState } from "../containers/entities/qualifications/reducers";
import { CertificatesState } from "../containers/entities/certificates/reducers";
import { MembershipProductState } from "../containers/entities/membershipProducts/reducers/state";
import { VoucherProductState } from "../containers/entities/voucherProducts/reducers/state";
import { InvoicesState } from "../containers/entities/invoices/reducers/state";
import { PaymentInState } from "../containers/entities/paymentsIn/reducers/state";
import { AutomationState } from "../containers/automation/reducers";
import { PaymentOutState } from "../containers/entities/paymentsOut/reducers/state";
import { ShareState } from "../model/common/Share";
import { CourseClassState } from "../containers/entities/courseClasses/reducers";
import { CourseClassBulkSession } from "../containers/entities/courseClasses/reducers/state";
import { ModulesState } from "../containers/entities/modules/reducers/state";
import { AssessmentsState } from "../containers/entities/assessments/reducers/state";
import { ConfirmState } from "../model/common/Confirm";
import { ActionsQueueState } from "../model/common/ActionsQueue";
import { AppMessage } from "../model/common/Message";
import { ArticleProductState } from "../containers/entities/articleProducts/reducers/state";
import { PlainEntityState } from "../model/common/Plain";
import { SelectItemDefault } from "../model/entities/common";
import { CheckoutState } from "../model/checkout";
import { SwipeableDrawer } from "../model/common/drawer/SwipeableDrawerModel";
import { EnrolmentsState } from "../containers/entities/enrolments/reducers/state";
import { CommonPlainRecordSearchState } from "../common/reducers/commonPlainRecordSearchReducer";

// global app state
export interface State {
  automation: AutomationState;
  confirm: ConfirmState;
  courseClass: CourseClassState;
  courseClassesBulkSession: CourseClassBulkSession;
  preferences: PreferencesState;
  userPreferences: UserPreferencesState;
  access: AccessState;
  timezones: string[];
  countries: Country[];
  languages: Language[];
  fetch: Fetch;
  enums: any;
  form: any;
  drawer: Drawer;
  swipeableDrawer: SwipeableDrawer;
  login: LoginState;
  security: SecurityState;
  tags: TagsState;
  export: AvetmissExportState;
  process: ProcessState;
  list: ListState;
  taxes: TaxesState;
  share: ShareState;
  lastLocation: string;
  sites: SiteState;
  rooms: PlainEntityState<SelectItemDefault>;
  googleApiResponse: GoogleApiResponse;
  documents: DocumentsState;
  dashboard: DashboardState;
  payrolls: PayrollsState;
  accounts: AccountsState;
  contacts: ContactsState;
  certificates: CertificatesState;
  currency: Currency;
  usiSoftwareId: string;
  banking: BankingState;
  courses: CoursesState;
  lockedDate: Date;
  discounts: DiscountsState;
  sales: SaleState;
  quickSearchConcessionType: QuickSearchConcessionTypesState;
  quickSearchCorporatePass: QuickSearchCorporatePassState;
  qualification: QualificationState;
  timetable: TimetableState;
  customFieldTypes: CustomFieldTypesState;
  membershipProducts: MembershipProductState;
  voucherProducts: VoucherProductState;
  invoices: InvoicesState;
  paymentsIn: PaymentInState;
  paymentsOut: PaymentOutState;
  modules: ModulesState;
  assessments: AssessmentsState;
  actionsQueue: ActionsQueueState;
  message: AppMessage;
  articleProducts: ArticleProductState;
  checkout: CheckoutState;
  enrolments: EnrolmentsState;
  plainSearchRecords: CommonPlainRecordSearchState;
}
