import { mockedAPI } from "../../TestEntry";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_EMAIL_TEMPLATES_LIST,
  GET_EMAIL_TEMPLATES_LIST_FULFILLED
} from "../../../js/containers/automation/containers/email-templates/actions";
import { EpicGetEmailTemplatesList } from "../../../js/containers/automation/containers/email-templates/epics/EpicGetEmailTemplatesList";
import { CatalogItemType } from "../../../js/model/common/Catalog";
import { mapListToCatalogItem } from "../../../js/common/utils/Catalog";

describe("Get email templates list epic tests", () => {
  it("EpicGetEmailTemplatesList should returns correct values", () =>
    DefaultEpic({
      action: {
        type: GET_EMAIL_TEMPLATES_LIST
      },
      epic: EpicGetEmailTemplatesList,
      processData: () => {
        const emailTemplatesResponse = mockedAPI.db.getEmailTemplates();

        const emailTemplates: CatalogItemType[] = emailTemplatesResponse.rows.map(mapListToCatalogItem);

        emailTemplates.sort((a, b) => (a.title.toLowerCase() > b.title.toLowerCase() ? 1 : -1));

        return [
          {
            type: GET_EMAIL_TEMPLATES_LIST_FULFILLED,
            payload: { emailTemplates }
          }
        ];
      }
    }));
});
