/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Application } from '@api/model';
import { format as formatDate } from 'date-fns';
import { YYYY_MM_DD_MINUSED } from 'ish-ui';
import React, { Dispatch } from 'react';
import { connect } from 'react-redux';
import { initialize } from 'redux-form';
import { notesAsyncValidate } from '../../../common/components/form/notes/utils';
import { getFilters, setListEditRecord } from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../common/components/list-view/constants';
import ListView from '../../../common/components/list-view/ListView';
import { getManualLink } from '../../../common/utils/getManualLink';
import { FilterGroup, FindRelatedItem } from '../../../model/common/ListView';
import { State } from '../../../reducers/state';
import { getListTags } from '../../tags/actions';
import BulkEditCogwheelOption from '../common/components/BulkEditCogwheelOption';
import ApplicationEditView from './components/ApplicationEditView';

interface ApplicationsProps {
  getApplicationRecord?: () => void;
  onInit?: () => void;
  getFilters?: () => void;
  getTags?: () => void;
  customFieldTypesUpdating: any;
  customFieldTypes: any;
}

const Initial: Application = {
  applicationDate: formatDate(new Date(), YYYY_MM_DD_MINUSED),
  createdBy: null,
  customFields: {},
  documents: null,
  enrolBy: null,
  feeOverride: null,
  id: 0,
  reason: null,
  source: "office",
  status: "New",
  tags: []
};

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "New",
        expression: "(status == NEW) and ((enrolBy >= today) or (enrolBy == null))",
        active: true
      },
      {
        name: "In progress",
        expression: "(status == IN_PROGRESS) and ((enrolBy >= today) or (enrolBy == null))",
        active: true
      },
      {
        name: "Offered",
        expression: "(status == OFFERED) and ((enrolBy >= today) or (enrolBy == null))",
        active: true
      },
      {
        name: "Accepted",
        expression: "(status == ACCEPTED)",
        active: true
      },
      {
        name: "Rejected",
        expression: "(status == REJECTED)",
        active: true
      },
      {
        name: "Withdrawn",
        expression: "(status == WITHDRAWN)",
        active: true
      },
      {
        name: "Expired",
        expression: "((status == NEW) or (status == IN_PROGRESS) or (status == OFFERED)) and (enrolBy before now)",
        active: false
      }
    ]
  }
];

const manualLink = getManualLink("navigating-the-application-window");

const nameCondition = values => (values ? values.studentName : "");

const findRelatedGroup: FindRelatedItem[] = [
  {
    title: "Audits",
    list: "audit",
    expression: "entityIdentifier == Application and entityId"
  },
  { title: "Contacts", list: "contact", expression: "student.applications.id" },
  { title: "Courses", list: "course", expression: "applications.id" },
  { title: "Enrolments", list: "enrolment", expression: "student.applications.id" }
];

class Applications extends React.Component<ApplicationsProps, any> {
  componentDidMount() {
    this.props.getTags();
    this.props.getFilters();
  }

  render() {
    const {
      onInit
    } = this.props;

    return (
      <ListView
        listProps={{
          primaryColumn: "course.name",
          secondaryColumn: "student.contact.fullName"
        }}
        editViewProps={{
          manualLink,
          asyncValidate: notesAsyncValidate,
          asyncChangeFields: ["notes[].message"],
          nameCondition,
          hideTitle: true
        }}
        EditViewContent={ApplicationEditView}
        CogwheelAdornment={BulkEditCogwheelOption}
        rootEntity="Application"
        onInit={onInit}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
      />
    );
  }
}

const mapStateToProps = (state: State) => ({
  customFieldTypesUpdating: state.customFieldTypes.updating,
  customFieldTypes: state.customFieldTypes.types["Application"]
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getFilters: () => dispatch(getFilters("Application")),
  getTags: () => dispatch(getListTags("Application")),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Applications);