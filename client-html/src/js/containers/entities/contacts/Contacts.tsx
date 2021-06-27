/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { isBefore } from "date-fns";
import React, {
  Dispatch, useCallback, useEffect, useState
} from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import Typography from "@material-ui/core/Typography";
import { Contact } from "@api/model";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import {
  setListEditRecord,
  getFilters,
 clearListState
} from "../../../common/components/list-view/actions";
import ListView from "../../../common/components/list-view/ListView";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import {
  getContact,
  getContactsRelationTypes,
  getContactsConcessionTypes,
  getContactsTaxTypes,
  getContactTags,
  updateContact,
  createContact,
  deleteContact
} from "./actions";
import ContactEditView from "./components/ContactEditView";
import { getManualLink } from "../../../common/utils/getManualLink";
import {
 getContactRelationTypes, getCountries, getLanguages, getPaymentTypes
} from "../../preferences/actions";
import { getDefaultInvoiceTerms } from "../invoices/actions";
import ContactCogWheel from "./components/ContactCogWheel";
import { checkPermissions } from "../../../common/actions";
import { State } from "../../../reducers/state";
import { PreferencesState } from "../../preferences/reducers/state";
import student from "../../../../images/student.png";
import tutor from "../../../../images/tutor.png";
import company from "../../../../images/company.png";
import tutorStudent from "../../../../images/student-tutor.png";
import person from "../../../../images/person.png";
import { Classes } from "../../../model/entities/CourseClass";
import SendMessageEditView from "../messages/components/SendMessageEditView";
import { getContactFullName } from "./utils";

export type ContactType = "STUDENT" | "TUTOR" | "COMPANY" | "TUTOR_STUDENT";

interface ContactsProps {
  onInit?: () => void;
  onCreate?: (contact: Contact) => void;
  onSave?: (id: string, contact: Contact) => void;
  getRecords?: () => void;
  getFilters?: () => void;
  onDelete?: (id: number) => void;
  clearListState?: () => void;
  getTags?: () => void;
  getContactRecord?: (id: string) => void;
  getCountries?: () => void;
  getLanguages?: () => void;
  getContactsRelationTypes?: () => void;
  getContactsConcessionTypes?: () => void;
  getTaxTypes?: () => void;
  getDefaultTerms?: () => void;
  getGenerateAccessForContact?: () => void;
  getConfirmAccessForContact?: () => void;
  getQePermissions?: () => void;
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
  firstName: "",
  lastName: "",
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

const findRelatedGroup: any[] = [
  { title: "Applications", list: "application", expression: "student.contact.id" },
  { title: "Audits", list: "audit", expression: "entityIdentifier == Contact and entityId" },
  { title: "Certificates", list: "certificate", expression: "student.contact.id" },
  { title: "Classes enrolled", list: Classes.path, expression: "enrolments.student.contact.id" },
  { title: "Classes taught", list: Classes.path, expression: "tutorRoles.tutor.contact.id" },
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
  { title: "Transactions", list: "transaction", expression: "contact.id" },
  { title: "Waiting lists", list: "waitingList", expression: "student.contact.id" }
];

const secondaryColumnCondition = row => row.birthDate || "Birthday not specified";

const manualLink = getManualLink("contacts");

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

const nestedEditFields = {
  SendMessage: props => <SendMessageEditView {...props} />
};

export const getDisabledSubmitCondition = (isVerifyingUSI, usiVerificationResult): boolean => (
  isVerifyingUSI || (usiVerificationResult && usiVerificationResult.verifyStatus === "Invalid format")
);

const SearchMenuItem = React.memo<any>(({ content, data }) => (data.prefix ? (
  <div className="d-flex align-items-baseline">
    {content}
    <Typography className="ml-0-5" variant="caption" color="textSecondary">
      (
      {data.prefix}
      )
    </Typography>
  </div>
) : (
  content
)));

const searchMenuItemsRenderer = (content, data, search) => (
  data.prefix ? <SearchMenuItem content={content} data={data} search={search} /> : content
);

const today = new Date();
today.setHours(0, 0, 0, 0);

const setRowClasses = row => {
  const dateFinished = row["tutor.dateFinished"];

  if (dateFinished && isBefore(new Date(dateFinished), today)) {
    return "op05";
  }

  return undefined;
};

const Contacts: React.FC<ContactsProps> = props => {
  const {
    onDelete,
    getFilters,
    clearListState,
    onInit,
    onSave,
    getTags,
    getContactRecord,
    getCountries,
    getLanguages,
    getContactsRelationTypes,
    getContactsConcessionTypes,
    getTaxTypes,
    getDefaultTerms,
    getGenerateAccessForContact,
    getConfirmAccessForContact,
    getQePermissions,
    getContactRelationTypes,
    relationTypes,
    selection,
    isVerifyingUSI,
    usiVerificationResult,
    onCreate,
    getPaymentTypes
  } = props;

  const [findRelatedItems, setFindRelatedItems] = useState([]);

  useEffect(() => {
    if (relationTypes && selection.length) {
      const relationTypesItem: FindRelatedItem = {
        title: "Contacts related as...",
        items: [{ title: "All related contacts", list: "contact", expression: "allRelatedContacts.id" }]
      };

      relationTypes.forEach(t => {
        if (t.relationName === t.reverseRelationName) {
          const allSelected = selection.join(", ");

          relationTypesItem.items.push({
            title: t.relationName,
            list: "contact",
            // eslint-disable-next-line max-len
            customExpression: `(fromRelationType.id = "${t.id}" and fromRelatedContacts.id in (${allSelected})) or (toRelationType.id = "${t.id}" and toRelatedContacts.id in (${allSelected}))`
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
    getGenerateAccessForContact();
    getConfirmAccessForContact();
    getQePermissions();
    getContactRelationTypes();

    return () => {
      clearListState();
    };
  }, []);

  const onContactSave = useCallback((id: string, contact) => {
    const contactModel = { ...contact };
    const { student, relations } = contactModel;

    if (student) delete contactModel.student.education;

    contactModel.relations = formatRelationsBeforeSave(relations);

    if (contactModel.isCompany) delete contactModel.firstName;

    onSave(id, contactModel);
  }, []);

  const onContactCreate = useCallback(contact => {
    const contactModel = { ...contact };
    const { student, relations } = contactModel;

    if (student) delete contactModel.student.education;

    contactModel.relations = formatRelationsBeforeSave(relations);

    if (contactModel.isCompany) delete contactModel.firstName;

    onCreate(contactModel);
  }, []);

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
        nameCondition: getContactFullName,
        disabledSubmitCondition: getDisabledSubmitCondition(isVerifyingUSI, usiVerificationResult),
        asyncValidate: notesAsyncValidate,
        asyncBlurFields: ["notes[].message"]
      }}
      EditViewContent={ContactEditView}
      nestedEditFields={nestedEditFields}
      getEditRecord={getContactRecord}
      rootEntity="Contact"
      onCreate={onContactCreate}
      onInit={onInit}
      onSave={onContactSave}
      onDelete={onDelete}
      findRelated={findRelatedItems}
      filterGroupsInitial={filterGroups}
      CogwheelAdornment={ContactCogWheel}
      searchMenuItemsRenderer={searchMenuItemsRenderer}
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
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
  getContactRecord: (id: number) => dispatch(getContact(id)),
  onDelete: (id: number) => dispatch(deleteContact(id)),
  onSave: (id: string, contact: Contact) => dispatch(updateContact(id, contact)),
  onCreate: (contact: Contact) => dispatch(createContact(contact)),
  clearListState: () => dispatch(clearListState()),
  getGenerateAccessForContact: () => dispatch(checkPermissions({ path: "/a/v1/list/option/payroll?entity=Contact", method: "PUT" })),
  getConfirmAccessForContact: () => dispatch(
    checkPermissions({
      path: "/a/v1/list/option/payroll?entity=Contact&bulkConfirmTutorWages=true",
      method: "POST"
    })
  ),
  getQePermissions: () => {
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Enrolment", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=PriorLearning", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Outcome", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Certificate", method: "GET" }));
  },
  getPaymentTypes: () => dispatch(getPaymentTypes())
});

const mapStateToProps = (state: State) => ({
  relationTypes: state.preferences.contactRelationTypes,
  selection: state.list.selection,
  isVerifyingUSI: state.contacts.verifyingUSI,
  usiVerificationResult: state.contacts.usiVerificationResult
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Contacts);
