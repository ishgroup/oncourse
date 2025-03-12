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
  PaymentMethod, Tag,
  Tax
} from "@api/model";
import { PreferenceSchema } from '../../../model/preferences/PreferencesSchema';

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
  logo?: Record<string, PreferenceSchema>;
}
