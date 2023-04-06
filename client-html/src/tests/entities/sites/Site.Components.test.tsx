import SiteEditView from "../../../js/containers/entities/sites/components/SiteEditView";
import { mockedEditView } from "../../common/MockedEditView.Components";

describe("Virtual rendered SiteEditView", () => {
  mockedEditView({
    entity: "Site",
    EditView: SiteEditView,
    record: mockecApi => mockecApi.db.getSite(1),
    render: ({
      screen, initialValues, formRoleName, fireEvent
    }) => {
      const rooms = {};

      initialValues.rooms.forEach((room, index) => {
        fireEvent.click(screen.getByTestId(`minified-rooms[${index}]`));

        rooms[`rooms[${index}].name`] = room.name;
        rooms[`rooms[${index}].seatedCapacity`] = room.seatedCapacity;
      });

      setTimeout(() => {
        expect(screen.getByRole(formRoleName)).toHaveFormValues({
          name: initialValues.name,
          isAdministrationCentre: initialValues.isAdministrationCentre,
          isVirtual: initialValues.isVirtual,
          isShownOnWeb: initialValues.isShownOnWeb,
          timezone: initialValues.timezone,
          street: initialValues.street,
          suburb: initialValues.suburb,
          state: initialValues.state,
          postcode: initialValues.postcode,
          drivingDirections: initialValues.drivingDirections,
          publicTransportDirections: initialValues.publicTransportDirections,
          specialInstructions: initialValues.specialInstructions,
          ...rooms
        });
      }, 500);
    }
  });
});
