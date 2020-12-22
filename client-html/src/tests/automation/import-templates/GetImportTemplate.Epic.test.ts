import { initialize } from "redux-form";
import { mockedAPI } from "../../TestEntry";
import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetImportTemplate } from "../../../js/containers/automation/containers/import-templates/epics/EpicGetImportTemplate";
import { IMPORT_TEMPLATES_FORM_NAME } from "../../../js/containers/automation/containers/import-templates/ImportTemplates";
import {
  GET_IMPORT_TEMPLATE,
  GET_IMPORT_TEMPLATE_FULFILLED
} from "../../../js/containers/automation/containers/import-templates/actions";

describe("Get import template epic tests", () => {
  it("EpicGetImportTemplate should returns correct values", () =>
    DefaultEpic({
      action: {
        type: GET_IMPORT_TEMPLATE,
        payload: 1
      },
      epic: EpicGetImportTemplate,
      processData: () => {
        const importTemplate = mockedAPI.db.getImportTemplate(1);
        return [
          {
            type: GET_IMPORT_TEMPLATE_FULFILLED
          },
          initialize(IMPORT_TEMPLATES_FORM_NAME, importTemplate)
        ]
      }
    }));
});
