/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { List, ListItem, ListItemText, ListSubheader } from "@mui/material";
import Button from "@mui/material/Button";
import { withStyles } from "@mui/styles";
import * as React from "react";
import { connect } from "react-redux";
import AppBarContainer from "../../../../common/components/layout/AppBarContainer";
import * as Model from "../../../../model/preferences/Licences";
import { State } from "../../../../reducers/state";

const styles: any = () => ({
  disabledList: {
    color: "#ccc"
  },
  listItem: {
    paddingBottom: 0,
    paddingLeft: 0,
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
    const { licences, plugins, classes } = this.props;

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
              <ListSubheader disableSticky className="heading mb-2 pl-0">
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
                <ListSubheader disableSticky className="heading mb-2">
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

          {
            plugins && plugins["plugins.names"] && (
              <List
                className="mt-1"
                subheader={(
                  <ListSubheader disableSticky className="heading pl-0 mb-2">
                    Enabled Plugins
                  </ListSubheader>
                )}
              >
                {plugins["plugins.names"].split(",").map(item => {
                  const splitted = item.split("|");
                  return (
                    <ListItem key={item} className={classes.listItem}>
                      <ListItemText primary={`${splitted[0]} ${splitted[1]}`} />
                    </ListItem>
                  );
                })}
              </List>
            )
          }
        </AppBarContainer>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  licences: state.preferences.licences,
  plugins: state.preferences.plugins
});

const Styled = withStyles(styles)(Licences);

export default connect(mapStateToProps, null)(Styled as any);
