import { mockedAPI } from "../../TestEntry";
import { CommonListItem } from "../../../js/model/common/sidebar";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_EMAIL_TEMPLATES_LIST,
  GET_EMAIL_TEMPLATES_LIST_FULFILLED
} from "../../../js/containers/automation/containers/email-templates/actions";
import { EpicGetEmailTemplatesList } from "../../../js/containers/automation/containers/email-templates/epics/EpicGetEmailTemplatesList";

describe("Get email templates list epic tests", () => {
  it("EpicGetEmailTemplatesList should returns correct values", () =>
    DefaultEpic({
      action: {
        type: GET_EMAIL_TEMPLATES_LIST
      },
      epic: EpicGetEmailTemplatesList,
      processData: () => {
        const emailTemplatesResponse = mockedAPI.db.getEmailTemplates();

        const emailTemplates: CommonListItem[] = emailTemplatesResponse.rows.map(r => ({
          id: Number(r.id),
          name: r.values[0],
          keyCode: r.values[1],
          hasIcon: r.values[1].startsWith("ish."),
          grayOut: r.values[2] === "false"
        }));

        emailTemplates.sort((a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1));

        return [
          {
            type: GET_EMAIL_TEMPLATES_LIST_FULFILLED,
            payload: { emailTemplates }
          }
        ];
      }
    }));
});
