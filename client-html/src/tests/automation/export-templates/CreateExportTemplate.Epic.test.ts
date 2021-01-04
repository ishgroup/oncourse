import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  CREATE_EXPORT_TEMPLATE_FULFILLED,
  GET_EXPORT_TEMPLATES_LIST,
  createExportTemplate
} from "../../../js/containers/automation/containers/export-templates/actions";
import { EpicCreateExportTemplate } from "../../../js/containers/automation/containers/export-templates/epics/EpicCreateExportTemplate";

describe("Create export template epic tests", () => {
  it("EpicCreateExportTemplate should returns correct values", () => DefaultEpic({
    action: createExportTemplate({
      id: 21,
      name: "name 21",
      keyCode: "keyCode 21",
      enabled: true
    }),
    epic: EpicCreateExportTemplate,
    processData: () => [
      {
        type: CREATE_EXPORT_TEMPLATE_FULFILLED
      },
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