import { initialize } from "redux-form";
import { mockedAPI } from "../../TestEntry";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_EMAIL_TEMPLATE,
  GET_EMAIL_TEMPLATE_FULFILLED
} from "../../../js/containers/automation/containers/email-templates/actions";
import { EpicGetEmailTemplate } from "../../../js/containers/automation/containers/email-templates/epics/EpicGetEmailTemplate";
import { EMAIL_TEMPLATES_FORM_NAME } from "../../../js/containers/automation/containers/email-templates/EmailTemplates";

describe("Get email template epic tests", () => {
  it("EpicGetEmailTemplate should returns correct values", () =>
    DefaultEpic({
      action: {
        type: GET_EMAIL_TEMPLATE,
        payload: 1
      },
      epic: EpicGetEmailTemplate,
      processData: () => {
        const emailTemplate = mockedAPI.db.getEmailTemplate(1);
        return [
          {
            type: GET_EMAIL_TEMPLATE_FULFILLED
          },
          initialize(EMAIL_TEMPLATES_FORM_NAME, emailTemplate)
        ];
      }
    }));
});
