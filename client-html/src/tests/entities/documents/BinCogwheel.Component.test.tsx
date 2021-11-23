// import { createMount } from "@mui/material/test-utils";
import { shallow } from 'enzyme';
import * as React from "react";
import { mockedAPI, TestEntry } from "../../TestEntry";
import BinCogwheel from "../../../js/containers/entities/documents/components/BinCogwheel";

// TODO Enable test on fix

describe("Virtual rendered BinCogwheel of Document list view", () => {
  const initialValues = mockedAPI.db.getDocumentsForBinCogweel();
  let mount;

  // beforeAll(() => {
  //   mount = createMount();
  // });
  //
  // afterAll(() => {
  //   mount.cleanUp();
  // });

  const defaultProps = {
    selection: ["1", "2", "3"],
    records: initialValues,
    closeMenu: () => {}
  };

  const MockedEditView = pr => <BinCogwheel {...{ ...pr, ...defaultProps }} />;

  it(`Bincogwheel component of Document entity should render with given values`, () => {
    const wrapper = shallow(
      <TestEntry>
        <MockedEditView />
      </TestEntry>
    );

    // expect(wrapper.find("span").at(0).text()).toContain("Restore from Bin");
    //
    // wrapper.find("li").simulate("click");
  });
});
