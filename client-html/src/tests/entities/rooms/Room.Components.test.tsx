import RoomEditView from "../../../js/containers/entities/rooms/components/RoomEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered RoomEditView", () => {
  mockedEditView({
    entity: "Room",
    EditView: RoomEditView,
    record: mockecApi => mockecApi.db.getRoom(1),
    render: ({ screen, initialValues, formRoleName }) => {
      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        name: initialValues.name,
        seatedCapacity: initialValues.seatedCapacity.toString(),
        facilities: initialValues.facilities,
        directions: initialValues.directions,
      });
    }
  });
});
