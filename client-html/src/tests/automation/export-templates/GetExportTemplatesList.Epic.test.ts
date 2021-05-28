import { CommonListItem } from "../../../js/model/common/sidebar";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_EXPORT_TEMPLATES_LIST_FULFILLED,
  getExportTemplatesList
} from "../../../js/containers/automation/containers/export-templates/actions";
import { EpicGetExportTemplatesList } from "../../../js/containers/automation/containers/export-templates/epics/EpicGetExportTemplatesList";

describe("Get export templates list epic tests", () => {
  it("EpicGetExportTemplatesList should returns correct values", () => DefaultEpic({
    action: getExportTemplatesList(),
    epic: EpicGetExportTemplatesList,
    processData: mockedAPI => {
      const exportTemplatesResponse = mockedAPI.db.getExportTemplates();

      const exportTemplates: CommonListItem[] = exportTemplatesResponse.rows.map(r => ({
        id: Number(r.id),
        name: r.values[0],
        keyCode: r.values[1],
        hasIcon: r.values[1].startsWith("ish."),
        grayOut: r.values[2] === "false"
      }));

      exportTemplates.sort((a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1));

      return [
        {
          type: GET_EXPORT_TEMPLATES_LIST_FULFILLED,
          payload: { exportTemplates }
        }
      ];
    }
  }));
});
