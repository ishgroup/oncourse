import React, { useCallback, useEffect, useMemo } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { withRouter } from "react-router";
import { getFormValues, initialize, reduxForm } from "redux-form";
import { EmailTemplate } from "@api/model";
import { setNextLocation, showConfirm } from "../../../../common/actions";
import { onSubmitFail } from "../../../../common/utils/highlightFormClassErrors";
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
  enabled: false,
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

  const validateTemplateCopyName = useCallback(name => (emailTemplates.some(t => t.name.trim() === name.trim())
    ? "Name must be unique"
    : undefined),
  [emailTemplates]);

  const validateNewTemplateName = useCallback(name => {
      if (isNew) {
        const matches = emailTemplates.filter(t => t.name.trim() === name.trim());
        return matches.length ? "Name must be unique" : undefined;
      }
      return undefined;
    },
    [emailTemplates, isNew]);

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
      validateTemplateCopyName={validateTemplateCopyName}
      validateNewTemplateName={validateNewTemplateName}
      {...rest}
    />
);
});

const mapStateToProps = (state: State) => ({
  values: getFormValues(EMAIL_TEMPLATES_FORM_NAME)(state),
  emailTemplates: state.automation.emailTemplate.emailTemplates,
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onCreate: (template: EmailTemplate) => dispatch(createEmailTemplate(template)),
  onUpdate: (template: EmailTemplate) => dispatch(updateEmailTemplate(template)),
  onUpdateInternal: (template: EmailTemplate) => dispatch(updateInternalEmailTemplate(template)),
  onDelete: (id: number) => dispatch(removeEmailTemplate(id)),
  openConfirm: (onConfirm: any, confirmMessage?: string) => dispatch(showConfirm(onConfirm, confirmMessage)),
  getEmailTemplate: (id: number) => dispatch(getEmailTemplate(id)),
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

export default reduxForm({
  form: EMAIL_TEMPLATES_FORM_NAME,
  onSubmitFail,
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(EmailTemplates)));
