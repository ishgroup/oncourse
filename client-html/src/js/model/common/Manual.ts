/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export type ManualType =
  | "AVETMISS"
  | "accounting"
  | "accounting_Deposit"
  | "advancedSetup_Export"
  | "advancedSetup_Import"
  | "advancedSetup_Tutor"
  | "advancedSetup_Help"
  | "applications"
  | "assessment"
  | "batchpayments_batchpayments"
  | "certification"
  | "classes"
  | "concessions_creatingMemberships"
  | "contacts"
  | "corporatePass"
  | "courses"
  | "dataCollection"
  | "delivery_outcomes"
  | "delivery_rpl"
  | "discounts"
  | "documentManagement"
  | "emailTemplates"
  | "externalintegrations"
  | "generalPrefs_avetmiss"
  | "generalPrefs_classdefaults"
  | "generalPrefs_college"
  | "generalPrefs_concessionTypes"
  | "generalPrefs_contactRelationTypes"
  | "generalPrefs_sellableItemsRelationTypes"
  | "generalPrefs_customFields"
  | "generalPrefs_financial"
  | "generalPrefs-fundingContractsPrefs"
  | "generalPrefs_holidays"
  | "generalPrefs_ldap"
  | "generalPrefs_maintenance"
  | "generalPrefs_messaging"
  | "generalPrefs_paymentTypes"
  | "generalPrefs_taxTypes"
  | "importExport"
  | "invoice"
  | "kiosk"
  | "leads"
  | "messages"
  | "payroll"
  | "processingEnrolments"
  | "processingEnrolments_PaymentIn"
  | "processingEnrolments_PaymentOut"
  | "product"
  | "reports"
  | "reports_background"
  | "rto_createModules"
  | "rto_createQual"
  | "sales"
  | "scripts"
  | "sitesRooms"
  | "sitesRooms_rooms"
  | "tagging"
  | "users"
  | "users_Users"
  | "users_roles"
  | "vouchers"
  | "waitingLists";
