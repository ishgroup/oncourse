import { act, fireEvent as testFireEvent, render as testRender, screen as testScreen } from '@testing-library/react';
import * as React from 'react';
import { MockAdapter } from '../../dev/mock/MockAdapter';
import { mockedAPI, TestEntry } from '../TestEntry';

interface Props {
  entity: string;
  View: (props: any) => any;
  record: (mockedApi: MockAdapter) => object;
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

  if (beforeFn) {
    beforeFn();
  }



  testRender(
    <TestEntry state={state ? { ...state({ mockedApi: mockedAPI, viewProps }) } : {}}>
      <View {...viewProps} />
    </TestEntry>
  );

  it(`${entity} components should render with given values`, async () => {
    await act( async () => render({
      screen: testScreen,
      initialValues,
      mockedApi: mockedAPI,
      fireEvent: testFireEvent,
      viewProps,
    }));
  });
};
