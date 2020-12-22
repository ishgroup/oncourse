import { DefaultEpic } from "../../common/Default.Epic";
import {
  CREATE_IMPORT_TEMPLATE,
  CREATE_IMPORT_TEMPLATE_FULFILLED,
  GET_IMPORT_TEMPLATES_LIST
} from "../../../js/containers/automation/containers/import-templates/actions";
import { EpicCreateImportTemplate } from "../../../js/containers/automation/containers/import-templates/epics/EpicCreateImportTemplate";
import { FETCH_SUCCESS } from "../../../js/common/actions";

describe("Create import template epic tests", () => {
  it("EpicCreateImportTemplate should returns correct values", () =>
    DefaultEpic({
      action: {
        type: CREATE_IMPORT_TEMPLATE,
        payload: {
          importTemplate: {
            id: 21,
            name: "name 21",
            keyCode: "keyCode 21",
            enabled: true
          }
        }
      },
      epic: EpicCreateImportTemplate,
      processData: () => [
          {
            type: CREATE_IMPORT_TEMPLATE_FULFILLED
          },
          {
            type: GET_IMPORT_TEMPLATES_LIST,
            payload: { keyCodeToSelect: "keyCode 21" }
          },
          {
            type: FETCH_SUCCESS,
            payload: { message: "Import template created" }
          }
        ]
    }));
});
