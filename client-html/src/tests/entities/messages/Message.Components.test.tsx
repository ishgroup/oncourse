import { format } from "date-fns";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import MessageEditView from "../../../js/containers/entities/messages/components/MessageEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered MessageEditView", () => {
  mockedEditView({
    entity: "Message",
    EditView: MessageEditView,
    record: mockecApi => mockecApi.db.getMessage(1),
    render: ({ screen, initialValues }) => {
      expect(screen.getByLabelText("Subject").value).toBe(initialValues.subject);
      expect(screen.getByLabelText(/Sent to/i, { selector: 'input' }).value).toBe(initialValues.sentToContactFullname);
      expect(screen.getByLabelText("Created on").value).toBe(format(new Date(initialValues.createdOn), III_DD_MMM_YYYY));
      expect(screen.getByLabelText("Creator key").value).toBe(initialValues.creatorKey);
    }
  });
});
