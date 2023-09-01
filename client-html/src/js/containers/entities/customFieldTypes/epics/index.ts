import { combineEpics } from "redux-observable";
import { EpicGetContactsCustomFieldsTypes } from "./EpicGetContactsCustomFieldTypes";
import { EpicGetCustomFieldTypes } from "./EpicGetCustomFieldTypes";
import { EpicGetInvoicesCustomFieldsTypes } from "./EpicGetInvoicesCustomFieldsTypes";
import { EpicGetSalesCustomFieldsTypes } from "./EpicGetSalesCustomFieldsTypes";

export const EpicCustomFieldTypes = combineEpics(EpicGetCustomFieldTypes, EpicGetSalesCustomFieldsTypes,
  EpicGetInvoicesCustomFieldsTypes, EpicGetContactsCustomFieldsTypes);
