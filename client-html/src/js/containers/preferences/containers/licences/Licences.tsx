import * as React from "react";
import { connect } from "react-redux";
import { withStyles } from "@mui/styles";
import {
  List, ListItem, ListSubheader, ListItemText
} from "@mui/material";
import Button from "@mui/material/Button";
import { State } from "../../../../reducers/state";
import * as Model from "../../../../model/preferences/Licences";
import AppBarContainer from "../../../../common/components/layout/AppBarContainer";

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
  [Model.LicenseAccessControl.uniqueKey]: "Access control",
  [Model.LicenseScripting.uniqueKey]: "Scripting",
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
        .filter(item => Object.keys(LicenseNames).includes(item) && licences[item] !== null
          && (licences[item].toLowerCase() === "false" || licences[item] === "0"))
        .map(item => this.listItem(item, false));

    return (
      <div>
        <AppBarContainer
          title="Licences"
          hideHelpMenu
          hideSubmitButton
          disableInteraction
        >
          <List
            subheader={(
              <ListSubheader disableSticky className="heading mb-2">
                Enabled Features
              </ListSubheader>
            )}
          >
            {licences
            && Object.keys(licences)
              .filter(item => Object.keys(LicenseNames).includes(item) && licences[item] !== null
                && (licences[item].toLowerCase() === "true" || Number(licences[item]) > 0))
              .map(item => this.listItem(item, true))}
          </List>

          {inactive && Boolean(inactive.length) && (
            <List
              className="mt-1"
              subheader={(
                <ListSubheader disableSticky className="heading">
                  Inactive Features
                  <a href="http://www.ish.com.au/oncourse/signup" target="_blank" className="link" rel="noreferrer">
                    <Button
                      color="primary"
                      size="small"
                      className="m-1 licencesUpgradeButton"
                    >
                      Upgrade now
                    </Button>
                  </a>
                </ListSubheader>
              )}
            >
              {inactive}
            </List>
          )}
        </AppBarContainer>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  licences: state.preferences.licences
});

const Styled = withStyles(styles)(Licences);

export default connect(mapStateToProps, null)(Styled as any);
