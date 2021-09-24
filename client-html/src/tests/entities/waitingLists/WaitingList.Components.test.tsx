import WaitingListEditView from "../../../js/containers/entities/waitingLists/components/WaitingListEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered WaitingListEditView", () => {
  mockedEditView({
    entity: "WaitingList",
    EditView: WaitingListEditView,
    record: mockecApi => mockecApi.db.getWaitingList(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#contactId input").val()).toContain(initialValues.studentName);
      expect(wrapper.find("#studentCount input").val()).toContain(initialValues.studentCount);
      expect(wrapper.find("#courseId input").val()).toContain(initialValues.courseName);
      expect(wrapper.find("#studentNotes input").val()).toContain(initialValues.studentNotes);
      expect(wrapper.find("#privateNotes input").val()).toContain(initialValues.privateNotes);
    }
  });
});
