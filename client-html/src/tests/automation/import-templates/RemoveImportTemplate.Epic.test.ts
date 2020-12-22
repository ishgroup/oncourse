import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_IMPORT_TEMPLATES_LIST,
  REMOVE_IMPORT_TEMPLATE,
  REMOVE_IMPORT_TEMPLATE_FULFILLED
} from "../../../js/containers/automation/containers/import-templates/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicRemoveImportTemplate } from "../../../js/containers/automation/containers/import-templates/epics/EpicRemoveImportTemplate";

describe("Remove import template epic tests", () => {
  it("EpicRemoveImportTemplate should returns correct values", () =>
    DefaultEpic({
      action: {
        type: REMOVE_IMPORT_TEMPLATE,
        payload: 1
      },
      epic: EpicRemoveImportTemplate,
      processData: () => [
        {
          type: REMOVE_IMPORT_TEMPLATE_FULFILLED
        },
        {
          type: GET_IMPORT_TEMPLATES_LIST
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Import template deleted" }
        }
      ]
    }));
});
