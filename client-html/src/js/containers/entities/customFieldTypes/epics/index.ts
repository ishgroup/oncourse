import { combineEpics } from "redux-observable";
import { EpicGetCustomFieldTypes } from "./EpicGetCustomFieldTypes";
import { EpicGetSalesCustomFieldsTypes } from "./EpicGetSalesCustomFieldsTypes";

export const EpicCustomFieldTypes = combineEpics(EpicGetCustomFieldTypes, EpicGetSalesCustomFieldsTypes);
