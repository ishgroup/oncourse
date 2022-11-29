/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useMemo } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { withRouter } from "react-router";
import { getFormSyncErrors, getFormValues, initialize, reduxForm } from "redux-form";
import { EmailTemplate } from "@api/model";
import { onSubmitFail } from "../../../../common/utils/highlightFormErrors";
import { usePrevious } from "../../../../common/utils/hooks";
import { State } from "../../../../reducers/state";
import {
 createEmailTemplate, getEmailTemplate, removeEmailTemplate, updateEmailTemplate, updateInternalEmailTemplate
} from "./actions";
import EmailTemplatesForm from "./containers/EmailTemplatesForm";

export const EMAIL_TEMPLATES_FORM_NAME = "EmailTemplatesForm";

const initialDefault: EmailTemplate = {
  name: null,
  keyCode: null,
  entity: null,
  subject: null,
  plainBody: "",
  body: null,
  status: "Installed but Disabled",
  variables: [],
  options: [],
  type: null
};

const EmailTemplates = React.memo<any>(props => {
  const {
    dispatch,
    getEmailTemplate,
    emailTemplates,
    match: {
      params: { id }
    },
    ...rest
  } = props;

  const prevId = usePrevious(id);

  const isNew = useMemo(() => id === "new", [id]);

  useEffect(() => {
    if (id && prevId !== id) {
      if (isNew) {
        dispatch(initialize(EMAIL_TEMPLATES_FORM_NAME, initialDefault));
      } else {
        getEmailTemplate(id);
      }
    }
  }, [id, prevId]);

  return (
    <EmailTemplatesForm
      dispatch={dispatch}
      isNew={isNew}
      emailTemplates={emailTemplates}
      {...rest}
    />
);
});

const mapStateToProps = (state: State) => ({
  values: getFormValues(EMAIL_TEMPLATES_FORM_NAME)(state),
  syncErrors: getFormSyncErrors(EMAIL_TEMPLATES_FORM_NAME)(state),
  emailTemplates: state.automation.emailTemplate.emailTemplates,
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onCreate: (template: EmailTemplate) => dispatch(createEmailTemplate(template)),
  onUpdate: (template: EmailTemplate) => dispatch(updateEmailTemplate(template)),
  onUpdateInternal: (template: EmailTemplate) => dispatch(updateInternalEmailTemplate(template)),
  onDelete: (id: number) => dispatch(removeEmailTemplate(id)),
  getEmailTemplate: (id: number) => dispatch(getEmailTemplate(id)),
});

export default reduxForm({
  form: EMAIL_TEMPLATES_FORM_NAME,
  onSubmitFail,
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(EmailTemplates)));
