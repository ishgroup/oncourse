import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_EXPORT_TEMPLATES_LIST,
  REMOVE_EXPORT_TEMPLATE_FULFILLED,
  removeExportTemplate
} from "../../../js/containers/automation/containers/export-templates/actions";
import { EpicRemoveExportTemplate } from "../../../js/containers/automation/containers/export-templates/epics/EpicRemoveExportTemplate";

describe("Remove export template epic tests", () => {
  it("EpicRemoveExportTemplate should returns correct values", () => DefaultEpic({
    action: removeExportTemplate(1),
    epic: EpicRemoveExportTemplate,
    processData: () => [
      {
        type: REMOVE_EXPORT_TEMPLATE_FULFILLED
      },
      {
        type: GET_EXPORT_TEMPLATES_LIST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Export template deleted" }
      }
    ]
  }));
});