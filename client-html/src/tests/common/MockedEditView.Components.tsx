import * as React from "react";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../js/common/components/list-view/constants";
import ListEditView, { editViewFormRole } from "../../js/common/components/list-view/components/edit-view/EditView";
import { defaultComponents } from "./Default.Components";

interface Props {
  entity: string;
  EditView: any;
  record: (mockedApi: any) => object;
  render: ({
    screen, initialValues, userEvent, formRoleName, mockedApi
  }) => any;
}

export const mockedEditView: ({
  entity, EditView, record, render
}: Props) => void = ({
  entity, EditView, record, render
}) => defaultComponents({
  entity,
  View: pr => <ListEditView {...pr} />,
  record,
  beforeFn: () => {
    window.performance.getEntriesByName = jest.fn(() => []);
  },
  defaultProps: () => ({
    EditViewContent: props => <EditView {...props} />,
    rootEntity: entity,
    form: LIST_EDIT_VIEW_FORM_NAME,
    hasSelected: true,
    creatingNew: false
  }),
  render: pr => render({ ...pr, formRoleName: editViewFormRole })
});
