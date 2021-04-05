/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useMemo, useState, useEffect, useCallback,
} from "react";
import { connect } from "react-redux";
import { change } from "redux-form";
import Grid from "@material-ui/core/Grid";
import LockOutlined from "@material-ui/icons/LockOutlined";
import makeStyles from "@material-ui/core/styles/makeStyles";
import { EmailTemplateApi } from "@api/model";
import Typography from "@material-ui/core/Typography";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../../../reducers/state";
import { ADMIN_EMAIL_KEY } from "../../../../../../constants/Config";
import { DefaultHttpService } from "../../../../../../common/services/HttpService";
import { Switch } from "../../../../../../common/components/form/form-fields/Switch";

const useStyles = makeStyles(() => ({
  itemIcon: {
    fontSize: "16px",
    position: "relative",
    bottom: "-3px"
  },
}));

const MessageCardContent = React.memo<any>(props => {
  const {
    name, emailTemplates, customPreferencesFields, field, dispatch, form, renderVariables, options
  } = props;

  const [messageVariables, setMessageVariables] = useState([]);

  const classes = useStyles();

  const messageTemplateItems = useMemo(
    () => (emailTemplates
      ? emailTemplates.filter(t => t.keyCode).map(t => ({
         value: t.keyCode, label: t.name, hasIcon: t.hasIcon, id: t.id,
        }))
      : []), [emailTemplates],
  );

  const emailTemplatesForRender = useCallback(item => (
    item.hasIcon ? (
      <span>
        {item.label}
        {' '}
        <LockOutlined className={classes.itemIcon} />
      </span>
    ) : item.label ), []);

  const getEmailTemplate = async (id: number) => {
    const emailTemplateService = new EmailTemplateApi(new DefaultHttpService());
    let emailTemplate;

    try {
      emailTemplate = await emailTemplateService.get(id);
      emailTemplate && setMessageVariables(emailTemplate.variables);
    } catch (e) {
      console.warn(e);
    }

    return emailTemplate;
  };

  useEffect(() => {
    if (field && field.template) {
      const templateOption = options?.find(o => o.name === field.template);

      if (templateOption) {
        dispatch(change(form, `${name}.template`, templateOption.value ));
      }

      const currentEmailTemplate = emailTemplates.find(t => t.keyCode === field.template);
      if (currentEmailTemplate) {
        getEmailTemplate(currentEmailTemplate.id);
      }
    }
  }, [field, options]);

  const changeEmailTemplate = async item => {
    const emailTemplate = await getEmailTemplate(item.id);
    emailTemplate && emailTemplate.variables && emailTemplate.variables.forEach(e => {
      if (e.type === "Checkbox") {
        dispatch(change(form, `${name}.${e.name}`, false));
      }
    });
  };

  const onTypeChange = (e, checked) => {
    const updated = { ...field };
    if (checked) {
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

  return (
    <Grid container className="mt-2">
      <Grid item xs={12} className="centeredFlex">
        <Typography variant="caption" color="textSecondary">
          Plain message
        </Typography>
        <Switch
          checked={!templateMessage}
          onChange={onTypeChange}
        />
      </Grid>

      {templateMessage ? (
        <Grid item xs={6}>
          <FormField
            type="select"
            name={`${name}.template`}
            label="Template"
            items={messageTemplateItems}
            selectLabelCondition={emailTemplatesForRender}
            onInnerValueChange={changeEmailTemplate}
            required
          />
        </Grid>
      ) : (
        <>
          <Grid item xs={12}>
            <FormField
              type="text"
              name={`${name}.to`}
              label="To"
              fullWidth
            />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="text"
              name={`${name}.subject`}
              label="Subject"
              fullWidth
            />
          </Grid>
          <Grid item xs={12}>
            <FormField
              type="text"
              name={`${name}.content`}
              label="Content"
              fullWidth
            />
          </Grid>
        </>
      )}

      <Grid item xs={12}>
        <FormField
          type="text"
          name={`${name}.from`}
          label="From"
          placeholder={customPreferencesFields && customPreferencesFields[ADMIN_EMAIL_KEY] || 'No value'}
          fullWidth
        />
      </Grid>

      <Grid item xs={12}>
        <FormField
          type="text"
          name={`${name}.cc`}
          label="cc"
          fullWidth
        />
      </Grid>

      <Grid item xs={12}>
        <FormField
          type="text"
          name={`${name}.bcc`}
          label="bcc"
          fullWidth
        />
      </Grid>

      <Grid item xs={12}>
        <FormField
          type="text"
          name={`${name}.key`}
          label="Key"
          fullWidth
        />
      </Grid>

      <Grid item xs={12}>
        <FormField
          type="text"
          name={`${name}.keyCollision`}
          label="Key collision"
          fullWidth
        />
      </Grid>

      {renderVariables(messageVariables, name)}
    </Grid>
  );
});

const mapStateToProps = (state: State) => ({
  customPreferencesFields: state.preferences.customFields,
});

export default connect<any, any, any>(mapStateToProps)(MessageCardContent);
