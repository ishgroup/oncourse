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

import { Category } from "@api/model";
import BatchPayment from "../containers/batch-payment/BatchPayment";
import PreferencesApp from "../containers/preferences/index";
import AuditsApp from "../containers/audits/index";
import LoginApp from "../containers/login/index";
import Common from "../containers/Common";
import preferencesRoutes from "../containers/preferences/routes";
import SecurityApp from "../containers/security/index";
import securityRoutes from "../containers/security/routes";
import TagsApp from "../containers/tags/index";
import AvetmissExportApp from "../containers/avetmiss-export/index";
import QualificationsApp from "../containers/entities/qualifications/index";
import ModulesApp from "../containers/entities/modules/index";
import SitesApp from "../containers/entities/sites/index";
import RoomsApp from "../containers/entities/rooms/index";
import InvoicesApp from "../containers/entities/invoices/index";
import PaymentsInApp from "../containers/entities/paymentsIn/index";
import PaymentsOutApp from "../containers/entities/paymentsOut/index";
import CorporatePassesApp from "../containers/entities/corporatePasses/index";
import TransactionsApp from "../containers/entities/transactions/index";
import PayslipsApp from "../containers/entities/payslips/index";
import AccountsApp from "../containers/entities/accounts/index";
import BankingApp from "../containers/entities/bankings/index";
import Dashboard from "../containers/dashboard/index";
import WaitingListsApp from "../containers/entities/waitingLists/index";
import ApplicationApp from "../containers/entities/applications/index";
import ArticleProductApp from "../containers/entities/articleProducts/index";
import VoucherProductApp from "../containers/entities/voucherProducts/index";
import MembershipProductApp from "../containers/entities/membershipProducts/index";
import CourseApp from "../containers/entities/courses/index";
import CourseClassApp from "../containers/entities/courseClasses";
import CertificateApp from "../containers/entities/certificates/index";
import Survey from "../containers/entities/survey/index";
import SalesApp from "../containers/entities/sales/index";
import Timetable from "../containers/timetable/index";
import DiscountApp from "../containers/entities/discounts/index";
import DocumentsApp from "../containers/entities/documents/index";
import OutcomeApp from "../containers/entities/outcomes/index";
import AssessmentApp from "../containers/entities/assessments/index";
import EnrolmentApp from "../containers/entities/enrolments/index";
import PriorLearningApp from "../containers/entities/priorLearnings/index";
import MessageApp from "../containers/entities/messages/index";
import FinaliseApp from "../containers/finalise-period/index";
import ContactsApp from "../containers/entities/contacts";
import { EnvironmentConstants } from "../constants/EnvironmentConstants";
import MergeContacts from "../containers/entities/contacts/components/merge-contacts/MergeContacts";
import Automation from "../containers/automation/Automation";
import { DuplicateCourseClassSwingWrapper } from "../containers/entities/courseClasses/components/duplicate-courseClass/DuplicateCourseClassSwingWrapper";
import BulkTagView from "../containers/tags/containers/BulkTagView";
import QuickEnrol from "../containers/checkout/Checkout";
import NotFound from "../containers/notFound/NotFound";

type RouteGroupTypes = "Preferences" | "Training" | "Finance" | "Common" | "Activity" | "Products";

type SystemCategories =
  | "Dashboard"
  | "Menu"
  | "Login"
  | "Timetable"
  | "Merge Contacts"
  | "Automation"
  | "Batch Payment"
  | "Duplicate course classes"
  | "Not found";

export interface Route<T> {
  title?: T; // route title, displayed on sidebar
  path?: string; // route path (regexp)
  url?: string; // route link
  exact?: boolean;
  isPublic?: boolean;
  noMenuLink?: boolean; // prevent links menu item rendering
  icon?: string; // icon class for menu item in slim mode
  main: any; // main component for route
  routes?: Route<string>[]; // sub routes
  group?: RouteGroupTypes;
}

export type MainRoute = Route<Category | SystemCategories>;

export interface RouteGroup {
  title: RouteGroupTypes;
  routes: MainRoute[];
}

export const loginRoute: MainRoute = {
  title: "Login",
  path: "/login/:condition?/:subcondition?",
  url: "/login",
  main: LoginApp,
  group: "Common"
};

export const routes: MainRoute[] = [
  // Activity
  {
    title: "Applications",
    path: "/application/:id?",
    url: "/application",
    main: ApplicationApp,
    group: "Activity"
  },
  {
    title: "Certificates",
    path: "/certificate/:id?",
    url: "/certificate",
    main: CertificateApp,
    group: "Activity"
  },
  {
    title: "Student Feedback",
    path: "/survey/:id?",
    url: "/survey",
    main: Survey,
    group: "Activity"
  },
  {
    title: "Outcomes",
    path: "/outcome/:id?",
    url: "/outcome",
    main: OutcomeApp,
    group: "Activity"
  },
  {
    title: "Assessment tasks",
    path: "/assessment/:id?",
    url: "/assessment",
    main: AssessmentApp,
    group: "Activity"
  },
  {
    title: "Enrolments",
    path: "/enrolment/:id?",
    url: "/enrolment",
    main: EnrolmentApp,
    group: "Activity"
  },
  {
    title: "Prior learnings",
    path: "/priorLearning/:id?",
    url: "/priorLearning",
    main: PriorLearningApp,
    group: "Activity"
  },
  {
    title: "Messages",
    path: "/message/:id?",
    url: "/message",
    main: MessageApp,
    group: "Activity"
  },
  // Preferences
  {
    title: "Preferences",
    path: "/preferences",
    url: "/preferences/college",
    main: PreferencesApp,
    routes: preferencesRoutes,
    group: "Preferences"
  },
  {
    title: "Data collection forms",
    path: "/preferences/collectionForms",
    url: "/preferences/collectionForms",
    main: PreferencesApp,
    routes: preferencesRoutes,
    group: "Preferences"
  },
  {
    title: "Data collection rules",
    path: "/preferences/collectionRules",
    url: "/preferences/collectionRules",
    main: PreferencesApp,
    routes: preferencesRoutes,
    group: "Preferences"
  },
  {
    title: "Tutor pay rates",
    path: "/preferences/tutorRoles",
    url: "/preferences/tutorRoles",
    main: PreferencesApp,
    routes: preferencesRoutes,
    group: "Preferences"
  },
  {
    title: "Holidays",
    path: "/preferences/holidays",
    url: "/preferences/holidays",
    main: PreferencesApp,
    routes: preferencesRoutes,
    group: "Preferences"
  },
  {
    title: "Payment types",
    path: "/preferences/paymentTypes",
    url: "/preferences/paymentTypes",
    main: PreferencesApp,
    routes: preferencesRoutes,
    group: "Preferences"
  },
  {
    title: "Tax types",
    path: "/preferences/taxTypes",
    url: "/preferences/taxTypes",
    main: PreferencesApp,
    routes: preferencesRoutes,
    group: "Preferences"
  },
  {
    title: "Concession types",
    path: "/preferences/concessionTypes",
    url: "/preferences/concessionTypes",
    main: PreferencesApp,
    routes: preferencesRoutes,
    group: "Preferences"
  },
  {
    title: "Contact relation types",
    path: "/preferences/contactRelationTypes",
    url: "/preferences/contactRelationTypes",
    main: PreferencesApp,
    routes: preferencesRoutes,
    group: "Preferences"
  },
  {
    title: "Sellable items relation types",
    path: "/preferences/sellableItemsRelationTypes",
    url: "/preferences/sellableItemsRelationTypes",
    main: PreferencesApp,
    routes: preferencesRoutes,
    group: "Preferences"
  },
  {
    title: "Custom fields",
    path: "/preferences/customFields",
    url: "/preferences/customFields",
    main: PreferencesApp,
    routes: preferencesRoutes,
    group: "Preferences"
  },
  {
    title: "Funding Contract",
    path: "/preferences/fundingContracts",
    url: "/preferences/fundingContracts",
    main: PreferencesApp,
    routes: preferencesRoutes,
    group: "Preferences"
  },
  {
    title: "Audit Logging",
    path: "/audit/:id?",
    url: "/audit",
    main: AuditsApp,
    group: "Preferences"
  },
  {
    title: "Security",
    path: "/security",
    url: "/security/settings",
    main: SecurityApp,
    routes: securityRoutes,
    group: "Preferences"
  },
  {
    title: "Tags",
    path: "/tags/:id?",
    url: "/tags",
    noMenuLink: true,
    main: TagsApp,
    group: "Preferences"
  },
  {
    title: "Export AVETMISS 8...",
    path: "/avetmiss-export",
    url: "/avetmiss-export",
    main: AvetmissExportApp,
    group: "Preferences"
  },
  // Training
  {
    title: "Qualifications",
    path: "/qualification/:id?",
    url: "/qualification",
    main: QualificationsApp,
    group: "Training"
  },
  {
    title: "Units Of Competency",
    path: "/module/:id?",
    url: "/module",
    main: ModulesApp,
    group: "Training"
  },
  {
    title: "Sites",
    path: "/site/:id?",
    url: "/site",
    main: SitesApp,
    group: "Training"
  },
  {
    title: "Rooms",
    path: "/room/:id?",
    url: "/room",
    main: RoomsApp,
    group: "Training"
  },
  {
    title: "Waiting lists",
    path: "/waitingList/:id?",
    url: "/waitingList",
    main: WaitingListsApp,
    group: "Training"
  },
  {
    title: "Documents",
    path: "/document/:id?",
    url: "/document",
    main: DocumentsApp,
    group: "Training"
  },
  {
    title: "Merge Contacts",
    path: "/mergeContacts",
    url: "/mergeContacts",
    main: MergeContacts,
    group: "Training"
  },
  // Finance
  {
    title: "Batch payment in",
    exact: true,
    path: "/batchPayment",
    url: "/batchPayment",
    main: BatchPayment
  },
  {
    title: "Invoices",
    path: "/invoice/:id?",
    url: "/invoice",
    main: InvoicesApp,
    group: "Finance"
  },
  {
    title: "Payments In",
    path: "/paymentIn/:id?",
    url: "/paymentIn",
    main: PaymentsInApp,
    group: "Finance"
  },
  {
    title: "Payments Out",
    path: "/paymentOut/:id?",
    url: "/paymentOut",
    main: PaymentsOutApp,
    group: "Finance"
  },
  {
    title: "Corporate Pass",
    path: "/corporatePass/:id?",
    url: "/corporatePass",
    main: CorporatePassesApp,
    group: "Finance"
  },
  {
    title: "Transactions",
    path: "/transaction/:id?",
    url: "/transaction",
    main: TransactionsApp,
    group: "Finance"
  },
  {
    title: "Tutor pay",
    path: "/payslip/:id?",
    url: "/payslip",
    main: PayslipsApp,
    group: "Finance"
  },
  {
    title: "Accounts",
    path: "/account/:id?",
    url: "/account",
    main: AccountsApp,
    group: "Finance"
  },
  {
    title: "Banking Deposits",
    path: "/banking/:id?",
    url: "/banking",
    main: BankingApp,
    group: "Finance"
  },
  {
    title: "Discounts",
    path: "/discount/:id?",
    url: "/discount",
    main: DiscountApp,
    group: "Finance"
  },
  {
    title: "Finalise period",
    path: "/finalise",
    url: "/finalise",
    main: FinaliseApp,
    group: "Finance"
  },
  // Common
  {
    title: "Dashboard",
    path: "/",
    url: "/",
    exact: true,
    main: Dashboard,
    group: "Common"
  },
  {
    title: "Timetable",
    path: "/timetable",
    url: "/timetable",
    main: Timetable,
    group: "Common"
  },
  {
    title: "Login",
    path: "/invite/:token",
    url: "/invite",
    main: LoginApp,
    group: "Common"
  },
  // Products
  {
    title: "Products",
    path: "/product/:id?",
    url: "/product",
    main: ArticleProductApp,
    group: "Products"
  },
  {
    title: "Courses",
    path: "/course/:id?",
    url: "/course",
    main: CourseApp,
    group: "Products"
  },
  {
    title: "Voucher Types",
    path: "/voucher/:id?",
    url: "/voucher",
    main: VoucherProductApp,
    group: "Products"
  },
  {
    title: "Memberships",
    path: "/membership/:id?",
    url: "/membership",
    main: MembershipProductApp,
    group: "Products"
  },
  {
    title: "Sales",
    path: "/sale/:id?",
    url: "/sale",
    main: SalesApp,
    group: "Products"
  },
  {
    title: "Automation",
    path: "/automation",
    url: "/automation",
    main: Automation,
    group: "Preferences"
  },
  {
    title: "Import templates",
    path: "/automation/import-templates",
    url: "/automation/import-templates",
    main: Automation,
    group: "Preferences"
  },
  {
    title: "Export templates",
    path: "/automation/export-templates",
    url: "/automation/export-templates",
    main: Automation,
    group: "Preferences"
  },
  {
    title: "Message templates",
    path: "/automation/email-templates",
    url: "/automation/email-templates",
    main: Automation,
    group: "Preferences"
  },
  {
    title: "PDF backgrounds",
    path: "/automation/pdf-backgrounds",
    url: "/automation/pdf-backgrounds",
    main: Automation,
    group: "Preferences"
  },
  {
    title: "PDF reports",
    path: "/automation/pdf-reports",
    url: "/automation/pdf-reports",
    main: Automation,
    group: "Preferences"
  },
  {
    title: "Integrations",
    path: "/automation/integrations",
    url: "/automation/integrations",
    main: Automation,
    group: "Preferences"
  },
  {
    title: "Scripts",
    path: "/automation/script",
    url: "/automation/script",
    main: Automation,
    group: "Preferences"
  },
  {
    title: "Classes",
    path: "/class/:id?",
    url: "/class",
    main: CourseClassApp,
    group: "Products"
  },
  {
    title: "Duplicate course classes",
    path: "/duplicateCourseClasses",
    url: "/duplicateCourseClasses",
    main: DuplicateCourseClassSwingWrapper,
    group: "Products"
  },
  {
    title: "Contacts",
    path: "/contact/:id?",
    url: "/contact",
    main: ContactsApp,
    group: "Activity"
  },
  {
    title: "Tags",
    path: "/tagTreeView",
    url: "/tagTreeView",
    noMenuLink: true,
    main: BulkTagView,
    group: "Preferences"
  },
  {
    title: "Checkout (Quick Enrol)",
    path: "/checkout/:id?",
    url: "/checkout",
    main: QuickEnrol,
    group: "Activity"
  },
  {
    title: "Not found",
    main: NotFound,
    group: "Common"
  },
  loginRoute
];

if (process.env.NODE_ENV === EnvironmentConstants.development) {
  routes.push({
    title: "Menu",
    exact: true,
    path: "/menu",
    url: "/menu",
    main: Common
  });
}

export const routeGroups: RouteGroup[] = [
  {
    title: "Preferences",
    routes: routes.filter(r => r.group === "Preferences")
  },
  {
    title: "Training",
    routes: routes.filter(r => r.group === "Training")
  },
  {
    title: "Finance",
    routes: routes.filter(r => r.group === "Finance")
  },
  {
    title: "Common",
    routes: routes.filter(r => r.group === "Common")
  },
  {
    title: "Activity",
    routes: routes.filter(r => r.group === "Activity")
  },
  {
    title: "Products",
    routes: routes.filter(r => r.group === "Products")
  }
];
