import * as React from "react";
// import { createMount } from "@mui/material/test-utils";
import { shallow } from 'enzyme';
import { mockedAPI, TestEntry } from "../TestEntry";

interface Props {
  entity: string;
  View: (props: any) => any;
  record: (mockedApi: any) => object;
  render: (wrapper: any, initialValues: any, shallow?: any) => any;
  defaultProps?: ({ entity, initialValues, mockedApi }) => object;
  beforeFn?: () => void;
}

export const defaultComponents: ({
  entity, View, record, render, defaultProps, beforeFn,
}: Props) => void = ({
  entity, View, record, render, defaultProps, beforeFn,
}) => {
  const initialValues = record(mockedAPI);
  // let mount;

  // beforeAll(() => {
  //   mount = createMount();
  // });
  //
  // afterAll(() => {
  //   mount.cleanUp();
  // });

  let viewProps = { initialValues, values: initialValues };

  if (defaultProps) {
    viewProps = { ...viewProps, ...defaultProps({ entity, initialValues, mockedApi: mockedAPI }) };
  }

  const MockedEditView = pr => <View {...{ ...pr, ...viewProps }} />;

  if (beforeFn) {
    beforeFn();
  }

  it(`${entity} components should render with given values`, async () => {
    const wrapper = await shallow(
      <TestEntry>
        <MockedEditView />
      </TestEntry>,
    );

    return new Promise<void>(resolve => {
      setTimeout(() => {
        render(wrapper.render(), initialValues, wrapper);
        resolve();
      }, 2000);
    });
  });
};
