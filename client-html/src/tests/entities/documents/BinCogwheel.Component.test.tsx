import * as React from "react";
import BinCogwheel from "../../../js/containers/entities/documents/components/BinCogwheel";
import { defaultComponents } from "../../common/Default.Components";

describe("Virtual rendered BinCogwheel of Document list view", () => {
  defaultComponents({
    entity: "BinCogwheel",
    View: props => <div><BinCogwheel {...props} /></div>,
    record: mockedApi => mockedApi.db.getDocumentsForBinCogweel(),
    defaultProps: ({ initialValues }) => ({
      opened: true,
      selection: ["1", "2", "3"],
      records: initialValues,
      closeMenu: jest.fn(),
    }),
    render: ({ screen, fireEvent }) => {
      expect(screen.getByText('Restore from Bin')).toBeTruthy();

      fireEvent.click(screen.getByTestId('delete-action-item'));
    }
  });
});
