/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useMemo, useCallback,
} from "react";
import { connect } from "react-redux";
import { change, getFormValues } from "redux-form";
import Grid from "@material-ui/core/Grid";
import LockOutlined from "@material-ui/icons/LockOutlined";
import Typography from "@material-ui/core/Typography";
import { CustomFieldType } from "@api/model";
import { Dispatch } from "redux";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../../../reducers/state";
import { ADMIN_EMAIL_KEY } from "../../../../../../constants/Config";
import { Switch } from "../../../../../../common/components/form/form-fields/Switch";
import instantFetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import EmailTemplateService from "../../../email-templates/services/EmailTemplateService";
import { ScriptComponent, ScriptExtended } from "../../../../../../model/scripts";

interface Props {
  name: string;
  emailTemplates: any;
  field: ScriptComponent;
  dispatch: Dispatch;
  form: string;
  renderVariables: any;
  disabled: boolean;
  customPreferencesFields?: CustomFieldType;
  values?: ScriptExtended;
}

const MessageCardContent = React.memo<Props>(props => {
  const {
    name, emailTemplates, customPreferencesFields, field, dispatch, form, renderVariables, disabled, values
  } = props;

  const messageTemplateItems = useMemo(
    () => (emailTemplates
      ? emailTemplates.filter(t => t.keyCode).map(t => ({
         value: t.keyCode, label: t.name, hasIcon: t.hasIcon, id: t.id,
        }))
      : []), [emailTemplates],
  );

  const templateOptionIndex = useMemo(() => values?.options?.findIndex(o => o.name === field.template), [values?.options, field.template]);

  const emailTemplatesForRender = useCallback(item => (
    item.hasIcon ? (
      <span>
        {item.label}
        {' '}
        <LockOutlined className="selectItmeIcon" />
      </span>
    ) : item.label ), []);

  const getEmailTemplate = async (id: number) => EmailTemplateService
    .get(id)
    .catch(e => instantFetchErrorHandler(dispatch, e));

  const changeEmailTemplate = async item => {
    const emailTemplate = await getEmailTemplate(item.id);
    if (emailTemplate) {
      emailTemplate.variables.forEach(e => {
        if (e.type === "Checkbox") {
          dispatch(change(form, `${name}.${e.name}`, false));
        }
      });
      dispatch(change(form, `${name}.templateEntity`, emailTemplate));
    }
  };

  const onTypeChange = (e, checked) => {
    const updated = { ...field };
    if (!checked) {
      delete updated["template"];
      updated["to"] = null;
      updated["subject"] = null;
      updated["content"] = null;
    } else {
      updated["template"] = null;
      delete updated["to"];
      delete updated["subject"];
      delete updated["content"];
    }
    dispatch(change(form, `${name}`, updated));
  };

  const templateMessage = field.hasOwnProperty("template");

  const FromField = (
    <Grid item xs={12}>
      <FormField
        type="text"
        name={`${name}.from`}
        label="From"
        placeholder={customPreferencesFields && customPreferencesFields[ADMIN_EMAIL_KEY] || 'No value'}
        disabled={disabled}
        fullWidth
      />
    </Grid>
  );

  return (
    <Grid container className="mt-2">
      <Grid item xs={12} className="centeredFlex">
        <Typography variant="caption" color="textSecondary">
          Use template
        </Typography>
        <Switch
          checked={templateMessage}
          onChange={onTypeChange}
          disabled={disabled}
        />
      </Grid>

      {templateMessage ? (
        <>
          <Grid item xs={6}>
            <FormField
              type="select"
              name={typeof templateOptionIndex === "number" && templateOptionIndex !== -1
                ? `options[${templateOptionIndex}].value`
                : `${name}.template`
              }
              label="Template"
              items={messageTemplateItems}
              selectLabelCondition={emailTemplatesForRender}
              onInnerValueChange={changeEmailTemplate}
              disabled={disabled}
              required
            />
          </Grid>
          {FromField}
        </>
      ) : (
        <>
          {FromField}
          <Grid item xs={12}>
            <FormField
              type="text"
              name={`${name}.to`}
              label="To"
              disabled={disabled}
              fullWidth
            />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="text"
              name={`${name}.cc`}
              disabled={disabled}
              label="cc"
              fullWidth
            />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="text"
              name={`${name}.bcc`}
              disabled={disabled}
              label="bcc"
              fullWidth
            />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="text"
              name={`${name}.subject`}
              label="Subject"
              disabled={disabled}
              fullWidth
            />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="multilineText"
              name={`${name}.content`}
              label="Content"
              disabled={disabled}
              fullWidth
            />
          </Grid>
        </>
      )}

      {field.hasOwnProperty("key") && (
        <Grid item xs={12}>
          <FormField
            type="text"
            name={`${name}.key`}
            disabled={disabled}
            label="Key"
            fullWidth
          />
        </Grid>
      )}

      {field.hasOwnProperty("keyCollision") && (
        <Grid item xs={12}>
          <FormField
            type="text"
            name={`${name}.keyCollision`}
            disabled={disabled}
            label="Key collision"
            fullWidth
          />
        </Grid>
      )}

      {field.templateEntity ? renderVariables(field.templateEntity.variables, name, disabled) : null}
    </Grid>
  );
});

const mapStateToProps = (state: State, ownProps) => ({
  customPreferencesFields: state.preferences.customFields,
  values: getFormValues(ownProps.form)(state)
});

export default connect<any, any, any>(mapStateToProps)(MessageCardContent);
