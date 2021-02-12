import * as React from "react";
import { createMount } from "@material-ui/core/test-utils";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../js/common/components/list-view/constants";
import ListEditView from "../../js/common/components/list-view/components/edit-view/EditView";
import { mockedAPI, TestEntry } from "../TestEntry";

interface Props {
  entity: string;
  EditView: any;
  record: (mockedApi: any) => object;
  render: (wrapper: any, initialValues: any) => any;
}

export const mockedEditView: ({
  entity, EditView, record, render
}: Props) => void = ({
  entity, EditView, record, render
}) => {
  const initialValues = record(mockedAPI);
  let mount;

  beforeAll(() => {
    mount = createMount();
  });

  afterAll(() => {
    mount.cleanUp();
  });

  const defaultProps = {
    EditViewContent: props => <EditView {...props} />,
    rootEntity: entity,
    initialValues,
    values: initialValues,
    form: LIST_EDIT_VIEW_FORM_NAME,
    hasSelected: true,
    creatingNew: false
  };

  const MockedEditView = pr => <ListEditView {...{ ...pr, ...defaultProps }} />;

  window.performance.getEntriesByName = jest.fn(() => []);

  it(`${entity} components should render with given values`, () => {
    const wrapper = mount(
      <TestEntry>
        <MockedEditView />
      </TestEntry>
    );

    return new Promise(resolve => {
      setTimeout(() => {
        render(wrapper, initialValues);
        resolve();
      }, 1000);
    });
  });
};
