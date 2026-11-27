import {
  ColumnWidth,
  ConcessionType,
  ContactRelationType,
  CustomFieldType,
  DataCollectionForm,
  DataCollectionRule,
  DefinedTutorRole,
  EntityRelationType,
  FieldType,
  FundingSource,
  GradingType,
  Logo,
  PaymentMethod,
  Tag,
  Tax
} from '@api/model';

export interface TutorRolesLoad {
  roles?: DefinedTutorRole[];
  error?: boolean;
}

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
  // Tutor roles loaded with custom columns, by columns, so loads with other columns do not replace or interrupt them
  tutorRolesByColumns?: { [columns: string]: TutorRolesLoad };
  gradingTypes?: GradingType[];
  enums?: any;
  college?: any;
  licences?: any;
  messaging?: any;
  classDefaults?: any;
  classTypes?: Tag[];
  courseTypes?: Tag[];
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
  plugins?: string;
  logo?: Logo;
}
