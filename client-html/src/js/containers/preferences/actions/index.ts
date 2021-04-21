import {
  DataCollectionForm,
  DataCollectionRule,
  PaymentMethod,
  Tax,
  ConcessionType,
  ContactRelationType,
  EntityRelationType,
  CustomFieldType,
  EnumName,
  ColumnWidth,
  Holiday,
  DefinedTutorRole, GradingType
} from "@api/model";
import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";
import { Categories } from "../../../model/preferences";

export const GET_DATA_COLLECTION_FORM_FIELD_TYPES_REQUEST = _toRequestType("get/datacollection/formFieldTypes");
export const GET_DATA_COLLECTION_FORM_FIELD_TYPES_FULFILLED = FULFILLED(GET_DATA_COLLECTION_FORM_FIELD_TYPES_REQUEST);

export const GET_DATA_COLLECTION_FORMS_REQUEST = _toRequestType("get/datacollection/form");
export const GET_DATA_COLLECTION_FORMS_FULFILLED = FULFILLED(GET_DATA_COLLECTION_FORMS_REQUEST);

export const UPDATE_DATA_COLLECTION_FORM_REQUEST = _toRequestType("update/datacollection/form");
export const UPDATE_DATA_COLLECTION_FORM_FULFILLED = FULFILLED(UPDATE_DATA_COLLECTION_FORM_REQUEST);

export const CREATE_DATA_COLLECTION_FORM_REQUEST = _toRequestType("create/datacollection/form");
export const CREATE_DATA_COLLECTION_FORM_FULFILLED = FULFILLED(CREATE_DATA_COLLECTION_FORM_REQUEST);

export const DELETE_DATA_COLLECTION_FORM_REQUEST = _toRequestType("delete/datacollection/form");
export const DELETE_DATA_COLLECTION_FORM_FULFILLED = FULFILLED(DELETE_DATA_COLLECTION_FORM_REQUEST);

export const GET_DATA_COLLECTION_RULES_REQUEST = _toRequestType("get/datacollection/rule");
export const GET_DATA_COLLECTION_RULES_FULFILLED = FULFILLED(GET_DATA_COLLECTION_RULES_REQUEST);

export const CREATE_DATA_COLLECTION_RULE_REQUEST = _toRequestType("post/datacollection/rule");
export const CREATE_DATA_COLLECTION_RULE_FULFILLED = FULFILLED(CREATE_DATA_COLLECTION_RULE_REQUEST);

export const DELETE_DATA_COLLECTION_RULE_REQUEST = _toRequestType("delete/datacollection/rule");
export const DELETE_DATA_COLLECTION_RULE_FULFILLED = FULFILLED(CREATE_DATA_COLLECTION_RULE_REQUEST);

export const UPDATE_DATA_COLLECTION_RULE_REQUEST = _toRequestType("put/datacollection/rule");
export const UPDATE_DATA_COLLECTION_RULE_FULFILLED = FULFILLED(UPDATE_DATA_COLLECTION_RULE_REQUEST);

export const GET_PREFERENCES_REQUEST = _toRequestType("get/preferences");
export const GET_PREFERENCES_FULFILLED = FULFILLED(GET_PREFERENCES_REQUEST);

export const GET_PREFERENCES_BY_KEYS_REQUEST = _toRequestType("get/preferencesByKeys");
export const GET_PREFERENCES_BY_KEYS_FULFILLED = FULFILLED(GET_PREFERENCES_BY_KEYS_REQUEST);

export const GET_COMPLEX_PASS_REQUEST = _toRequestType("get/complexPass");
export const GET_COMPLEX_PASS_FILLED = FULFILLED(GET_COMPLEX_PASS_REQUEST);

export const GET_IS_LOGGED_REQUEST = _toRequestType("get/isLoggedIn");
export const GET_IS_LOGGED_FULFILLED = FULFILLED(GET_IS_LOGGED_REQUEST);

export const SAVE_PREFERENCES_REQUEST = _toRequestType("save/preferences");
export const SAVE_PREFERENCES_FULFILLED = FULFILLED(SAVE_PREFERENCES_REQUEST);

export const GET_TIMEZONES_REQUEST = _toRequestType("get/timezones");
export const GET_TIMEZONES_FULFILLED = FULFILLED(GET_TIMEZONES_REQUEST);

export const GET_COUNTRIES_REQUEST = _toRequestType("get/countries");
export const GET_COUNTRIES_REQUEST_FULFILLED = FULFILLED(GET_COUNTRIES_REQUEST);

export const GET_LANGUAGES_REQUEST = _toRequestType("get/languages");
export const GET_LANGUAGES_REQUEST_FULFILLED = FULFILLED(GET_LANGUAGES_REQUEST);

export const GET_HOLIDAYS_REQUEST = _toRequestType("get/holidays");
export const GET_HOLIDAYS_FULFILLED = FULFILLED(GET_HOLIDAYS_REQUEST);

export const SAVE_HOLIDAYS_REQUEST = _toRequestType("save/holidays");
export const SAVE_HOLIDAYS_FULFILLED = FULFILLED(SAVE_HOLIDAYS_REQUEST);

export const DELETE_HOLIDAYS_ITEM_REQUEST = _toRequestType("delete/holidays");
export const DELETE_HOLIDAYS_ITEM_FULFILLED = FULFILLED(DELETE_HOLIDAYS_ITEM_REQUEST);

export const GET_PAYMENT_TYPES_REQUEST = _toRequestType("get/payment/type");
export const GET_PAYMENT_TYPES_FULFILLED = FULFILLED(GET_PAYMENT_TYPES_REQUEST);

export const DELETE_PAYMENT_TYPE_REQUEST = _toRequestType("delete/payment/type");
export const DELETE_PAYMENT_TYPE_FULFILLED = FULFILLED(DELETE_PAYMENT_TYPE_REQUEST);

export const UPDATE_PAYMENT_TYPES_REQUEST = _toRequestType("post/payment/type");
export const UPDATE_PAYMENT_TYPES_FULFILLED = FULFILLED(UPDATE_PAYMENT_TYPES_REQUEST);

export const GET_TAX_TYPES_REQUEST = _toRequestType("get/tax");
export const GET_TAX_TYPES_FULFILLED = FULFILLED(GET_TAX_TYPES_REQUEST);

export const DELETE_TAX_TYPE_REQUEST = _toRequestType("delete/tax");
export const DELETE_TAX_TYPE_FULFILLED = FULFILLED(DELETE_TAX_TYPE_REQUEST);

export const UPDATE_TAX_TYPES_REQUEST = _toRequestType("post/tax");
export const UPDATE_TAX_TYPES_FULFILLED = FULFILLED(UPDATE_TAX_TYPES_REQUEST);

export const GET_CONCESSION_TYPES_REQUEST = _toRequestType("get/concession/type");
export const GET_CONCESSION_TYPES_FULFILLED = FULFILLED(GET_CONCESSION_TYPES_REQUEST);

export const DELETE_CONCESSION_TYPE_REQUEST = _toRequestType("delete/concession/type");
export const DELETE_CONCESSION_TYPE_FULFILLED = FULFILLED(DELETE_CONCESSION_TYPE_REQUEST);

export const UPDATE_CONCESSION_TYPES_REQUEST = _toRequestType("post/concession/type");
export const UPDATE_CONCESSION_TYPES_FULFILLED = FULFILLED(UPDATE_CONCESSION_TYPES_REQUEST);

export const GET_CONTACT_RELATION_TYPES_REQUEST = _toRequestType("get/contact/relation/type");
export const GET_CONTACT_RELATION_TYPES_FULFILLED = FULFILLED(GET_CONTACT_RELATION_TYPES_REQUEST);

export const DELETE_CONTACT_RELATION_TYPE_REQUEST = _toRequestType("delete/contact/relation/type");
export const DELETE_CONTACT_RELATION_TYPE_FULFILLED = FULFILLED(DELETE_CONTACT_RELATION_TYPE_REQUEST);

export const UPDATE_CONTACT_RELATION_TYPES_REQUEST = _toRequestType("post/contact/relation/type");
export const UPDATE_CONTACT_RELATION_TYPES_FULFILLED = FULFILLED(UPDATE_CONTACT_RELATION_TYPES_REQUEST);

export const GET_ENTITY_RELATION_TYPES_REQUEST = _toRequestType("get/entity/relation/type");
export const GET_ENTITY_RELATION_TYPES_FULFILLED = FULFILLED(GET_ENTITY_RELATION_TYPES_REQUEST);

export const DELETE_ENTITY_RELATION_TYPE_REQUEST = _toRequestType("delete/entity/relation/type");
export const DELETE_ENTITY_RELATION_TYPE_FULFILLED = FULFILLED(DELETE_ENTITY_RELATION_TYPE_REQUEST);

export const UPDATE_ENTITY_RELATION_TYPES_REQUEST = _toRequestType("post/entity/relation/type");
export const UPDATE_ENTITY_RELATION_TYPES_FULFILLED = FULFILLED(UPDATE_ENTITY_RELATION_TYPES_REQUEST);

export const GET_CUSTOM_FIELDS_REQUEST = _toRequestType("get/preference/field/type");
export const GET_CUSTOM_FIELDS_FULFILLED = FULFILLED(GET_CUSTOM_FIELDS_REQUEST);

export const DELETE_CUSTOM_FIELD_REQUEST = _toRequestType("delete/preference/field/type");

export const UPDATE_CUSTOM_FIELDS_REQUEST = _toRequestType("post/preference/field/type");
export const UPDATE_CUSTOM_FIELDS_FULFILLED = FULFILLED(UPDATE_CUSTOM_FIELDS_REQUEST);

export const GET_ENUM_REQUEST = _toRequestType("get/preference/enum");
export const GET_ENUM_FULFILLED = FULFILLED(GET_ENUM_REQUEST);

export const GET_COLUMNS_WIDTH_REQUEST = _toRequestType("get/preference/settings");
export const GET_COLUMNS_WIDTH_REQUEST_FULFILLED = FULFILLED(GET_COLUMNS_WIDTH_REQUEST);

export const UPDATE_COLUMNS_WIDTH_REQUEST = _toRequestType("post/preference/settings");
export const UPDATE_COLUMNS_WIDTH_REQUEST_FULFILLED = FULFILLED(UPDATE_COLUMNS_WIDTH_REQUEST);

export const GET_CURRENCY = _toRequestType("get/currency");
export const GET_CURRENCY_FULFILLED = FULFILLED(GET_CURRENCY);

export const GET_USI_SORTWARE_ID = _toRequestType("get/USISoftwareId");
export const GET_USI_SORTWARE_ID_FULFILLED = FULFILLED(GET_USI_SORTWARE_ID);

export const GET_ACCOUNT_TRANSACTION_LOCKED_DATE = _toRequestType("get/preference/lockedDate");
export const GET_ACCOUNT_TRANSACTION_LOCKED_DATE_FULFILLED = FULFILLED(GET_ACCOUNT_TRANSACTION_LOCKED_DATE);

export const GET_TUTOR_ROLES_REQUEST = _toRequestType("get/tutor/roles");
export const GET_TUTOR_ROLES_FULFILLED = FULFILLED(GET_TUTOR_ROLES_REQUEST);

export const GET_TUTOR_ROLE_REQUEST = _toRequestType("get/tutor/role");
export const GET_TUTOR_ROLE_FULFILLED = FULFILLED(GET_TUTOR_ROLE_REQUEST);

export const CREATE_TUTOR_ROLE_REQUEST = _toRequestType("post/tutor/role");
export const CREATE_TUTOR_ROLE_FULFILLED = FULFILLED(CREATE_TUTOR_ROLE_REQUEST);

export const UPDATE_TUTOR_ROLE_REQUEST = _toRequestType("put/tutor/role");
export const UPDATE_TUTOR_ROLE_FULFILLED = FULFILLED(UPDATE_TUTOR_ROLE_REQUEST);

export const DELETE_TUTOR_ROLE_REQUEST = _toRequestType("delete/tutor/role");
export const DELETE_TUTOR_ROLE_FULFILLED = FULFILLED(DELETE_TUTOR_ROLE_REQUEST);

export const GET_GRADING_TYPES_REQUEST = _toRequestType("get/grading/type");
export const GET_GRADING_TYPES_FULFILLED = FULFILLED(GET_GRADING_TYPES_REQUEST);

export const UPDATE_GRADING_TYPES_REQUEST = _toRequestType("post/grading/type");

export const getGradingTypes = () => ({
  type: GET_GRADING_TYPES_REQUEST
});

export const updateGradingTypes = (types: GradingType[]) => ({
  type: UPDATE_GRADING_TYPES_REQUEST,
  payload: types
});

export const getCurrency = () => ({
  type: GET_CURRENCY
});

export const getUSISoftwareId = () => ({
  type: GET_USI_SORTWARE_ID
});

export const getEnum = (name: EnumName) => ({
  type: GET_ENUM_REQUEST,
  payload: { name }
});

export const updateCustomFields = (customFields: CustomFieldType[]) => ({
  type: UPDATE_CUSTOM_FIELDS_REQUEST,
  payload: { customFields }
});

export const deleteCustomField = (id: string) => ({
  type: DELETE_CUSTOM_FIELD_REQUEST,
  payload: { id }
});

export const getCustomFields = () => ({
  type: GET_CUSTOM_FIELDS_REQUEST
});

export const updateContactRelationTypes = (contactRelationTypes: ContactRelationType[]) => ({
  type: UPDATE_CONTACT_RELATION_TYPES_REQUEST,
  payload: { contactRelationTypes }
});

export const deleteContactRelationType = (id: string) => ({
  type: DELETE_CONTACT_RELATION_TYPE_REQUEST,
  payload: { id }
});

export const getContactRelationTypes = () => ({
  type: GET_CONTACT_RELATION_TYPES_REQUEST
});

export const updateEntityRelationTypes = (entityRelationTypes: EntityRelationType[]) => ({
  type: UPDATE_ENTITY_RELATION_TYPES_REQUEST,
  payload: { entityRelationTypes }
});

export const deleteEntityRelationType = (id: string) => ({
  type: DELETE_ENTITY_RELATION_TYPE_REQUEST,
  payload: { id }
});

export const getEntityRelationTypes = () => ({
  type: GET_ENTITY_RELATION_TYPES_REQUEST
});

export const updateConcessionTypes = (concessionTypes: ConcessionType[]) => ({
  type: UPDATE_CONCESSION_TYPES_REQUEST,
  payload: { concessionTypes }
});

export const deleteConcessionType = (id: string) => ({
  type: DELETE_CONCESSION_TYPE_REQUEST,
  payload: { id }
});

export const getConcessionTypes = () => ({
  type: GET_CONCESSION_TYPES_REQUEST
});

export const updateTaxTypes = (taxTypes: Tax[]) => ({
  type: UPDATE_TAX_TYPES_REQUEST,
  payload: { taxTypes }
});

export const deleteTaxType = (id: string) => ({
  type: DELETE_TAX_TYPE_REQUEST,
  payload: { id }
});

export const getTaxTypes = () => ({
  type: GET_TAX_TYPES_REQUEST
});

export const updatePaymentTypes = (paymentTypes: PaymentMethod[]) => ({
  type: UPDATE_PAYMENT_TYPES_REQUEST,
  payload: { paymentTypes }
});

export const deletePaymentType = (id: string) => ({
  type: DELETE_PAYMENT_TYPE_REQUEST,
  payload: { id }
});

export const getPaymentTypes = () => ({
  type: GET_PAYMENT_TYPES_REQUEST
});

export const updateDataCollectionRule = (id: string, rule: DataCollectionRule) => ({
  type: UPDATE_DATA_COLLECTION_RULE_REQUEST,
  payload: { id, rule }
});

export const removeDataCollectionRule = (id: string) => ({
  type: DELETE_DATA_COLLECTION_RULE_REQUEST,
  payload: { id }
});

export const createDataCollectionRule = (rule: DataCollectionRule) => ({
  type: CREATE_DATA_COLLECTION_RULE_REQUEST,
  payload: { rule }
});

export const getDataCollectionRules = () => ({
  type: GET_DATA_COLLECTION_RULES_REQUEST
});

export const deleteDataCollectionForm = (id: string) => ({
  type: DELETE_DATA_COLLECTION_FORM_REQUEST,
  payload: { id }
});

export const createDataCollectionForm = (form: DataCollectionForm) => ({
  type: CREATE_DATA_COLLECTION_FORM_REQUEST,
  payload: { form }
});

export const updateDataCollectionForm = (id: string, form: DataCollectionForm) => ({
  type: UPDATE_DATA_COLLECTION_FORM_REQUEST,
  payload: { id, form }
});

export const getDataCollectionFormFieldTypes = (formType: string) => ({
  type: GET_DATA_COLLECTION_FORM_FIELD_TYPES_REQUEST,
  payload: { formType }
});

export const getDataCollectionForms = () => ({
  type: GET_DATA_COLLECTION_FORMS_REQUEST
});

export const getPreferences = (category: Categories) => ({
  type: GET_PREFERENCES_REQUEST,
  payload: category
});

export const getPreferencesByKeys = (keys: string[], category: Categories) => ({
  type: GET_PREFERENCES_BY_KEYS_REQUEST,
  payload: { keys, category }
});

export const isComplexPassRequired = () => ({
  type: GET_COMPLEX_PASS_REQUEST
});

export const isLoggedIn = () => ({
  type: GET_IS_LOGGED_REQUEST
});

export const savePreferences = (category: Categories, fields) => ({
  type: SAVE_PREFERENCES_REQUEST,
  payload: { category, fields }
});

export const getTimezones = () => ({
  type: GET_TIMEZONES_REQUEST
});

export const getCountries = () => ({
  type: GET_COUNTRIES_REQUEST
});

export const getLanguages = () => ({
  type: GET_LANGUAGES_REQUEST
});

export const getColumnsWidth = () => ({
  type: GET_COLUMNS_WIDTH_REQUEST
});

export const updateColumnsWidth = (columnWidth: ColumnWidth) => ({
  type: UPDATE_COLUMNS_WIDTH_REQUEST,
  payload: columnWidth
});

export const getHolidays = () => ({
  type: GET_HOLIDAYS_REQUEST
});

export const saveHolidays = (items: Holiday[]) => ({
  type: SAVE_HOLIDAYS_REQUEST,
  payload: { items }
});

export const deleteHolidaysItem = id => ({
  type: DELETE_HOLIDAYS_ITEM_REQUEST,
  payload: { id }
});

export const getAccountTransactionLockedDate = () => ({
  type: GET_ACCOUNT_TRANSACTION_LOCKED_DATE
});

export const getTutorRoles = (selectFirst?: boolean, keyCodeToSelect?: string, columns?: string) => ({
  type: GET_TUTOR_ROLES_REQUEST,
  payload: { selectFirst, keyCodeToSelect, columns }
});

export const getTutorRole = (id: string) => ({
  type: GET_TUTOR_ROLE_REQUEST,
  payload: { id }
});

export const createTutorRole = (tutorRole: DefinedTutorRole) => ({
  type: CREATE_TUTOR_ROLE_REQUEST,
  payload: { tutorRole }
});

export const updateTutorRole = (tutorRole: DefinedTutorRole) => ({
  type: UPDATE_TUTOR_ROLE_REQUEST,
  payload: { tutorRole }
});

export const removeTutorRole = (id: string, tutorRoles: DefinedTutorRole[]) => ({
  type: DELETE_TUTOR_ROLE_REQUEST,
  payload: { id, tutorRoles }
});
