import { initialize } from "redux-form";
import { mockedAPI } from "../../TestEntry";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_EXPORT_TEMPLATE,
  GET_EXPORT_TEMPLATE_FULFILLED
} from "../../../js/containers/automation/containers/export-templates/actions";
import {EpicGetExportTemplate} from "../../../js/containers/automation/containers/export-templates/epics/EpicGetExportTemplate";
import {EXPORT_TEMPLATES_FORM_NAME} from "../../../js/containers/automation/containers/export-templates/ExportTemplates";

describe("Get export template epic tests", () => {
  it("EpicGetExportTemplate should returns correct values", () =>
    DefaultEpic({
      action: {
        type: GET_EXPORT_TEMPLATE,
        payload: 1
      },
      epic: EpicGetExportTemplate,
      processData: () => {
        const exportTemplate = mockedAPI.db.getExportTemplate(1);
        return [
          {
            type: GET_EXPORT_TEMPLATE_FULFILLED
          },
          initialize(EXPORT_TEMPLATES_FORM_NAME, exportTemplate)
        ]
      }
    }));
});
