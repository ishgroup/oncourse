/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Contact } from '@api/model';
import Typography from '@mui/material/Typography';
import { isBefore } from 'date-fns';
import React, { useCallback, useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { initialize } from 'redux-form';
import company from '../../../../images/company.png';
import person from '../../../../images/person.png';
import tutorStudent from '../../../../images/student-tutor.png';
import student from '../../../../images/student.png';
import tutor from '../../../../images/tutor.png';
import { checkPermissions } from '../../../common/actions';
import instantFetchErrorHandler from '../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import { notesAsyncValidate } from '../../../common/components/form/notes/utils';
import { clearListState, getFilters, setListEditRecord } from '../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../common/components/list-view/constants';
import ListView from '../../../common/components/list-view/ListView';
import EntityService from '../../../common/services/EntityService';
import { getManualLink } from '../../../common/utils/getManualLink';
import { FilterGroup, FindRelatedItem } from '../../../model/common/ListView';
import { State } from '../../../reducers/state';
import { getContactRelationTypes, getCountries, getLanguages, getPaymentTypes } from '../../preferences/actions';
import { PreferencesState } from '../../preferences/reducers/state';
import { getDefaultInvoiceTerms } from '../invoices/actions';
import { getContactsConcessionTypes, getContactsRelationTypes, getContactsTaxTypes, getContactTags } from './actions';
import ContactCogWheel from './components/ContactCogWheel';
import ContactEditView from './components/ContactEditView';
import { getContactFullName } from './utils';

export type ContactType = "STUDENT" | "TUTOR" | "COMPANY" | "TUTOR_STUDENT";

export interface ContactsProps {
  dispatch?: Dispatch<any>;
  onInit?: () => void;
  getRecords?: () => void;
  getFilters?: () => void;
  setCustomTableModel?: () => void;
  clearListState?: () => void;
  getTags?: () => void;
  getCountries?: () => void;
  getLanguages?: () => void;
  getContactsRelationTypes?: () => void;
  getContactsConcessionTypes?: () => void;
  getTaxTypes?: () => void;
  getDefaultTerms?: () => void;
  getPermissions?: () => void;
  getContactRelationTypes?: () => void;
  selection?: string[];
  relationTypes?: PreferencesState["contactRelationTypes"];
  isVerifyingUSI?: boolean;
  usiVerificationResult?: any;
  getPaymentTypes?: any;
}

export const formatRelationsBeforeSave = relations => {
  if (Array.isArray(relations) && relations.length) {
    return relations
      .filter(r => !!(r.relationId && r.relatedContactId))
      .map(r => {
        const isReverseRelation = String(r.relationId).includes("r");
        return {
          id: r.id,
          relationId: parseInt(r.relationId, 10),
          contactToId: isReverseRelation ? null : r.relatedContactId,
          contactToName: isReverseRelation ? null : r.relatedContactName,
          contactFromId: isReverseRelation ? r.relatedContactId : null,
          contactFromName: isReverseRelation ? r.relatedContactName : null
        };
      });
  }

  return relations;
};

export const ContactInitial: Contact = {
  id: 0,
  student: null,
  tutor: null,
  firstName: null,
  lastName: null,
  middleName: null,
  birthDate: null,
  gender: null,
  street: null,
  suburb: null,
  allowSms: false,
  allowPost: false,
  allowEmail: true,
  isCompany: false,
  deliveryStatusPost: 0,
  deliveryStatusSms: 0,
  deliveryStatusEmail: 0,
  customFields: {},
  abandonedCarts: [],
  tags: []
};

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Companies",
        expression: "isCompany == true",
        active: false
      },
      {
        name: "Students",
        expression: "isStudent == true",
        active: false
      },
      {
        name: "Students currently enrolled",
        expression:
        // eslint-disable-next-line max-len
          "(studentCourseClass.endDateTime >= yesterday or studentCourseClass.endDateTime == null) and studentCourseClass.isCancelled == false and studentEnrolments.status == SUCCESS",
        active: false
      },
      {
        name: "Tutors",
        expression: "isTutor == true",
        active: false
      },
      {
        name: "Tutors currently teaching",
        expression:
          "tutorCourseClass.endDateTime != null and tutorCourseClass.endDateTime >= yesterday and tutorCourseClass.isCancelled != true",
        active: false
      }
    ]
  }
];

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Applications", list: "application", expression: "student.contact.id" },
  { title: "Audits", list: "audit", expression: "entityIdentifier == Contact and entityId" },
  { title: "Certificates", list: "certificate", expression: "student.contact.id" },
  { title: "Classes enrolled", list: "class", expression: "enrolments.student.contact.id" },
  { title: "Classes taught", list: "class", expression: "tutorRoles.tutor.contact.id" },
  {
    title: "Documents",
    list: "document",
    expression: "attachmentRelations.entityIdentifier == Contact and attachmentRelations.entityRecordId"
  },
  { title: "Enrolments", list: "enrolment", expression: "student.contact.id" },
  { title: "Outcomes", list: "outcome", expression: "contact.id" },
  { title: "Feedback about tutors", list: "survey", expression: "enrolment.courseClass.tutorRoles.tutor.contact.id" },
  { title: "Feedback from students", list: "survey", expression: "enrolment.student.contact.id" },
  { title: "Invoices", list: "invoice", expression: "contact.id" },
  { title: "Memberships", list: "membership", expression: "productItems.contact.id" },
  { title: "Payments in", list: "paymentIn", expression: "payer.id" },
  { title: "Payments out", list: "paymentOut", expression: "payee.id" },
  { title: "Payslips", list: "payslip", expression: "contact.id" },
  { title: "Sales", list: "sale", expression: "purchasedBy.id" },
  { title: "Student timetable", list: "timetable", expression: "courseClass.enrolments.student.id" },
  { title: "Tutor timetable", list: "timetable", expression: "tutor.contact.id" },
  { title: "Transactions", list: "transaction", expression: "contact.id" },
  { title: "VET reporting", list: "vetReporting", expression: "id" },
  { title: "Waiting lists", list: "waitingList", expression: "student.contact.id" }
];

const secondaryColumnCondition = row => row.birthDate || "Birthday not specified";

const manualLink = getManualLink("creating-and-modifying-contacts");

const getContactTypeImage = (type: ContactType) => {
  switch (type) {
    case "STUDENT": {
      return student;
    }
    case "TUTOR": {
      return tutor;
    }
    case "COMPANY": {
      return company;
    }
    case "TUTOR_STUDENT": {
      return tutorStudent;
    }
    default: {
      return person;
    }
  }
};

const customColumnFormats = {
  contactType: v => (v ? <img src={getContactTypeImage(v)} alt={v} /> : null)
};

export const getDisabledSubmitCondition = (isVerifyingUSI, usiVerificationResult): boolean => (
  isVerifyingUSI || (usiVerificationResult && usiVerificationResult.verifyStatus === "Invalid format")
);

const SearchMenuItem = React.memo<{ content, data }>(({ content, data }) => (
  <div className="d-flex align-items-baseline">
    {content}
    <Typography className="ml-0-5" variant="caption" color="textSecondary">
      (
      {data.prefix}
      )
    </Typography>
  </div>
));

const searchMenuItemsRenderer = (content, data) => (
  data.prefix ? <SearchMenuItem content={content} data={data} /> : content
);

const today = new Date();
today.setHours(0, 0, 0, 0);

const setRowClasses = row => {
  const dateFinished = row["tutor.dateFinished"];

  if (dateFinished && isBefore(new Date(dateFinished), today)) {
    return "text-op05";
  }

  return undefined;
};

const Contacts: React.FC<ContactsProps> = props => {
  const {
    dispatch,
    getFilters,
    clearListState,
    onInit,
    getTags,
    getCountries,
    getLanguages,
    getContactsRelationTypes,
    getContactsConcessionTypes,
    getTaxTypes,
    getDefaultTerms,
    getPermissions,
    getContactRelationTypes,
    relationTypes,
    selection,
    isVerifyingUSI,
    usiVerificationResult,
    getPaymentTypes,
  } = props;

  const [findRelatedItems, setFindRelatedItems] = useState([]);

  useEffect(() => {
    if (relationTypes) {
      const relationTypesItem: FindRelatedItem = {
        title: "Contacts related as...",
        items: [{ title: "All related contacts", list: "contact", expression: "allRelatedContacts.id" }]
      };

      relationTypes.forEach(t => {
        if (t.relationName === t.reverseRelationName) {
          relationTypesItem.items.push({
            title: t.relationName,
            list: "contact",
            // eslint-disable-next-line max-len
            customExpression: ids => `(fromRelationType.id = "${t.id}" and fromRelatedContacts.id in (${ids})) or (toRelationType.id = "${t.id}" and toRelatedContacts.id in (${ids}))`
          });

          return;
        }

        relationTypesItem.items.push(
          {
            title: t.relationName,
            list: "contact",
            expression: `toContacts.relationType.toContactName = "${t.reverseRelationName}" and toContacts.toContact.id`
          },
          {
            title: t.reverseRelationName,
            list: "contact",
            expression: `fromContacts.relationType.fromContactName = "${t.relationName}" and fromContacts.fromContact.id`
          }
        );
      });

      const updatedGroups = [...findRelatedGroup];

      updatedGroups.splice(2, 0, relationTypesItem);
      setFindRelatedItems(updatedGroups);
    }
  }, [relationTypes, selection]);

  useEffect(() => {
    getPaymentTypes();
    getFilters();
    getTags();
    getCountries();
    getLanguages();
    getContactsRelationTypes();
    getContactsConcessionTypes();
    getTaxTypes();
    getDefaultTerms();
    getPermissions();
    getContactRelationTypes();

    return () => {
      clearListState();
    };
  }, []);

  const getContactFullNameWithTitle = (values: Contact) =>
    `${!values.isCompany && values.title && values.title.trim().length > 0 ? `${values.title} ` : ""}${!values.isCompany ? getContactFullName(values) : values.lastName}`;

  const getCustomBulkEditFields = useCallback(async () => {
    if (!selection || !selection.length) return [];

    const result = [];

    await EntityService.getPlainRecords(
      "CustomField",
      "customFieldType.key,customFieldType.name",
      `entityIdentifier is Contact and customFieldType.dataType is PORTAL_SUBDOMAIN`
    )
      .then(res => {
        if (res.rows.length) {
          const values = res.rows[0].values;
          result.push({
            keyCode: `customFields.${values[0]}`,
            label: values[1],
            name: values[1],
            type: "Portal subdomain"
          });
        }
      })
      .catch(err => instantFetchErrorHandler(dispatch, err));

    return result;
  }, [selection]);

  return (
    <ListView
      listProps={{
        firstColumnName: "contactType",
        primaryColumn: "fullName",
        secondaryColumn: "birthDate",
        setRowClasses,
        secondaryColumnCondition,
        customColumnFormats
      }}
      editViewProps={{
        manualLink,
        nameCondition: getContactFullNameWithTitle,
        disabledSubmitCondition: getDisabledSubmitCondition(isVerifyingUSI, usiVerificationResult),
        asyncValidate: notesAsyncValidate,
        asyncChangeFields: ["notes[].message"],
        hideTitle: true
      }}
      EditViewContent={ContactEditView}
      rootEntity="Contact"
      onInit={onInit}
      findRelated={findRelatedItems}
      filterGroupsInitial={filterGroups}
      CogwheelAdornment={ContactCogWheel}
      searchMenuItemsRenderer={searchMenuItemsRenderer}
      getCustomBulkEditFields={getCustomBulkEditFields}
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  onInit: () => {
    dispatch(setListEditRecord(ContactInitial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, ContactInitial));
  },
  getTags: () => {
    dispatch(getContactTags());
  },
  getContactRelationTypes: () => {
    dispatch(getContactRelationTypes());
  },
  getFilters: () => dispatch(getFilters("Contact")),
  getCountries: () => dispatch(getCountries()),
  getLanguages: () => dispatch(getLanguages()),
  getContactsRelationTypes: () => dispatch(getContactsRelationTypes()),
  getContactsConcessionTypes: () => dispatch(getContactsConcessionTypes()),
  getDefaultTerms: () => dispatch(getDefaultInvoiceTerms()),
  getTaxTypes: () => dispatch(getContactsTaxTypes()),
  clearListState: () => dispatch(clearListState()),
  getPermissions: () => {
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Enrolment", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=PriorLearning", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Outcome", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Certificate", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=PaymentIn", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/option/payroll?entity=Contact&bulkConfirmTutorWages=true", method: "POST" }));
    dispatch(checkPermissions({ path: "/a/v1/list/option/payroll?entity=Contact", method: "PUT" }));
  },
  getPaymentTypes: () => dispatch(getPaymentTypes())
});

const mapStateToProps = (state: State) => ({
  relationTypes: state.preferences.contactRelationTypes,
  selection: state.list.selection,
  isVerifyingUSI: state.contacts.verifyingUSI,
  usiVerificationResult: state.contacts.usiVerificationResult
});

export default connect(mapStateToProps, mapDispatchToProps)(Contacts);