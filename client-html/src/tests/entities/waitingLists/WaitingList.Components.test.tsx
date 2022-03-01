import WaitingListEditView from "../../../js/containers/entities/waitingLists/components/WaitingListEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered WaitingListEditView", () => {
  mockedEditView({
    entity: "WaitingList",
    EditView: WaitingListEditView,
    record: mockecApi => mockecApi.db.getWaitingList(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        contactId: initialValues.studentName,
        studentCount: initialValues.studentCount,
        courseId: initialValues.courseName,
        studentNotes: initialValues.studentNotes,
        privateNotes: initialValues.privateNotes,
      });
    }
  });
});
