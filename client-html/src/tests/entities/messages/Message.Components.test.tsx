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
      const inputs = wrapper.find("input");
      expect(inputs[0].attribs.value).toContain(initialValues.subject);
      expect(inputs[1].attribs.value).toContain(defaultContactName(initialValues.sentToContactFullname));
      expect(inputs[2].attribs.value).toContain(
        format(new Date(initialValues.createdOn), III_DD_MMM_YYYY).toString()
      );
      expect(wrapper.find("code").text()).toContain(initialValues.message);
      expect(inputs[5].attribs.value).toContain(initialValues.creatorKey);
    }
  });
});
