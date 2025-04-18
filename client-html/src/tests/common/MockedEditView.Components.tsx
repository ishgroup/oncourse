import * as React from "react";
import EditView from '../../js/common/components/list-view/components/edit-view/EditView';
import { LIST_EDIT_VIEW_FORM_NAME } from "../../js/common/components/list-view/constants";
import ListEditView, { editViewFormRole } from "../../js/common/components/list-view/components/edit-view/EditView";
import { State } from '../../js/reducers/state';
import { defaultComponents } from "./Default.Components";

interface Props {
  entity: string;
  EditView: any;
  record: (mockedApi) => object;
  render: ({
    screen, initialValues, formRoleName, mockedApi, fireEvent
  }) => any;
  state?: ({ mockedApi, viewProps }) => Partial<State>;
}

export const mockedEditView = ({
 entity, EditView, record, render, state
}: Props) => {

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
