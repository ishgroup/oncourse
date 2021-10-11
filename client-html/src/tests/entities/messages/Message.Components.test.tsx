import { format } from "date-fns";
import { defaultContactName } from "../../../js/containers/entities/contacts/utils";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import MessageEditView from "../../../js/containers/entities/messages/components/MessageEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered MessageEditView", () => {
  mockedEditView({
    entity: "Message",
    EditView: MessageEditView,
    record: mockecApi => mockecApi.db.getMessage(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find(".textField").text()).toContain(initialValues.subject);
      expect(wrapper.find(".textField").text()).toContain(defaultContactName(initialValues.sentToContactFullname));
      expect(wrapper.find(".textField").text()).toContain(
        format(new Date(initialValues.createdOn), III_DD_MMM_YYYY).toString()
      );
      expect(wrapper.find("code").text()).toContain(initialValues.message);
      expect(wrapper.find(".textField").text()).toContain("No Value");
      expect(wrapper.find(".textField").text()).toContain("No Value");
      expect(wrapper.find(".textField").text()).toContain(initialValues.creatorKey);
    }
  });
});
