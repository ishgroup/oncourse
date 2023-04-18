import { combineEpics } from "redux-observable";
import { EpicGetCustomFieldTypes } from "./EpicGetCustomFieldTypes";
import { EpicGetSalesCustomFieldsTypes } from "./EpicGetSalesCustomFieldsTypes";
import { EpicGetInvoicesCustomFieldsTypes } from "./EpicGetInvoicesCustomFieldsTypes";

export const EpicCustomFieldTypes = combineEpics(EpicGetCustomFieldTypes, EpicGetSalesCustomFieldsTypes, EpicGetInvoicesCustomFieldsTypes);
