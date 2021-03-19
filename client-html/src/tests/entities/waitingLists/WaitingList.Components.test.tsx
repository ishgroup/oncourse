import WaitingListEditView from "../../../js/containers/entities/waitingLists/components/WaitingListEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered WaitingListEditView", () => {
  mockedEditView({
    entity: "WaitingList",
    EditView: WaitingListEditView,
    record: mockecApi => mockecApi.db.getWaitingList(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#contactId").text()).toContain(initialValues.studentName);
      expect(wrapper.find("#tags").text()).toContain("#name 1 ");

      expect(wrapper.find("#studentCount").text()).toContain(initialValues.studentCount);
      expect(wrapper.find("#courseId").text()).toContain(initialValues.courseName);
      expect(wrapper.find("#studentNotes").text()).toContain(initialValues.studentNotes);
      expect(wrapper.find("#privateNotes").text()).toContain(initialValues.privateNotes);
    }
  });
});
