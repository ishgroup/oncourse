import * as path from "path";
import { Route } from "../../routes";
import Avetmiss from "./containers/avetmiss/Avetmiss";
import Class from "./containers/class/ClassDefaults";
import College from "./containers/college/College";
import ConcessionTypes from "./containers/concession-types/ConcessionTypes";
import ContactRelationTypes from "./containers/contact-relation-types/ContactRelationTypes";
import CustomFields from "./containers/custom-fields/CustomFields";
import CollectionForms from "./containers/data-collection-forms/CollectionFormContainer";
import CollectionRules from "./containers/data-collection-rules/CollectionRuleFormContainer";
import EntityRelationTypes from "./containers/entity-relation-types/EntityRelationTypes";
import Financial from "./containers/financial/Financial";
import FundingContracts from "./containers/funding-contracts/FundingContracts";
import GradingForm from "./containers/grading/GradingForm";
import Holidays from "./containers/holidays/Holidays";
import LDAP from "./containers/ldap/LDAP";
import Licences from "./containers/licences/Licences";
import Maintenance from "./containers/maintenance/Maintenance";
import Messaging from "./containers/messaging/Messaging";
import PaymentTypes from "./containers/payment-types/PaymentTypes";
import TaxTypes from "./containers/tax-types/TaxTypes";
import TutorRoleForm from "./containers/tutor-roles/TutorRoleFormContainer";

const preferencesRoutes: Route[] = [
  {
    title: "AVETMISS",
    path: "/preferences/avetmiss",
    url: "/preferences/avetmiss",
    main: Avetmiss
  },
  {
    title: "Class defaults",
    path: "/preferences/class",
    url: "/preferences/class",
    main: Class
  },
  {
    title: "College",
    path: "/preferences/college",
    url: "/preferences/college",
    main: College
  },
  {
    title: "Concession types",
    path: "/preferences/concessionTypes",
    url: "/preferences/concessionTypes",
    main: ConcessionTypes
  },
  {
    title: "Contact relation types",
    path: "/preferences/contactRelationTypes",
    url: "/preferences/contactRelationTypes",
    main: ContactRelationTypes
  },
  {
    title: "Custom fields",
    path: "/preferences/customFields",
    url: "/preferences/customFields",
    main: CustomFields
  },
  {
    title: "Financial",
    path: "/preferences/financial",
    url: "/preferences/financial",
    main: Financial
  },
  {
    title: "Funding contracts",
    path: "/preferences/fundingContracts",
    url: "/preferences/fundingContracts",
    main: FundingContracts
  },
  {
    title: "Grading types",
    path: "/preferences/gradingTypes",
    url: "/preferences/gradingTypes",
    main: GradingForm
  },
  {
    title: "Holidays",
    path: "/preferences/holidays",
    url: "/preferences/holidays",
    main: Holidays
  },
  {
    title: "LDAP",
    path: "/preferences/ldap",
    url: "/preferences/ldap",
    main: LDAP
  },
  {
    title: "Licences",
    path: "/preferences/licences",
    url: "/preferences/licences",
    main: Licences
  },
  {
    title: "Maintenance",
    path: "/preferences/maintenance",
    url: "/preferences/maintenance",
    main: Maintenance
  },
  {
    title: "Messaging",
    path: "/preferences/messaging",
    url: "/preferences/messaging",
    main: Messaging
  },
  {
    title: "Payment types",
    path: "/preferences/paymentTypes",
    url: "/preferences/paymentTypes",
    main: PaymentTypes
  },
  {
    title: "Sellable items relation types",
    path: "/preferences/sellableItemsRelationTypes",
    url: "/preferences/sellableItemsRelationTypes",
    main: EntityRelationTypes
  },
  {
    title: "Tax types",
    path: "/preferences/taxTypes",
    url: "/preferences/taxTypes",
    main: TaxTypes
  },
  {
    title: "Data collection rules",
    path: "/preferences/collectionRules/:action/:id?",
    url: "/preferences/collectionRules",
    noMenuLink: true,
    main: CollectionRules
  },
  {
    title: "Data collection forms",
    path: "/preferences/collectionForms/:action/:type/:id?",
    url: "/preferences/collectionForms",
    noMenuLink: true,
    main: CollectionForms
  },
  {
    title: "Tutor roles",
    path: "/preferences/tutorRoles/:id?",
    url: "/preferences/tutorRoles",
    noMenuLink: true,
    main: TutorRoleForm
  }
];

export default preferencesRoutes;
