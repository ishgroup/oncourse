/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineReducers, Reducer } from 'redux';
import { reducer as formReducer } from 'redux-form';
import { IAction } from '../common/actions/IshAction';
import { documentReducer } from '../common/components/form/documents/reducers';
import { googleApiReducer } from '../common/components/google-maps/reducers';
import { swipeableDrawerReducer } from '../common/components/layout/swipeable-sidebar/reducers';
import { shareReducer } from '../common/components/list-view/components/share/reducers';
import { listReducer } from '../common/components/list-view/reducers/listReducer';
import { accessReducer } from '../common/reducers/accessReducer';
import { actionsQueueReducer } from '../common/reducers/actionsQueueReducer';
import { commonPlainRecordSearchReducer } from '../common/reducers/commonPlainRecordSearchReducer';
import { confirmReducer } from '../common/reducers/confirmReducer';
import { drawerReducer } from '../common/reducers/drawerReducer';
import { fetchReducer } from '../common/reducers/fetchReducer';
import { fieldProcessingReducer } from '../common/reducers/fieldProcessingReducer';
import { lastLocationReducer } from '../common/reducers/lastLocationReducer';
import { messageReducer } from '../common/reducers/messageReducer';
import { setNextLocationReducer } from '../common/reducers/nextLocation';
import { processReducer } from '../common/reducers/processReducer';
import { sendMessageReducer } from '../common/reducers/sendMessageReducer';
import { userPreferencesReducer } from '../common/reducers/userPreferencesReducer';
import { automationReducer } from '../containers/automation/reducers';
import { AvetmissExportReducer } from '../containers/avetmiss-export/reducers';
import { checkoutReducer } from '../containers/checkout/reducers';
import { dashboardReducer } from '../containers/dashboard/reducers';
import { bankingReducer } from '../containers/entities/bankings/reducers';
import { certificatesReducer } from '../containers/entities/certificates/reducers';
import { contactsReducer } from '../containers/entities/contacts/reducers';
import { courseClassesBulkSessionReducer, courseClassReducer } from '../containers/entities/courseClasses/reducers';
import { customFieldTypesReducer } from '../containers/entities/customFieldTypes/reducers';
import { discountsReducer } from '../containers/entities/discounts/reducers';
import { enrolmentsReducer } from '../containers/entities/enrolments/reducers';
import { invoicesReducer } from '../containers/entities/invoices/reducers';
import { paymentInReducer } from '../containers/entities/paymentsIn/reducers';
import { paymentOutReducer } from '../containers/entities/paymentsOut/reducers';
import { payrollsReducer } from '../containers/entities/payrolls/reducers';
import { saleReducer } from '../containers/entities/sales/reducers';
import { siteReducer } from '../containers/entities/sites/reducers';
import { taxesReducer } from '../containers/entities/taxes/reducers';
import { voucherProductReducer } from '../containers/entities/voucherProducts/reducers';
import { loginReducer, systemUserReducer } from '../containers/login/reducers';
import {
  countriesReducer,
  locationReducer,
  enumsReducer,
  languagesReducer,
  lockedDateReducer,
  preferencesReducer,
  timezonesReducer,
  usiSoftwareIdReducer
} from '../containers/preferences/reducers';
import { securityReducer } from '../containers/security/reducers';
import { tagsReducer } from '../containers/tags/reducers';
import { timetableReducer } from '../containers/timetable/reducers';
import { State } from './state';

export const combinedReducers: Reducer<State,IAction> = combineReducers({
  access: accessReducer,
  actionsQueue: actionsQueueReducer,
  automation: automationReducer,
  banking: bankingReducer,
  certificates: certificatesReducer,
  checkout: checkoutReducer,
  confirm: confirmReducer,
  contacts: contactsReducer,
  countries: countriesReducer,
  courseClass: courseClassReducer,
  courseClassesBulkSession: courseClassesBulkSessionReducer,
  location: locationReducer,
  customFieldTypes: customFieldTypesReducer,
  dashboard: dashboardReducer,
  discounts: discountsReducer,
  documents: documentReducer,
  drawer: drawerReducer,
  enrolments: enrolmentsReducer,
  enums: enumsReducer,
  export: AvetmissExportReducer,
  fetch: fetchReducer,
  form: formReducer,
  googleApiResponse: googleApiReducer,
  invoices: invoicesReducer,
  languages: languagesReducer,
  lastLocation: lastLocationReducer,
  list: listReducer,
  lockedDate: lockedDateReducer,
  login: loginReducer,
  message: messageReducer,
  nextLocation: setNextLocationReducer,
  paymentsIn: paymentInReducer,
  paymentsOut: paymentOutReducer,
  payrolls: payrollsReducer,
  plainSearchRecords: commonPlainRecordSearchReducer,
  preferences: preferencesReducer,
  process: processReducer,
  fieldProcessing: fieldProcessingReducer,
  sales: saleReducer,
  security: securityReducer,
  share: shareReducer,
  sites: siteReducer,
  swipeableDrawer: swipeableDrawerReducer,
  tags: tagsReducer,
  taxes: taxesReducer,
  timetable: timetableReducer,
  timezones: timezonesReducer,
  userPreferences: userPreferencesReducer,
  usiSoftwareId: usiSoftwareIdReducer,
  voucherProducts: voucherProductReducer,
  systemUser: systemUserReducer,
  sendMessage: sendMessageReducer
});
