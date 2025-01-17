import { combineEpics } from "redux-observable";
import { EpicCreateDataCollectionForm } from "./EpicCreateDataCollectionForm";
import { EpicDeleteDataCollectionForm } from "./EpicDeleteDataCollectionForm";
import { EpicGetDataCollectionFormFieldTypes } from "./EpicGetDataCollectionFormFieldTypes";
import { EpicGetDataCollectionForms } from "./EpicGetDataCollectionForms";
import { EpicUpdateDataCollectionForm } from "./EpicUpdateDataCollectionForm";

export const EpicDataCollectionForms = combineEpics(
  EpicGetDataCollectionForms,
  EpicGetDataCollectionFormFieldTypes,
  EpicUpdateDataCollectionForm,
  EpicCreateDataCollectionForm,
  EpicDeleteDataCollectionForm
);
