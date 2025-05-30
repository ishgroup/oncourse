/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CorporatePass } from '@api/model';
import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { initialize } from 'redux-form';
import { getFilters, setFilterGroups, setListEditRecord, } from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../common/components/list-view/constants';
import ListView from '../../../common/components/list-view/ListView';
import { getManualLink } from '../../../common/utils/getManualLink';
import { COMMON_PLACEHOLDER } from '../../../constants/Forms';
import { FilterGroup, FindRelatedItem } from '../../../model/common/ListView';
import { getEntityTags } from '../../tags/actions';
import CorporatePassEditView from './components/CorporatePassEditView';

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Current",
        expression: "expiryDate is null or expiryDate >= today",
        active: true
      },
      {
        name: "Expired",
        expression: "expiryDate < today",
        active: false
      }
    ]
  }
];

const Initial: CorporatePass = {
  id: null,
  contactId: null,
  contactFullName: null,
  password: null,
  expiryDate: null,
  invoiceEmail: null,
  linkedDiscounts: [],
  linkedSalables: []
};

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == CorporatePass and entityId" },
  { title: "Classes", list: "class", expression: "corporatePassCourseClass.corporatePass.id" },
  { title: "Contacts", list: "contact", expression: "corporatePasses.id" },
  {
    title: "Enrolled Students",
    list: "contact",
    expression: "student.enrolments.status == SUCCESS and student.enrolments.invoiceLines.invoice.corporatePassUsed.id"
  },
  { title: "Enrolments", list: "enrolment", expression: "invoiceLines.invoice.corporatePassUsed.id" },
  { title: "Invoices", list: "invoice", expression: "corporatePassUsed.id" }
];

const manualLink = getManualLink("corporate-pass");

const secondaryColumnCondition = dataRow => dataRow["expiryDate"] || COMMON_PLACEHOLDER;

class CorporatePasses extends React.Component<any, any> {
  componentDidMount() {
    this.props.getFilters();
    this.props.getTagsForClassesSearch();
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
          secondaryColumn: "expiryDate",
          secondaryColumnCondition
        }}
        EditViewContent={CorporatePassEditView}
        rootEntity="CorporatePass"
        onInit={onInit}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        editViewProps={{
          nameCondition: pass => pass.contactFullName,
          manualLink,
          hideTitle: true
        }}
        noListTags
      />
    );
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getTagsForClassesSearch: () => {
    dispatch(getEntityTags("CourseClass"));
    dispatch(getEntityTags("Course"));
  },
  getFilters: () => dispatch(getFilters("CorporatePass")),
  setFilterGroups: (filterGroups: FilterGroup[]) => dispatch(setFilterGroups(filterGroups))
});

export default connect<any, any, any>(null, mapDispatchToProps)(CorporatePasses);