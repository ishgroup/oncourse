/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicCommon } from "./common/epics";
import { EpicPreferences } from "./containers/preferences/epics";
import { EpicAudits } from "./containers/audits/epics";
import { EpicLogin } from "./containers/login/epics";
import { EpicScripts } from "./containers/automation/containers/scripts/epics";
import { EpicSecurity } from "./containers/security/epics";
import { EpicTags } from "./containers/tags/epics";
import { EpicAvetmissExport } from "./containers/avetmiss-export/epics";
import { EpicQualification } from "./containers/entities/qualifications/epics";
import { EpicModule } from "./containers/entities/modules/epics";
import { EpicShare } from "./common/components/list-view/components/share/epics";
import { EpicRoom } from "./containers/entities/rooms/epics";
import { EpicSite } from "./containers/entities/sites/epics";
import { EpicList } from "./common/components/list-view/epics";
import { EpicGoogleMaps } from "./common/components/google-maps/epics";
import { EpicDocuments } from "./common/components/form/documents/epics";
import { EpicInvoice } from "./containers/entities/invoices/epics";
import { EpicPaymentIn } from "./containers/entities/paymentsIn/epics";
import { EpicPaymentOut } from "./containers/entities/paymentsOut/epics";
import { EpicTransaction } from "./containers/entities/transactions/epics";
import { EpicPayslip } from "./containers/entities/payslips/epics";
import { EpicPayrolls } from "./containers/entities/payrolls/epics";
import { EpicAccounts } from "./containers/entities/accounts/epics";
import { EpicDashboard } from "./containers/dashboard/epics";
import { EpicGoogleAnalytics } from "./common/components/google-tag-manager/epics";
import { EpicCorporatePasses } from "./containers/entities/corporatePasses/epics";
import { EpicBankings } from "./containers/entities/bankings/epics";
import { EpicContacts } from "./containers/entities/contacts/epics";
import { EpicCourses } from "./containers/entities/courses/epics";
import { EpicCourseClass } from "./containers/entities/courseClasses/epics";
import { EpicDiscounts } from "./containers/entities/discounts/epics";
import { EpicEditDocument } from "./containers/entities/documents/epics";
import { EpicSales } from "./containers/entities/sales/epics";
import { EpicWaitingList } from "./containers/entities/waitingLists/epics";
import { EpicTimetable } from "./containers/timetable/epics";
import { EpicApplication } from "./containers/entities/applications/epics";
import { EpicArticleProduct } from "./containers/entities/articleProducts/epics";
import { EpicVoucherProduct } from "./containers/entities/voucherProducts/epics";
import { EpicMembershipProduct } from "./containers/entities/membershipProducts/epics";
import { EpicCertificate } from "./containers/entities/certificates/epics";
import { EpicStudentFeedback } from "./containers/entities/survey/epics";
import { EpicCustomFieldTypes } from "./containers/entities/customFieldTypes/epics";
import { EpicTaxes } from "./containers/entities/taxes/epics";
import { EpicOutcome } from "./containers/entities/outcomes/epics";
import { EpicAssessment } from "./containers/entities/assessments/epics";
import { EpicEnrolment } from "./containers/entities/enrolments/epics";
import { EpicMessage } from "./containers/entities/messages/epics";
import { EpicAutomation } from "./containers/automation/epics";
import { EpicFanalise } from "./containers/finalise-period/epics";
import { EpicPdfReports } from "./containers/automation/containers/pdf-reports/epics";
import { EpicPdfBackgrounds } from "./containers/automation/containers/pdf-backgrounds/epics";
import { EpicNotes } from "./common/components/form/notes/epics";
import { EpicPriorLearning } from "./containers/entities/priorLearnings/epics";
import { EpicCheckout } from "./containers/checkout/epics";

// Creating ES6 Set Object to guarantee unique value of each import
const importSet = new Set([
  EpicPdfBackgrounds,
  EpicPdfReports,
  EpicFanalise,
  EpicAutomation,
  EpicTimetable,
  EpicNotes,
  EpicShare,
  EpicCommon,
  EpicPreferences,
  EpicAudits,
  EpicLogin,
  EpicScripts,
  EpicSecurity,
  EpicTags,
  EpicAvetmissExport,
  EpicQualification,
  EpicModule,
  EpicRoom,
  EpicSite,
  EpicList,
  EpicGoogleMaps,
  EpicGoogleAnalytics,
  EpicDocuments,
  EpicDashboard,
  EpicInvoice,
  EpicPaymentIn,
  EpicPaymentOut,
  EpicTransaction,
  EpicPayslip,
  EpicTaxes,
  EpicPayrolls,
  EpicAccounts,
  EpicCorporatePasses,
  EpicBankings,
  EpicContacts,
  EpicCourses,
  EpicCourseClass,
  EpicDiscounts,
  EpicEditDocument,
  EpicSales,
  EpicWaitingList,
  EpicApplication,
  EpicArticleProduct,
  EpicVoucherProduct,
  EpicMembershipProduct,
  EpicCertificate,
  EpicStudentFeedback,
  EpicCustomFieldTypes,
  EpicOutcome,
  EpicAssessment,
  EpicEnrolment,
  EpicPriorLearning,
  EpicMessage,
  EpicCheckout
]);

const importArray = [];

importSet.forEach(entry => importArray.push(entry));

export const EpicRoot = combineEpics(...importArray);
