/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {useMemo, useState, useEffect} from "react";
import {connect} from "react-redux";
import {change} from "redux-form";
import Grid from "@material-ui/core/Grid";
import LockOutlined from "@material-ui/icons/LockOutlined";
import makeStyles from "@material-ui/core/styles/makeStyles";
import {EmailTemplateApi} from "@api/model";

import FormField from "../../../../../../common/components/form/form-fields/FormField";
import {AppTheme} from "../../../../../../model/common/Theme";
import {State} from "../../../../../../reducers/state";
import {ADMIN_EMAIL_KEY} from "../../../../../../constants/Config";
import {DefaultHttpService} from "../../../../../../common/services/HttpService";
import {SCRIPT_EDIT_VIEW_FORM_NAME} from "../../constants";

const useStyles = makeStyles((theme: AppTheme) => ({
  selectItemWrapper: {
    display: "flex",
    alignItems: "center",
  },
  itemIcon: {
    fontSize: "16px",
    marginLeft: theme.spacing(1)
  }
}));

const MessageCardContent = React.memo<any>(props => {
  const {
    name, emailTemplates, customPreferencesFields, field, dispatch, renderVariables
  } = props;

  const [emailVariables, setEmailVariables] = useState([]);

  const classes = useStyles();

  const emailTemplateItems = useMemo(
    () => (emailTemplates
      ? emailTemplates.filter(t => t.keyCode).map(t => ({ value: t.keyCode, label: t.name, hasIcon: t.hasIcon, id: t.id }))
      : []), [emailTemplates]);

  const emailTemplatesForRender = (item) => (
    item.hasIcon ? (
      <span className={classes.selectItemWrapper}>
        {item.label} <LockOutlined className={classes.itemIcon} />
      </span>
    ) : item.label )

  const getEmailTemplate = async (id: number) => {
    const emailTemplateService = new EmailTemplateApi(new DefaultHttpService());
    let emailTemplate;

    try {
      emailTemplate = await emailTemplateService.get(id);
      emailTemplate && setEmailVariables(emailTemplate.variables);
    } catch (e) {
      console.warn(e)
    }

    return emailTemplate;
  }

  useEffect(() => {
    if (field && field.template) {
      const currentEmailTemplate = emailTemplates.filter(t => t.keyCode === field.template);
      currentEmailTemplate.length && getEmailTemplate(currentEmailTemplate[0].id);
    }
  }, [field]);

  const changeEmailTemplate = async (item) => {
    const emailTemplate = await getEmailTemplate(item.id)

    emailTemplate.variables && emailTemplate.variables.forEach(e => {
      if (e.type === "Checkbox") {
        dispatch(change(SCRIPT_EDIT_VIEW_FORM_NAME, `${name}.${e.name}`, false));
      }
    })
  }

  return (
    <Grid container>
      <Grid item xs={12}>
        <FormField
          type="select"
          name={`${name}.template`}
          label="Template"
          items={emailTemplateItems}
          className="d-flex mt-2"
          selectLabelCondition={emailTemplatesForRender}
          onInnerValueChange={changeEmailTemplate}
          required
        />

        <Grid item xs={12}>
          <FormField
            type="text"
            name={`${name}.from`}
            label="From"
            placeholder={customPreferencesFields && customPreferencesFields[ADMIN_EMAIL_KEY] || 'No value'}
          />
        </Grid>

        {renderVariables(emailVariables, name)}

      </Grid>
    </Grid>
  );
});

const mapStateToProps = (state: State) => ({
  customPreferencesFields: state.preferences.customFields,
});

export default connect<any, any, any>(mapStateToProps, null)(MessageCardContent);
