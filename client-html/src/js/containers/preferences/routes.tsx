import React from "react";
import Class from "../../containers/preferences/containers/class/ClassDefaults";
import College from "../../containers/preferences/containers/college/College";
import LDAP from "../../containers/preferences/containers/ldap/LDAP";
import Licences from "../../containers/preferences/containers/licences/Licences";
import Messaging from "../../containers/preferences/containers/messaging/Messaging";
import Maintenance from "../../containers/preferences/containers/maintenance/Maintenance";
import Avetmiss from "./containers/avetmiss/Avetmiss";
import FundingContracts from "./containers/funding-contracts/FundingContracts";
import Financial from "../../containers/preferences/containers/financial/Financial";
import Holidays from "../../containers/preferences/containers/holidays/Holidays";
import CollectionForms from "./containers/data-collection-forms/CollectionFormContainer";
import CollectionRules from "./containers/data-collection-rules/CollectionRuleFormContainer";
import PaymentTypes from "./containers/payment-types/PaymentTypes";
import TaxTypes from "./containers/tax-types/TaxTypes";
import ConcessionTypes from "./containers/concession-types/ConcessionTypes";
import ContactRelationTypes from "./containers/contact-relation-types/ContactRelationTypes";
import CustomFields from "./containers/custom-fields/CustomFields";
import TutorRoleForm from "./containers/tutor-roles/TutorRoleFormContainer";
import EntityRelationTypes from "./containers/entity-relation-types/EntityRelationTypes";
import GradingForm from "./containers/grading/GradingForm";

const preferencesRoutes = [
  {
    title: "AVETMISS",
    path: "/preferences/avetmiss",
    url: "/preferences/avetmiss",
    customAppBar: true,
    main: Avetmiss
  },
  {
    title: "Class defaults",
    path: "/preferences/class",
    url: "/preferences/class",
    customAppBar: true,
    main: Class
  },
  {
    title: "College",
    path: "/preferences/college",
    url: "/preferences/college",
    customAppBar: true,
    main: College
  },
  {
    title: "Concession types",
    path: "/preferences/concessionTypes",
    url: "/preferences/concessionTypes",
    customAppBar: true,
    main: ConcessionTypes
  },
  {
    title: "Contact relation types",
    path: "/preferences/contactRelationTypes",
    url: "/preferences/contactRelationTypes",
    customAppBar: true,
    main: ContactRelationTypes
  },
  {
    title: "Custom fields",
    path: "/preferences/customFields",
    url: "/preferences/customFields",
    customAppBar: true,
    main: CustomFields
  },
  {
    title: "Financial",
    path: "/preferences/financial",
    url: "/preferences/financial",
    customAppBar: true,
    main: Financial
  },
  {
    title: "Funding contracts",
    path: "/preferences/fundingContracts",
    url: "/preferences/fundingContracts",
    customAppBar: true,
    main: FundingContracts
  },
  {
    title: "Grading types",
    path: "/preferences/gradingTypes",
    url: "/preferences/gradingTypes",
    customAppBar: true,
    main: GradingForm
  },
  {
    title: "Holidays",
    path: "/preferences/holidays",
    url: "/preferences/holidays",
    customAppBar: true,
    main: Holidays
  },
  {
    title: "LDAP",
    path: "/preferences/ldap",
    url: "/preferences/ldap",
    customAppBar: true,
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
    customAppBar: true,
    main: Maintenance
  },
  {
    title: "Messaging",
    path: "/preferences/messaging",
    url: "/preferences/messaging",
    customAppBar: true,
    main: Messaging
  },
  {
    title: "Payment types",
    path: "/preferences/paymentTypes",
    url: "/preferences/paymentTypes",
    customAppBar: true,
    main: PaymentTypes
  },
  {
    title: "Sellable items relation types",
    path: "/preferences/sellableItemsRelationTypes",
    url: "/preferences/sellableItemsRelationTypes",
    customAppBar: true,
    main: EntityRelationTypes
  },
  {
    title: "Tax types",
    path: "/preferences/taxTypes",
    url: "/preferences/taxTypes",
    customAppBar: true,
    main: TaxTypes
  },
  {
    path: "/preferences/collectionRules/:action/:id?",
    url: "/preferences/collectionRules",
    noMenuLink: true,
    customAppBar: true,
    main: CollectionRules
  },
  {
    path: "/preferences/collectionForms/:action/:type/:id?",
    url: "/preferences/collectionForms",
    noMenuLink: true,
    customAppBar: true,
    main: CollectionForms
  },
  {
    path: "/preferences/tutorRoles/:id?",
    url: "/preferences/tutorRoles",
    noMenuLink: true,
    customAppBar: true,
    main: TutorRoleForm
  }
];

export default preferencesRoutes;
