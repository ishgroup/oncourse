/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { alpha } from '@mui/material/styles';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { isEmpty } from 'es-toolkit/compat';
import { makeAppStyles } from 'ish-ui';
import * as React from 'react';
import { useEffect, useMemo } from 'react';
import { connect } from 'react-redux';
import { Form, getFormValues, initialize, reduxForm } from 'redux-form';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import FormField from '../../../../../common/components/form/formFields/FormField';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { onSubmitFail } from '../../../../../common/utils/highlightFormErrors';
import * as Model from '../../../../../model/preferences/Assistant';
import { FormModelSchema } from '../../../../../model/preferences/FormModelShema';
import { State } from '../../../../../reducers/state';
import { PREFERENCES_AUDITS_LINK } from '../../../constants';

export const ASSISTANT_FORM_NAME = "AssistantForm";

const useStyles = makeAppStyles()(theme => ({
  row: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: theme.spacing(2),
    padding: theme.spacing(1.5, 0),
    borderTop: `1px solid ${theme.palette.divider}`
  },
  title: {
    display: "block",
    fontWeight: 600
  },
  badge: {
    padding: theme.spacing(0.25, 1),
    borderRadius: theme.spacing(0.5),
    fontSize: "12px",
    fontWeight: 600,
    color: theme.palette.text.primary,
    backgroundColor: alpha(theme.palette.warning.main, 0.3)
  }
}));

interface SettingRowProps {
  name: string;
  title: string;
  description: string;
  badge?: React.ReactNode;
}

const SettingRow = ({
 name, title, description, badge
}: SettingRowProps) => {
  const { classes } = useStyles();

  return (
    <div className={classes.row}>
      <div>
        {/* FormSwitch renders its input with id "input-<name>", the label gives the switch its name */}
        <Typography component="label" htmlFor={`input-${name}`} variant="body2" className={classes.title}>
          {title}
        </Typography>
        <Typography variant="body2" color="textSecondary">
          {description}
        </Typography>
      </div>
      <div className="centeredFlex">
        {badge}
        {/* not debounced, so the form value, the descriptions and the save button follow the switch at once */}
        <FormField type="switch" name={name} color="primary" stringValue debounced={false} />
      </div>
    </div>
  );
};

// The server reads a missing setting as its default. The form starts from the same values,
// so the switches, the descriptions and the saved values agree.
const withDefaultValues = (formData, formModel: FormModelSchema) => {
  const defaults = {
    [formModel.AssistantEnabled.uniqueKey]: "false",
    [formModel.AssistantAdminOnly.uniqueKey]: "true",
    [formModel.AssistantStudentDataAllowed.uniqueKey]: "false"
  };

  const values = { ...formData };

  Object.keys(defaults).forEach(key => {
    const value = formData[key];
    values[key] = value === undefined || value === null || value === "" ? defaults[key] : String(value);
  });

  return values;
};

const AssistantFormBase = props => {
  const {
    handleSubmit, onSave, dirty, data, invalid, form, formRoleName, formData, initialized, dispatch, values, formatModel
  } = props;

  const { classes } = useStyles();

  const formModel: FormModelSchema = useMemo(() => formatModel(Model), []);

  useEffect(() => {
    // Initializing form with values
    if (!isEmpty(formData) && !initialized) {
      dispatch(initialize(ASSISTANT_FORM_NAME, withDefaultValues(formData, formModel)));
    }
  }, [formData, initialized]);

  const adminOnly = String(values?.[formModel.AssistantAdminOnly.uniqueKey]) !== "false";

  return (
    <Form className="container" onSubmit={handleSubmit(onSave)} role={formRoleName}>
      <RouteChangeConfirm form={form} when={dirty} />

      <AppBarContainer
        values={data}
        getAuditsUrl={PREFERENCES_AUDITS_LINK}
        disabled={!dirty}
        invalid={invalid}
        title={$t('oncourse_assistant')}
        disableInteraction
        createdOn={v => v.created}
        modifiedOn={v => v.modified}
      >
        <SettingRow
          name={formModel.AssistantEnabled.uniqueKey}
          title={$t('enable_oncourse_assistant')}
          description={$t('shows_the_ask_oncourse_ai_button_to_permitted_user')}
        />
        <SettingRow
          name={formModel.AssistantAdminOnly.uniqueKey}
          title={$t('available_to')}
          description={adminOnly ? $t('users_with_the_admin_toggle_on') : $t('all_users_within_their_existing_permissions')}
          badge={<span className={classes.badge}>{$t('pilot')}</span>}
        />
        <SettingRow
          name={formModel.AssistantStudentDataAllowed.uniqueKey}
          title={$t('allow_student_data_to_be_sent_to_the_ai_model')}
          description={$t('lets_the_assistant_include_student_data_in_request')}
        />
      </AppBarContainer>
    </Form>
  );
};

const mapStateToProps = (state: State) => ({
  values: getFormValues(ASSISTANT_FORM_NAME)(state)
});

export default reduxForm({
  form: ASSISTANT_FORM_NAME,
  onSubmitFail
})(connect<any, any, any>(mapStateToProps)(AssistantFormBase));
