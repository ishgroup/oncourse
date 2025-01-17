import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  CREATE_EMAIL_TEMPLATE,
  GET_EMAIL_TEMPLATES_LIST
} from "../../../js/containers/automation/containers/email-templates/actions"; import {
  EMAIL_TEMPLATES_FORM_NAME
} from "../../../js/containers/automation/containers/email-templates/EmailTemplates";
import { EpicCreateEmailTemplate } from "../../../js/containers/automation/containers/email-templates/epics/EpicCreateEmailTemplate";

const emailTemplate = {
  id: 21,
  name: "name 21",
  keyCode: "keyCode 21",
  enabled: true
};

describe("Create email template epic tests", () => {
  it("EpicCreateEmailTemplate should returns correct values", () =>
    DefaultEpic({
      action: {
        type: CREATE_EMAIL_TEMPLATE,
        payload: {
          emailTemplate
        }
      },
      epic: EpicCreateEmailTemplate,
      processData: () => [
        initialize(EMAIL_TEMPLATES_FORM_NAME, emailTemplate),
        {
          type: GET_EMAIL_TEMPLATES_LIST,
          payload: { keyCodeToSelect: "keyCode 21" }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Message template created" }
        }
      ]
    }));
});
