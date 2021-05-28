import { mockedAPI } from "../../TestEntry";
import { EpicGetImportTemplatesList } from "../../../js/containers/automation/containers/import-templates/epics/EpicGetImportTemplatesList";
import {
  GET_IMPORT_TEMPLATES_LIST,
  GET_IMPORT_TEMPLATES_LIST_FULFILLED
} from "../../../js/containers/automation/containers/import-templates/actions";
import { CommonListItem } from "../../../js/model/common/sidebar";
import { DefaultEpic } from "../../common/Default.Epic";

describe("Get import templates list epic tests", () => {
  it("EpicGetImportTemplatesList should returns correct values", () =>
     DefaultEpic({
      action: {
        type: GET_IMPORT_TEMPLATES_LIST
      },
      epic: EpicGetImportTemplatesList,
      processData: () => {
        const importTemplatesResponse = mockedAPI.db.getImportTemplates();

        const importTemplates: CommonListItem[] = importTemplatesResponse.rows.map(r => ({
          id: Number(r.id),
          name: r.values[0],
          keyCode: r.values[1],
          hasIcon: r.values[1].startsWith("ish."),
          grayOut: r.values[2] === "false"
        }));

        importTemplates.sort((a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1));

        return [
          {
            type: GET_IMPORT_TEMPLATES_LIST_FULFILLED,
            payload: { importTemplates }
          }
        ];
      }
    }));
});
