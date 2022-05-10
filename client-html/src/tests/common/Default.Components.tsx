import * as React from "react";
import {
  render as testRender, screen as testScreen, waitFor, fireEvent as testFireEvent
} from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { mockedAPI, TestEntry } from "../TestEntry";

interface Props {
  entity: string;
  View: (props: any) => any;
  record: (mockedApi: any) => object;
  render: ({
    screen, initialValues, mockedApi, fireEvent, viewProps
  }) => any;
  defaultProps?: ({ entity, initialValues, mockedApi }) => object;
  beforeFn?: () => void;
  state?: ({ mockedApi, viewProps }) => object;
}

export const defaultComponents = (props: Props) => {
  const {
    entity,
    View,
    record,
    render,
    defaultProps,
    beforeFn,
    state
  } = props;

  const initialValues = record(mockedAPI);

  let viewProps = { initialValues, values: initialValues };

  if (defaultProps) {
    viewProps = { ...viewProps, ...defaultProps({ entity, initialValues, mockedApi: mockedAPI }) };
  }

  const MockedEditView = pr => <View {...{ ...pr, ...viewProps }} />;

  if (beforeFn) {
    beforeFn();
  }

  it(`${entity} components should render with given values`, async () => {
    testRender(
      <TestEntry state={state ? { ...state({ mockedApi: mockedAPI, viewProps }) } : {}}>
        <MockedEditView />
      </TestEntry>
    );

    await waitFor(() => {
      render({
        screen: testScreen,
        initialValues,
        mockedApi: mockedAPI,
        fireEvent: testFireEvent,
        viewProps,
      });
    }, {
      timeout: 2000
    });
  });
};
