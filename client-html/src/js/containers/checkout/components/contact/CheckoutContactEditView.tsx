/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { getFormSyncErrors, getFormValues, InjectedFormProps, reduxForm } from "redux-form";
import { Contact } from "@api/model";
import { notesAsyncValidate } from "../../../../common/components/form/notes/utils";
import { State } from "../../../../reducers/state";
import ContactEditView from "../../../entities/contacts/components/ContactEditView";
import { formatRelationsBeforeSave, getDisabledSubmitCondition } from "../../../entities/contacts/Contacts";
import { checkoutCreateContact, checkoutUpdateContact } from "../../actions/checkoutContact";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import { onSubmitFail } from "../../../../common/utils/highlightFormErrors";
import AppBarContainer from "../../../../common/components/layout/AppBarContainer";

export const CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME = "CheckoutContactEditForm";

interface Props extends Partial<InjectedFormProps> {
  creatingNew?: boolean;
  values?: any;
  syncErrors?: any;
  dispatch?: Dispatch<any>;
  showConfirm: ShowConfirmCaller;
  onSave: (record, dispatch, formProps) => void;
  onContactSave?: (id: string, contact: Contact) => void;
  onContactCreate?: (contact: Contact) => void;
  onClose?: () => void;
  isVerifyingUSI?: boolean;
  usiVerificationResult?: any;
  leftOffset?: number;
}

const QuickEnrolContactEditViewForm: React.FC<Props> = props => {
  const {
    values,
    asyncValidating,
    syncErrors,
    invalid,
    submitSucceeded,
    dispatch,
    dirty,
    handleSubmit,
    onClose,
    creatingNew,
    showConfirm,
    onSave,
    onContactSave,
    onContactCreate,
    isVerifyingUSI,
    usiVerificationResult,
    leftOffset
  } = props;

  const handleContactSave = React.useCallback(contact => {
    const contactModel = { ...contact };
    if (contactModel.student) delete contactModel.student.education;
    if (contactModel.isCompany) delete contactModel.firstName;

    contactModel.relations = formatRelationsBeforeSave(contactModel.relations);
    onContactSave(contact.id, contactModel);
  }, []);

  const handleContactCreate = React.useCallback(contact => {
    const contactModel = { ...contact };
    if (contactModel.student) delete contactModel.student.education;
    if (contactModel.isCompany) delete contactModel.firstName;

    contactModel.relations = formatRelationsBeforeSave(contactModel.relations);

    onContactCreate(contactModel);
  }, []);

  const onSubmit = useCallback((contact, dispatch, formProps) => {
    onSave(contact, dispatch, formProps);
    if (formProps.creatingNew) {
      handleContactCreate(contact);
    } else {
      handleContactSave(contact);
    }
  }, []);

  const disabledSubmitCondition = getDisabledSubmitCondition(isVerifyingUSI, usiVerificationResult);

  return (
    <form className="flex-column w-100" onSubmit={handleSubmit(onSubmit)} autoComplete="off">
      <AppBarContainer
        noTitle
        disabled={invalid || (!creatingNew && !dirty) || Boolean(asyncValidating) || disabledSubmitCondition}
        invalid={invalid}
        onCloseClick={creatingNew ? onClose : null}
        containerClass="p-0"
      >
        <ContactEditView
          twoColumn
          asyncValidating={asyncValidating}
          syncErrors={syncErrors}
          submitSucceeded={submitSucceeded}
          manualLink=""
          invalid={invalid}
          form={CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME}
          rootEntity="Contact"
          isNew={creatingNew}
          values={values}
          dirty={dirty}
          dispatch={dispatch}
          showConfirm={showConfirm}
          leftOffset={leftOffset}
          toogleFullScreenEditView={() => {}}
        />
      </AppBarContainer>
    </form>
  );
};

const mapStateToProps = (state: State) => ({
  values: getFormValues(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME)(state),
  syncErrors: getFormSyncErrors(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME)(state),
  pending: state.fetch.pending,
  isVerifyingUSI: state.contacts.verifyingUSI,
  usiVerificationResult: state.contacts.usiVerificationResult
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onContactSave: (id: string, contact: Contact) => dispatch(checkoutUpdateContact(id, contact)),
  onContactCreate: (contact: Contact) => dispatch(checkoutCreateContact(contact))
});

const CheckoutContactEditView = reduxForm({
  form: CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME,
  asyncValidate: notesAsyncValidate,
  asyncChangeFields: ["notes[].message"],
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(QuickEnrolContactEditViewForm));

export default CheckoutContactEditView as any;