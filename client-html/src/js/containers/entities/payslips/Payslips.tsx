/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Payslip } from '@api/model';
import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { initialize } from 'redux-form';
import { checkPermissions } from '../../../common/actions';
import { clearListState, getFilters, setListEditRecord } from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../common/components/list-view/constants';
import ListView from '../../../common/components/list-view/ListView';
import { getManualLink } from '../../../common/utils/getManualLink';
import { FilterGroup, FindRelatedItem } from '../../../model/common/ListView';
import { State } from '../../../reducers/state';
import { getListTags } from '../../tags/actions';
import PayslipCogwheelOptions from './components/PayslipCogwheelOptions';
import PayslipsEditView from './components/PayslipsEditView';

const Initial: Payslip = {
  status: "New",
  tutorId: null,
  tutorFullName: null,
  publicNotes: null,
  privateNotes: null,
  tags: [],
  paylines: []
};

const filterGroups: FilterGroup[] = [
  {
    title: "STATUS",
    filters: [
      {
        name: "New",
        expression: "status is HOLLOW",
        active: false
      },
      {
        name: "Completed",
        expression: "status is COMPLETED",
        active: false
      },
      {
        name: "Approved",
        expression: "status is APPROVED",
        active: false
      },
      {
        name: "Paid/Exported",
        expression: "status is FINALISED",
        active: false
      }
    ]
  },
];

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Payslip and entityId" },
  { title: "Contacts", list: "contact", expression: "payslips.id" },
  { title: "Classes", list: "class", expression: "costs.paylines.payslip.id" }
];
const nameCondition = (values: Payslip) => values.tutorFullName;

const manualLink = getManualLink("tutor-pay");

class Payslips extends React.Component<any, any> {
  componentDidMount() {
    this.props.getFilters();
    this.props.getTags();
    this.props.getGenerateAccess();
    this.props.getConfirmAccess();
  }

  componentWillUnmount() {
    this.props.clearListState();
  }

  shouldComponentUpdate() {
    return false;
  }

  render() {
    const {
      onInit
    } = this.props;

    return (
      <ListView
        listProps={{
          primaryColumn: "contact.fullName",
          secondaryColumn: "createdOn"
        }}
        editViewProps={{
          nameCondition,
          manualLink,
          hideTitle: true
        }}
        EditViewContent={PayslipsEditView}
        rootEntity="Payslip"
        onInit={onInit}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        CogwheelAdornment={PayslipCogwheelOptions}
      />
    );
  }
}

const mapStateToProps = (state: State) => ({
  currency: state.location.currency
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getFilters: () => dispatch(getFilters("Payslip")),
  getTags: () => dispatch(getListTags("Payslip")),
  clearListState: () => dispatch(clearListState()),
  getGenerateAccess: () => dispatch(checkPermissions({ path: "/a/v1/list/option/payroll?entity=Payslip", method: "PUT" })),
  getConfirmAccess: () => dispatch(
    checkPermissions({ path: "/a/v1/list/option/payroll?entity=Payslip&bulkConfirmTutorWages=true", method: "POST" })
  )
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Payslips);