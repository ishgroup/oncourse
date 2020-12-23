import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_IMPORT_TEMPLATE,
  GET_IMPORT_TEMPLATES_LIST,
  UPDATE_IMPORT_TEMPLATE,
  UPDATE_IMPORT_TEMPLATE_FULFILLED
} from "../../../js/containers/automation/containers/import-templates/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicUpdateImportTemplate } from "../../../js/containers/automation/containers/import-templates/epics/EpicUpdateImportTemplate";

describe("Update import template epic tests", () => {
  it("EpicUpdateImportTemplate should returns correct values", () =>
    DefaultEpic({
      action: {
        type: UPDATE_IMPORT_TEMPLATE,
        payload: {
          importTemplate: {
            id: 1,
            name: "name 1",
            keyCode: "keyCode 1",
            enabled: false
          }
        }
      },
      epic: EpicUpdateImportTemplate,
      processData: () => [
        {
          type: UPDATE_IMPORT_TEMPLATE_FULFILLED
        },
        {
          type: GET_IMPORT_TEMPLATE,
          payload: 1
        },
        {
          type: GET_IMPORT_TEMPLATES_LIST
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Import template updated" }
        }
      ]
    }));
});
