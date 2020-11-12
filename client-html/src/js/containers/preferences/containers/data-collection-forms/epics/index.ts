import { combineEpics } from "redux-observable";
import { EpicGetDataCollectionForms } from "./EpicGetDataCollectionForms";
import { EpicGetDataCollectionFormFieldTypes } from "./EpicGetDataCollectionFormFieldTypes";
import { EpicUpdateDataCollectionForm } from "./EpicUpdateDataCollectionForm";
import { EpicCreateDataCollectionForm } from "./EpicCreateDataCollectionForm";
import { EpicDeleteDataCollectionForm } from "./EpicDeleteDataCollectionForm";

export const EpicDataCollectionForms = combineEpics(
  EpicGetDataCollectionForms,
  EpicGetDataCollectionFormFieldTypes,
  EpicUpdateDataCollectionForm,
  EpicCreateDataCollectionForm,
  EpicDeleteDataCollectionForm
);
