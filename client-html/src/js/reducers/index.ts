/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineReducers } from "redux";
import { reducer as formReducer } from "redux-form";
import {
  preferencesReducer,
  timezonesReducer,
  enumsReducer,
  countriesReducer,
  languagesReducer,
  currencyReducer,
  usiSoftwareIdReducer,
  lockedDateReducer
} from "../containers/preferences/reducers";
import { drawerReducer } from "../common/reducers/drawerReducer";
import { loginReducer } from "../containers/login/reducers";
import { securityReducer } from "../containers/security/reducers";
import { tagsReducer } from "../containers/tags/reducers";
import { AvetmissExportReducer } from "../containers/avetmiss-export/reducers";
import { processReducer } from "../common/reducers/processReducer";
import { listReducer } from "../common/components/list-view/reducers/listReducer";
import { lastLocationReducer } from "../common/reducers/lastLocationReducer";
import { setNextLocationReducer } from "../common/reducers/nextLocation";
import { fetchReducer } from "../common/reducers/fetchReducer";
import { shareReducer } from "../common/components/list-view/components/share/reducers";
import { siteReducer } from "../containers/entities/sites/reducers";
import { googleApiReducer } from "../common/components/google-maps/reducers";
import { documentReducer } from "../common/components/form/documents/reducers";
import { dashboardReducer } from "../containers/dashboard/reducers";
import { payrollsReducer } from "../containers/entities/payrolls/reducers";
import { accessReducer } from "../common/reducers/accessReducer";
import { accountEntityReducer } from "../containers/entities/accounts/reducers";
import { contactsReducer } from "../containers/entities/contacts/reducers";
import { userPreferencesReducer } from "../common/reducers/userPreferencesReducer";
import { bankingReducer } from "../containers/entities/bankings/reducers";
import { coursesReducer } from "../containers/entities/courses/reducers";
import { discountsReducer } from "../containers/entities/discounts/reducers";
import { saleReducer } from "../containers/entities/sales/reducers";
import { timetableReducer } from "../containers/timetable/reducers";
import {
  quickSearchConcessionTypeReducer,
  quickSearchCorporatePassReducer
} from "../containers/entities/common/reducers";
import { customFieldTypesReducer } from "../containers/entities/customFieldTypes/reducers";
import { taxesReducer } from "../containers/entities/taxes/reducers";
import { certificatesReducer } from "../containers/entities/certificates/reducers";
import { membershipProductReducer } from "../containers/entities/membershipProducts/reducers";
import { voucherProductReducer } from "../containers/entities/voucherProducts/reducers";
import { invoicesReducer } from "../containers/entities/invoices/reducers";
import { paymentInReducer } from "../containers/entities/paymentsIn/reducers";
import { automationReducer } from "../containers/automation/reducers";
import { confirmReducer } from "../common/reducers/confirmReducer";
import { paymentOutReducer } from "../containers/entities/paymentsOut/reducers";
import {
  courseClassesBulkSessionReducer,
  courseClassReducer
} from "../containers/entities/courseClasses/reducers";
import { modulesReducer } from "../containers/entities/modules/reducers";
import { assessmentsReducer } from "../containers/entities/assessments/reducers";
import { actionsQueueReducer } from "../common/reducers/actionsQueueReducer";
import { messageReducer } from "../common/reducers/messageReducer";
import { articleProductsReducer } from "../containers/entities/articleProducts/reducers";
import { checkoutReducer } from "../containers/checkout/reducers";
import { swipeableDrawerReducer } from "../common/components/layout/swipeable-sidebar/reducers";
import { enrolmentsReducer } from "../containers/entities/enrolments/reducers";
import { commonPlainRecordSearchReducer } from "../common/reducers/commonPlainRecordSearchReducer";

export const combinedReducers = combineReducers({
  automation: automationReducer,
  confirm: confirmReducer,
  courseClass: courseClassReducer,
  courseClassesBulkSession: courseClassesBulkSessionReducer,
  preferences: preferencesReducer,
  userPreferences: userPreferencesReducer,
  access: accessReducer,
  timezones: timezonesReducer,
  countries: countriesReducer,
  languages: languagesReducer,
  fetch: fetchReducer,
  enums: enumsReducer,
  drawer: drawerReducer,
  swipeableDrawer: swipeableDrawerReducer,
  form: formReducer,
  login: loginReducer,
  security: securityReducer,
  tags: tagsReducer,
  export: AvetmissExportReducer,
  process: processReducer,
  list: listReducer,
  share: shareReducer,
  lastLocation: lastLocationReducer,
  nextLocation: setNextLocationReducer,
  sites: siteReducer,
  googleApiResponse: googleApiReducer,
  documents: documentReducer,
  dashboard: dashboardReducer,
  payrolls: payrollsReducer,
  accounts: accountEntityReducer,
  contacts: contactsReducer,
  certificates: certificatesReducer,
  taxes: taxesReducer,
  currency: currencyReducer,
  usiSoftwareId: usiSoftwareIdReducer,
  banking: bankingReducer,
  lockedDate: lockedDateReducer,
  courses: coursesReducer,
  discounts: discountsReducer,
  sales: saleReducer,
  timetable: timetableReducer,
  quickSearchConcessionType: quickSearchConcessionTypeReducer,
  quickSearchCorporatePass: quickSearchCorporatePassReducer,
  customFieldTypes: customFieldTypesReducer,
  membershipProducts: membershipProductReducer,
  voucherProducts: voucherProductReducer,
  invoices: invoicesReducer,
  paymentsIn: paymentInReducer,
  paymentsOut: paymentOutReducer,
  modules: modulesReducer,
  assessments: assessmentsReducer,
  actionsQueue: actionsQueueReducer,
  message: messageReducer,
  articleProducts: articleProductsReducer,
  checkout: checkoutReducer,
  enrolments: enrolmentsReducer,
  plainSearchRecords: commonPlainRecordSearchReducer
});
