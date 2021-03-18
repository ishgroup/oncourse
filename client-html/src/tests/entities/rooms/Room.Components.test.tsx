import RoomEditView from "../../../js/containers/entities/rooms/components/RoomEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered RoomEditView", () => {
  mockedEditView({
    entity: "Room",
    EditView: RoomEditView,
    record: mockecApi => mockecApi.db.getRoom(1),
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#name").text()).toContain(initialValues.name);
      expect(wrapper.find("#tags").text()).toContain("#name 1 ");
      expect(wrapper.find("#seatedCapacity").text()).toContain(initialValues.seatedCapacity);
      expect(wrapper.find("#siteId").text()).toContain("No value");
      expect(wrapper.find("#facilities").text()).toContain("No value");
      expect(wrapper.find("#directions").text()).toContain("No value");
    }
  });
});
