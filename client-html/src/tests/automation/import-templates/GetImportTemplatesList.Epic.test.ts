import { mockedAPI } from "../../TestEntry";
import { EpicGetImportTemplatesList } from "../../../js/containers/automation/containers/import-templates/epics/EpicGetImportTemplatesList";
import {
  GET_IMPORT_TEMPLATES_LIST,
  GET_IMPORT_TEMPLATES_LIST_FULFILLED
} from "../../../js/containers/automation/containers/import-templates/actions";
import { DefaultEpic } from "../../common/Default.Epic";
import { CatalogItemType } from "../../../js/model/common/Catalog";
import { mapListToCatalogItem } from "../../../js/common/utils/Catalog";

describe("Get import templates list epic tests", () => {
  it("EpicGetImportTemplatesList should returns correct values", () =>
     DefaultEpic({
      action: {
        type: GET_IMPORT_TEMPLATES_LIST
      },
      epic: EpicGetImportTemplatesList,
      processData: () => {
        const importTemplatesResponse = mockedAPI.db.getImportTemplates();

        const importTemplates: CatalogItemType[] = importTemplatesResponse.rows.map(mapListToCatalogItem);

        importTemplates.sort((a, b) => (a.title.toLowerCase() > b.title.toLowerCase() ? 1 : -1));

        return [
          {
            type: GET_IMPORT_TEMPLATES_LIST_FULFILLED,
            payload: { importTemplates }
          }
        ];
      }
    }));
});
