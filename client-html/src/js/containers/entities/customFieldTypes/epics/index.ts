import { combineEpics } from "redux-observable";
import { EpicGetCustomFieldTypes } from "./EpicGetCustomFieldTypes";
import { EpicGetSalesCustomFieldsTypes } from "./EpicGetSalesCustomFieldsTypes";
import { EpicGetInvoicesCustomFieldsTypes } from "./EpicGetInvoicesCustomFieldsTypes";
import { EpicGetContactsCustomFieldsTypes } from "./EpicGetContactsCustomFieldTypes";

export const EpicCustomFieldTypes = combineEpics(EpicGetCustomFieldTypes, EpicGetSalesCustomFieldsTypes,
  EpicGetInvoicesCustomFieldsTypes, EpicGetContactsCustomFieldsTypes);
