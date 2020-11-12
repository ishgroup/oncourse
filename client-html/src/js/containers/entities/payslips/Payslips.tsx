/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { Payslip } from "@api/model";
import {
  clearListState,
  getFilters,
  setListEditRecord
} from "../../../common/components/list-view/actions";
import { defaultContactName } from "../contacts/utils";
import {
  createPayslip, updatePayslip, removePayslip, getPayslip
} from "./actions";
import PayslipsEditView from "./components/PayslipsEditView";
import ListView from "../../../common/components/list-view/ListView";
import { getListTags } from "../../tags/actions";
import PayslipCogwheelOptions from "./components/PayslipCogwheelOptions";
import { checkPermissions } from "../../../common/actions";
import { State } from "../../../reducers/state";
import { getManualLink } from "../../../common/utils/getManualLink";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";

const Initial: Payslip = {
  status: "New",
  tutorId: null,
  tutorFullName: null,
  publicNotes: null,
  privateNotes: null,
  tags: [],
  paylines: []
};

const findRelatedGroup: any[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Payslip and entityId" },
  { title: "Contacts", list: "contact", expression: "payslips.id" },
  { title: "Classes", list: "class", expression: "costs.paylines.payslip.id" }
];
const nameCondition = (values: Payslip) => defaultContactName(values.tutorFullName);

const manualLink = getManualLink("payroll");

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

  onCreate = value => {
    const paylines = JSON.parse(JSON.stringify(value.paylines));

    paylines.forEach(i => {
      delete i.deferred;
    });

    const tags = JSON.parse(JSON.stringify(value.tags));

    tags.forEach(t => {
      delete t.active;
    });

    this.props.onCreate({ ...value, paylines, tags });
  };

  render() {
    const {
      onDelete, onSave, getPayslipRecord, onInit
    } = this.props;

    return (
      <div>
        <ListView
          listProps={{
            primaryColumn: "contact.fullName",
            secondaryColumn: "createdOn"
          }}
          editViewProps={{
            nameCondition,
            manualLink
          }}
          EditViewContent={PayslipsEditView}
          getEditRecord={getPayslipRecord}
          rootEntity="Payslip"
          onInit={onInit}
          onDelete={onDelete}
          onSave={onSave}
          onCreate={this.onCreate}
          findRelated={findRelatedGroup}
          CogwheelAdornment={PayslipCogwheelOptions}
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  currency: state.currency
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getFilters: () => dispatch(getFilters("Payslip")),
  getTags: () => dispatch(getListTags("Payslip")),
  getPayslipRecord: (id: string) => dispatch(getPayslip(id)),
  clearListState: () => dispatch(clearListState()),
  onSave: (id: string, payslip: Payslip) => dispatch(updatePayslip(id, payslip)),
  onCreate: (payslip: Payslip) => dispatch(createPayslip(payslip)),
  onDelete: (id: string) => dispatch(removePayslip(id)),
  getGenerateAccess: () => dispatch(checkPermissions({ path: "/a/v1/list/option/payroll?entity=Payslip", method: "PUT" })),
  getConfirmAccess: () => dispatch(
    checkPermissions({ path: "/a/v1/list/option/payroll?entity=Payslip&bulkConfirmTutorWages=true", method: "POST" })
  )
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Payslips);
