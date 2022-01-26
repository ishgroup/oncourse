import * as React from "react";
import { render as testRender, screen as testScreen } from '@testing-library/react';
import * as testUserEvent from '@testing-library/user-event';
import { mockedAPI, TestEntry } from "../TestEntry";

interface Props {
  entity: string;
  View: (props: any) => any;
  record: (mockedApi: any) => object;
  render: ({ screen, initialValues, userEvent }) => void;
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

  it(`${entity} components should render with given values`, async () => {
    await testRender(
      <TestEntry>
        <MockedEditView />
      </TestEntry>,
    );

    return new Promise<void>(resolve => {
      setTimeout(() => {
        render({
          screen: testScreen,
          initialValues,
          userEvent: testUserEvent
        });
        resolve();
      }, 2000);
    });
  });
};
