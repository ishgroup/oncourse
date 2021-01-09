import * as React from "react";
import { mount } from "enzyme";
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

  const defaultProps = {
    EditViewContent: EditView,
    rootEntity: entity,
    initialValues,
    form: LIST_EDIT_VIEW_FORM_NAME,
    hasSelected: true,
    creatingNew: false
  };

  const MockedEditView = pr => <ListEditView {...{ ...pr, ...defaultProps }} />;

  window.performance.getEntriesByName = jest.fn(() => []);

  const wrapper = mount(
    <TestEntry>
      <MockedEditView />
    </TestEntry>
  );

  render(wrapper, initialValues);
};
