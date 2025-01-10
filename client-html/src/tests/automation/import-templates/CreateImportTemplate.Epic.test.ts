import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  CREATE_IMPORT_TEMPLATE,
  GET_IMPORT_TEMPLATES_LIST
} from "../../../js/containers/automation/containers/import-templates/actions";
import {
  IMPORT_TEMPLATES_FORM_NAME
} from "../../../js/containers/automation/containers/import-templates/ImportTemplates";
import { EpicCreateImportTemplate } from "../../../js/containers/automation/containers/import-templates/epics/EpicCreateImportTemplate";
import { FETCH_SUCCESS } from "../../../js/common/actions";

const importTemplate = {
  id: 21,
  name: "name 21",
  keyCode: "keyCode 21",
  enabled: true
};

describe("Create import template epic tests", () => {
  it("EpicCreateImportTemplate should returns correct values", () =>
    DefaultEpic({
      action: {
        type: CREATE_IMPORT_TEMPLATE,
        payload: {
          importTemplate
        }
      },
      epic: EpicCreateImportTemplate,
      processData: () => [
        initialize(IMPORT_TEMPLATES_FORM_NAME, importTemplate),
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
