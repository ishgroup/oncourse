/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {useMemo} from "react";
import { connect } from "react-redux";
import Grid from "@material-ui/core/Grid";
import LockOutlined from "@material-ui/icons/LockOutlined";
import makeStyles from "@material-ui/core/styles/makeStyles";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import {AppTheme} from "../../../../../../model/common/Theme";
import {State} from "../../../../../../reducers/state";
import {ADMIN_EMAIL_KEY} from "../../../../../../constants/Config";

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
    name, emailTemplates, customPreferencesFields
  } = props;

  const classes = useStyles();

  const emailTemplateItems = useMemo(
    () => (emailTemplates
      ? emailTemplates.filter(t => t.keyCode).map(t => ({ value: t.keyCode, label: t.name, hasIcon: t.hasIcon }))
      : []), [emailTemplates]);

  const emailTemplatesForRender = (item) => (
    item.hasIcon ? (
      <span className={classes.selectItemWrapper}>
        {item.label} <LockOutlined className={classes.itemIcon} />
      </span>
    ) : item.label )

  return (
    <Grid container>
      <Grid item xs={12}>
        <FormField
          type="select"
          name={`${name}.template`}
          label="Email template"
          items={emailTemplateItems}
          className="d-flex mt-2"
          selectLabelCondition={emailTemplatesForRender}
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
      </Grid>
    </Grid>
  );
});

const mapStateToProps = (state: State) => ({
  customPreferencesFields: state.preferences.customFields,
});

export default connect<any, any, any>(mapStateToProps, null)(MessageCardContent);
