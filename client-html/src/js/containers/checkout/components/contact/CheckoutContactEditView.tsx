/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import {
  reduxForm, getFormSyncErrors, getFormValues, InjectedFormProps
} from "redux-form";
import Button from "@mui/material/Button";
import { Contact } from "@api/model";
import { notesAsyncValidate } from "../../../../common/components/form/notes/utils";
import { State } from "../../../../reducers/state";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import ContactEditView from "../../../entities/contacts/components/ContactEditView";
import { formatRelationsBeforeSave, getDisabledSubmitCondition } from "../../../entities/contacts/Contacts";
import { getContactName } from "../../../entities/contacts/utils";
import { checkoutCreateContact, checkoutUpdateContact } from "../../actions/checkoutContact";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import CheckoutAppBar from "../CheckoutAppBar";
import FormSubmitButton from "../../../../common/components/form/FormSubmitButton";
import { onSubmitFail } from "../../../../common/utils/highlightFormClassErrors";

export const CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME = "CheckoutContactEditForm";

interface Props extends Partial<InjectedFormProps> {
  creatingNew?: boolean;
  values?: any;
  syncErrors?: any;
  dispatch?: Dispatch<any>;
  showConfirm: ShowConfirmCaller;
  openNestedEditView: any;
  onSave: (record, dispatch, formProps) => void;
  onContactSave?: (id: string, contact: Contact) => void;
  onContactCreate?: (contact: Contact) => void;
  onClose?: () => void;
  isVerifyingUSI?: boolean;
  usiVerificationResult?: any;
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
    openNestedEditView,
    onSave,
    onContactSave,
    onContactCreate,
    isVerifyingUSI,
    usiVerificationResult
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
    <>
      <form className="flex-column w-100" onSubmit={handleSubmit(onSubmit)} autoComplete="off">
        <CustomAppBar>
          <CheckoutAppBar title={values && getContactName(values)} />
          <div>
            {creatingNew && (
              <Button onClick={onClose} className="closeAppBarButton">
                Close
              </Button>
            )}
            <FormSubmitButton
              disabled={invalid || (!creatingNew && !dirty) || Boolean(asyncValidating) || disabledSubmitCondition}
              invalid={invalid}
            />
          </div>
        </CustomAppBar>
        <div className="appBarContainer overflow-hidden">
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
            openNestedEditView={openNestedEditView}
            toogleFullScreenEditView={() => {}}
          />
        </div>
      </form>
    </>
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
  asyncBlurFields: ["notes[].message"],
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(QuickEnrolContactEditViewForm));

export default CheckoutContactEditView as any;
