import RoomEditView from "../../../js/containers/entities/rooms/components/RoomEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered RoomEditView", () => {
  mockedEditView({
    entity: "Room",
    EditView: RoomEditView,
    record: mockecApi => mockecApi.db.getRoom(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name input").val()).toContain(initialValues.name);
      expect(wrapper.find("#seatedCapacity input").val()).toContain(initialValues.seatedCapacity);
    }
  });
});
