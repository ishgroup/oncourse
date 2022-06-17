import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_EXPORT_TEMPLATES_LIST,
  createExportTemplate
} from "../../../js/containers/automation/containers/export-templates/actions";
import {
  EXPORT_TEMPLATES_FORM_NAME
} from "../../../js/containers/automation/containers/export-templates/ExportTemplates";
import { EpicCreateExportTemplate } from "../../../js/containers/automation/containers/export-templates/epics/EpicCreateExportTemplate";

const exportTemplate = {
  id: 21,
  name: "name 21",
  keyCode: "keyCode 21",
  status: "Enabled",
};

describe("Create export template epic tests", () => {
  it("EpicCreateExportTemplate should returns correct values", () => DefaultEpic({
    action: createExportTemplate(exportTemplate as any),
    epic: EpicCreateExportTemplate,
    processData: () => [
      initialize(EXPORT_TEMPLATES_FORM_NAME, exportTemplate),
      {
        type: GET_EXPORT_TEMPLATES_LIST,
        payload: { keyCodeToSelect: "keyCode 21" }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Export template created" }
      }
    ]
  }));
});