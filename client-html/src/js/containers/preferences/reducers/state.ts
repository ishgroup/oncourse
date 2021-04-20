import {
  DataCollectionForm,
  DataCollectionRule,
  FieldType,
  PaymentMethod,
  Tax,
  ConcessionType,
  ContactRelationType,
  CustomFieldType,
  ColumnWidth,
  FundingSource,
  DefinedTutorRole,
  EntityRelationType
} from "@api/model";

export interface PreferencesState {
  contact?: any;
  customFields?: CustomFieldType[];
  contactRelationTypes?: ContactRelationType[];
  entityRelationTypes?: EntityRelationType[];
  concessionTypes?: ConcessionType[];
  taxTypes?: Tax[];
  fundingContracts?: FundingSource[];
  dataCollectionFormFieldTypes?: FieldType[];
  dataCollectionForms?: DataCollectionForm[];
  dataCollectionRules?: DataCollectionRule[];
  paymentTypes?: PaymentMethod[];
  tutorRoles?: DefinedTutorRole[];
  enums?: any;
  college?: any;
  licences?: any;
  messaging?: any;
  classDefaults?: any;
  ldap?: any;
  maintenance?: any;
  avetmiss?: any;
  financial?: any;
  holidays?: any;
  ldapTestResponse?: string;
  sms?: any;
  email?: any;
  security?: any;
  complexPass?: boolean;
  isLogged?: boolean;
  columnWidth?: ColumnWidth;
  product?: any;
}
