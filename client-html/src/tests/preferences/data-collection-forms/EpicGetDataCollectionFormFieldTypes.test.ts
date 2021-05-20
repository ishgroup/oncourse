import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_DATA_COLLECTION_FORM_FIELD_TYPES_FULFILLED,
  getDataCollectionFormFieldTypes
} from "../../../js/containers/preferences/actions";
import {
  EpicGetDataCollectionFormFieldTypes
} from "../../../js/containers/preferences/containers/data-collection-forms/epics/EpicGetDataCollectionFormFieldTypes";

const fieldType = "Enrolment";

describe("Get data collection form field types epic tests", () => {
  it("EpicGetDataCollectionFormFieldTypes should returns correct values", () => DefaultEpic({
    action: getDataCollectionFormFieldTypes(fieldType),
    epic: EpicGetDataCollectionFormFieldTypes,
    processData: mockedApi => {
      const dataCollectionFormFieldTypes = mockedApi.db.getFieldTypes(fieldType);

      return [
        {
          type: GET_DATA_COLLECTION_FORM_FIELD_TYPES_FULFILLED,
          payload: { dataCollectionFormFieldTypes }
        }
      ];
    }
  }));
});
