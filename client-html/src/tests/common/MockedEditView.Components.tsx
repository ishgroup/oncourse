import { defaultComponents } from "./Default.Components";
import * as React from "react";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../js/common/components/list-view/constants";
import ListEditView, { editViewFormRole } from "../../js/common/components/list-view/components/edit-view/EditView";

interface Props {
  entity: string;
  EditView: any;
  record: (mockedApi: any) => object;
  render: ({
    screen, initialValues, formRoleName, mockedApi, fireEvent
  }) => any;
  state?: (mockedApi: any) => object;
}

export const mockedEditView = (props: Props) => {
  const {
    entity, EditView, record, render, state
  } = props;

  defaultComponents({
    entity,
    View: pr => <ListEditView {...pr} />,
    record,
    beforeFn: () => {
      window.performance.getEntriesByName = jest.fn(() => []);
    },
    defaultProps: () => ({
      EditViewContent: pr => <EditView {...pr} />,
      rootEntity: entity,
      form: LIST_EDIT_VIEW_FORM_NAME,
      hasSelected: true,
      creatingNew: false,
      toogleFullScreenEditView: jest.fn(),
    }),
    render: pr => render({ ...pr, formRoleName: editViewFormRole }),
    state,
  });
};
