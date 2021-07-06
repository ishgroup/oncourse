import * as React from "react";
import { connect } from "react-redux";
import { withStyles } from "@material-ui/core/styles";
import {
 List, ListItem, ListSubheader, ListItemText
} from "@material-ui/core";
import Button from "../../../../common/components/buttons/Button";
import { State } from "../../../../reducers/state";
import * as Model from "../../../../model/preferences/Licences";
import AppBar from "../../../../common/components/layout/AppBar";

const styles: any = () => ({
  disabledList: {
    color: "#ccc"
  },
  listItem: {
    paddingBottom: 0,
    "&:nth-child(2)": {
      paddingTop: 0
    }
  }
});

const LicenseNames = {
  [Model.LicenseSMS.uniqueKey]: "SMS",
  [Model.LicenseCreditCard.uniqueKey]: "Credit card",
  [Model.LicenseAccessControl.uniqueKey]: "Access control",
  [Model.LicensePayroll.uniqueKey]: "Payroll",
  [Model.LicenseVoucher.uniqueKey]: "Vouchers",
  [Model.LicenseAttendance.uniqueKey]: "Attendance",
  [Model.LicenseMembership.uniqueKey]: "Membership",
  [Model.LicenseScripting.uniqueKey]: "Scripting",
  [Model.LicenseFeeHelpExport.uniqueKey]: "VET Fee Help exports",
  [Model.LicenseFundingContract.uniqueKey]: "Funding contract",
  [Model.LicenseGravatar.uniqueKey]: "Gravatar",
};

class Licences extends React.Component<any, any> {
  listItem(item, active) {
    const { classes } = this.props;

    return (
      <ListItem key={item} className={classes.listItem}>
        {active ? (
          <ListItemText primary={LicenseNames[item] || item} />
        ) : (
          <ListItemText secondary={LicenseNames[item] || item} />
        )}
      </ListItem>
    );
  }

  render() {
    const { licences } = this.props;

    const inactive = licences
      && Object.keys(licences)
        .filter(item => licences[item] === "false")
        .map(item => this.listItem(item, false));

    return (
      <div>
        <AppBar title="Licences" />

        <List
          subheader={(
            <ListSubheader disableSticky className="heading mb-2">
              Enabled Features
            </ListSubheader>
          )}
        >
          {licences
            && Object.keys(licences)
              .filter(item => licences[item] === "true")
              .map(item => this.listItem(item, true))}
        </List>

        {inactive && Boolean(inactive.length) && (
          <List
            className="mt-1"
            subheader={(
              <ListSubheader disableSticky className="heading">
                Inactive Features
                <a href="http://www.ish.com.au/oncourse/signup" target="_blank" className="link">
                  <Button
                    color="primary"
                    text="Upgrade now"
                    size="small"
                    className="m-1"
                    rootClasses="licencesUpgradeButton"
                  />
                </a>
              </ListSubheader>
            )}
          >
            {inactive}
          </List>
        )}
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  licences: state.preferences.licences
});

const Styled = withStyles(styles)(Licences);

export default connect(mapStateToProps, null)(Styled as any);
