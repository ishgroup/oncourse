import * as React from "react";
import {
  render as testRender, screen as testScreen, waitFor, cleanup, fireEvent as testFireEvent
} from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { mockedAPI, TestEntry } from "../TestEntry";

interface Props {
  entity: string;
  View: (props: any) => any;
  record: (mockedApi: any) => object;
  render: ({
    screen, initialValues, mockedApi, fireEvent
  }) => any;
  defaultProps?: ({ entity, initialValues, mockedApi }) => object;
  beforeFn?: () => void;
}

export const defaultComponents: ({
  entity, View, record, render, defaultProps, beforeFn,
}: Props) => void = ({
  entity, View, record, render, defaultProps, beforeFn,
}) => {
  const initialValues = record(mockedAPI);

  let viewProps = { initialValues, values: initialValues };

  if (defaultProps) {
    viewProps = { ...viewProps, ...defaultProps({ entity, initialValues, mockedApi: mockedAPI }) };
  }

  const MockedEditView = pr => <View {...{ ...pr, ...viewProps }} />;

  if (beforeFn) {
    beforeFn();
  }

  afterEach(cleanup);

  it(`${entity} components should render with given values`, async () => {
    await waitFor(() => testRender(
      <TestEntry>
        <MockedEditView />
      </TestEntry>,
    ), {
      timeout: 2000
    });

    return render({
      screen: testScreen,
      initialValues,
      mockedApi: mockedAPI,
      fireEvent: testFireEvent,
    });
  });
};
