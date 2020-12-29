/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import DeleteForever from "@material-ui/icons/DeleteForever";
import {
  FormControlLabel, FormGroup, Grid, Paper, Checkbox, withStyles
} from "@material-ui/core/";
import clsx from "clsx";
import { initialize, reduxForm } from "redux-form";
import { connect } from "react-redux";
import CustomButton from "../../../../common/components/buttons/Button";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { Switch } from "../../../../common/components/form/form-fields/Switch";
import AppBarActions from "../../../../common/components/form/AppBarActions";
import AppBarHelpMenu from "../../../../common/components/form/AppBarHelpMenu";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import SecurityLevelsTagsGroup from "./components/SecurityLevelsTagsGroup";

const data = {
  roles: [
    { value: "edit", name: "admin" },
    { value: "show", name: "accounts" },
    { value: "show", name: "enrolment" },
    { value: "hide", name: "SEO" },
    { value: "edit", name: "management" }
  ],
  access: [{ value: "edit", name: "students" }, { value: "show", name: "tutors" }],
  website: [{ value: "show", name: "all website users" }]
};

const styles = () => ({
  paperPadding: {
    padding: "26px"
  },
  topCustomSwitchMargin: {
    marginBottom: "30px"
  },
  securityLevelsTagsGroup: {
    marginBottom: "50px"
  }
});

class SecurityLevels extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("SecurityLevelsSection", { name: "All staff" }));
  }

  render() {
    const { classes, className } = this.props;

    return (
      <div className={className}>
        <CustomAppBar>
          <Grid container>
            <Grid item md={12} lg={12} xl={9} className="centeredFlex">
              <FormField
                type="headerText"
                name="name"
                placeholder="Name"
                margin="none"
                className={classes.HeaderTextField}
                listSpacing={false}
                required
              />

              <div className="flex-fill" />

              <AppBarActions
                actions={[
                  {
                    action: () => {},
                    icon: <DeleteForever />,

                    confirmText: "Form will be deleted permanently",
                    confirmButtonText: "DELETE"
                  }
                ]}
              />

              <AppBarHelpMenu
                auditsUrl="audits?entityName=Preference"
                manualUrl="https://www.ish.com.au/s/onCourse/doc/SNAPSHOT/manual/generalPrefs.html#generalPrefs-college"
              />

              <CustomButton
                text="Save"
                type="submit"
                size="small"
                variant="text"
                rootClasses="whiteAppBarButton"
                disabledClasses="whiteAppBarButtonDisabled"
              />
            </Grid>
          </Grid>
        </CustomAppBar>
        <FormControlLabel
          control={<Switch />}
          label="Active"
          classes={{
            root: "switchWrapper",
            label: "switchLabel"
          }}
          className={classes.topCustomSwitchMargin}
        />
        <Grid container>
          <Grid item xl={6} lg={6} md={6} xs={12}>
            <SecurityLevelsTagsGroup
              heading="User roles"
              description="User roles granted access to records with this security level:"
              childItems={data.roles}
              className={classes.securityLevelsTagsGroup}
            />
            <SecurityLevelsTagsGroup
              heading="Access in portal"
              description="User roles granted access to records with this security level:"
              childItems={data.access}
              className={classes.securityLevelsTagsGroup}
            />
            <SecurityLevelsTagsGroup
              heading="Website"
              description="User roles granted access to records with this security level:"
              childItems={data.website}
              className={classes.securityLevelsTagsGroup}
            />
          </Grid>

          <Grid item xl={3} lg={6} md={6} xs={12}>
            <Paper className={clsx(classes.paperPadding, classes.paperBottomMargin)}>
              <FormGroup>
                <FormControlLabel
                  control={<Checkbox color="primary" />}
                  label="tags"
                  className="mr-0 ml-0"
                />
                <FormControlLabel
                  control={<Checkbox color="primary" />}
                  label="documents"
                  className="mr-0 ml-0"
                />
                <FormControlLabel
                  control={<Checkbox color="primary" />}
                  label="notes"
                  className="mr-0 ml-0"
                />
              </FormGroup>
            </Paper>
          </Grid>
        </Grid>
      </div>
    );
  }
}

const SecurityLevelsSection = reduxForm({
  form: "SecurityLevelsSection"
})(
  connect<any, any, any>(
    null,
    null
  )(withStyles(styles)(SecurityLevels))
);

export default SecurityLevelsSection;
