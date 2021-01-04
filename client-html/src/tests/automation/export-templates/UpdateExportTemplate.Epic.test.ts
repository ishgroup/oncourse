import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_EXPORT_TEMPLATE,
  GET_EXPORT_TEMPLATES_LIST,
  UPDATE_EXPORT_TEMPLATE_FULFILLED,
  updateExportTemplate
} from "../../../js/containers/automation/containers/export-templates/actions";
import { EpicUpdateExportTemplate } from "../../../js/containers/automation/containers/export-templates/epics/EpicUpdateExportTemplate";

describe("Update export template epic tests", () => {
  it("EpicUpdateExportTemplate should returns correct values", () => DefaultEpic({
    action: updateExportTemplate({
      id: 1,
      name: "name 1",
      keyCode: "keyCode 1",
      enabled: false
    }),
    epic: EpicUpdateExportTemplate,
    processData: () => [
      {
        type: UPDATE_EXPORT_TEMPLATE_FULFILLED
      },
      {
        type: GET_EXPORT_TEMPLATE,
        payload: 1
      },
      {
        type: GET_EXPORT_TEMPLATES_LIST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Export template updated" }
      }
    ]
  }));
});