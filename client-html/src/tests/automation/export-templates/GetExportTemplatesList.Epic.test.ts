import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_EXPORT_TEMPLATES_LIST_FULFILLED,
  getExportTemplatesList
} from "../../../js/containers/automation/containers/export-templates/actions";
import { EpicGetExportTemplatesList } from "../../../js/containers/automation/containers/export-templates/epics/EpicGetExportTemplatesList";
import { CatalogItemType } from "../../../js/model/common/Catalog";
import { mapListToCatalogItem } from "../../../js/common/utils/Catalog";

describe("Get export templates list epic tests", () => {
  it("EpicGetExportTemplatesList should returns correct values", () => DefaultEpic({
    action: getExportTemplatesList(),
    epic: EpicGetExportTemplatesList,
    processData: mockedAPI => {
      const exportTemplatesResponse = mockedAPI.db.getExportTemplates();

      const exportTemplates: CatalogItemType[] = exportTemplatesResponse.rows.map(mapListToCatalogItem);

      exportTemplates.sort((a, b) => (a.title.toLowerCase() > b.title.toLowerCase() ? 1 : -1));

      return [
        {
          type: GET_EXPORT_TEMPLATES_LIST_FULFILLED,
          payload: { exportTemplates }
        }
      ];
    }
  }));
});
