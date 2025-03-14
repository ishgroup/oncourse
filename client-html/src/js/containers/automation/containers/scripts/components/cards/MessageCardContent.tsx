/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { CustomFieldType } from '@api/model';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { Switch } from 'ish-ui';
import React, { useMemo, } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change, getFormValues } from 'redux-form';
import { IAction } from '../../../../../../common/actions/IshAction';
import instantFetchErrorHandler from '../../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { validateEmail } from '../../../../../../common/utils/validation';
import { ADMIN_EMAIL_KEY } from '../../../../../../constants/Config';
import { COMMON_PLACEHOLDER } from '../../../../../../constants/Forms';
import { CatalogItemType } from '../../../../../../model/common/Catalog';
import { ScriptComponent, ScriptExtended } from '../../../../../../model/scripts';
import { State } from '../../../../../../reducers/state';
import { renderAutomationItems } from '../../../../utils';
import EmailTemplateService from '../../../email-templates/services/EmailTemplateService';

interface Props {
  name: string;
  emailTemplates: CatalogItemType[];
  field: ScriptComponent;
  dispatch: Dispatch<IAction>
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
         value: t.keyCode, label: t.title, hasIcon: t.keyCode.startsWith("ish."), id: t.id,
        }))
      : []), [emailTemplates],
  );

  const templateOptionIndex = useMemo(() => values?.options?.findIndex(o => o.name === field.template), [values?.options, field.template]);

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
        label={$t('from')}
        placeholder={(customPreferencesFields && customPreferencesFields[ADMIN_EMAIL_KEY]) || COMMON_PLACEHOLDER}
        disabled={disabled}
        validate={validateEmail}
              />
    </Grid>
  );

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="mt-2 pb-2">
      <Grid item xs={12} className="centeredFlex">
        <Typography variant="caption" color="textSecondary">
          {$t('use_template')}
        </Typography>
        <Switch
          checked={templateMessage}
          onChange={onTypeChange}
          disabled={disabled}
        />
      </Grid>

      {templateMessage ? (
        <>
          <Grid item xs={12}>
            <FormField
              type="select"
              name={typeof templateOptionIndex === "number" && templateOptionIndex !== -1
                ? `options[${templateOptionIndex}].value`
                : `${name}.template`}
              label={$t('template')}
              items={messageTemplateItems}
              itemRenderer={renderAutomationItems}
              valueRenderer={renderAutomationItems}
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
              label={$t('To')}
              disabled={disabled}
                          />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="text"
              name={`${name}.cc`}
              disabled={disabled}
              label={$t('cc')}
                          />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="text"
              name={`${name}.bcc`}
              disabled={disabled}
              label={$t('bcc')}
                          />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="text"
              name={`${name}.subject`}
              label={$t('subject')}
              disabled={disabled}
                          />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="multilineText"
              name={`${name}.content`}
              label={$t('content')}
              disabled={disabled}
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
            label={$t('key')}
                      />
        </Grid>
      )}

      {field.hasOwnProperty("keyCollision") && (
        <Grid item xs={12}>
          <FormField
            type="text"
            name={`${name}.keyCollision`}
            disabled={disabled}
            label={$t('key_collision')}
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
