import React, { useCallback, useMemo, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import { faEnvelopeOpenText } from "@fortawesome/free-solid-svg-icons";
import Typography from "@material-ui/core/Typography";
import { NavLink } from "react-router-dom";
import { MenuItem } from "@material-ui/core";
import ScreenLockPortrait from "@material-ui/icons/ScreenLockPortrait";
import { connect } from "react-redux";
import { User } from "@api/model";
import CollapseMenuList from "../../../common/components/layout/side-bar-list/CollapseSideBarList";
import { State } from "../../../reducers/state";
import { LICENSE_ACCESS_CONTROL_KEY } from "../../../constants/Config";
import { SidebarSharedProps } from "../../../model/common/sidebar";

library.add(faEnvelopeOpenText);

const UserIconRenderer: React.FC<{ item: User }> = ({ item, ...rest }) => {
  const icons = [];
  if (item.tfaEnabled) {
    icons.push(<ScreenLockPortrait {...rest} key={item.id + "tfa"} />);
  }
  if (item.inviteAgain) {
    icons.push(<FontAwesomeIcon fixedWidth icon="envelope-open-text" {...rest} key={item.id + "invite"} />);
  }
  return <span className="centeredFlex">{icons}</span>;
};

const SecuritySideBar = React.memo<any>(
  ({
    className, userRoles, users, hasLicense, search, history, activeFiltersConditions
  }) => {
    const [activeLink, setActiveLink] = useState(false);

    const isActiveLink = useCallback(
      match => {
        if (match && !activeLink) {
          setActiveLink(!activeLink);
          return true;
        }
        if (!match && activeLink) {
          setActiveLink(!activeLink);
          return true;
        }
        return false;
      },
      [activeLink]
    );

    const usersItems = useMemo(
      () =>
        users
        && users.map(({
 id, email, active, tfaEnabled, inviteAgain
}) => ({
          id,
          name: email || "No email",
          grayOut: !active,
          tfaEnabled,
          inviteAgain
        })),
      [users]
    );

    const usersRolesItems = useMemo(() => userRoles && userRoles.map(({ id, name }) => ({ id, name })), [userRoles]);

    const sharedProps = useMemo<SidebarSharedProps>(
      () => ({
     history, search, activeFiltersConditions, category: "Security"
    }),
      [history, search, activeFiltersConditions]
    );

    return (
      <div className={className}>
        <NavLink to="/security/settings" className="link" isActive={isActiveLink}>
          <MenuItem button disableGutters className="listHeadingPadding heading" selected={activeLink}>
            <Typography className="heading pr-2" variant="h6" color="primary">
              Settings
            </Typography>
          </MenuItem>
        </NavLink>

        <CollapseMenuList
          name="User roles"
          basePath="/security/userRoles/"
          plusIconPath={hasLicense ? "new" : undefined}
          data={usersRolesItems}
          sharedProps={sharedProps}
        />

        <CollapseMenuList
          name="Users"
          basePath="/security/users/"
          data={usersItems}
          plusIconPath="new"
          ItemIconRenderer={UserIconRenderer}
          sharedProps={sharedProps}
        />
      </div>
    );
  }
);

const mapStateToProps = (state: State) => ({
  userRoles: state.security.userRoles,
  users: state.security.users,
  hasLicense: state.userPreferences[LICENSE_ACCESS_CONTROL_KEY] === "true"
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(SecuritySideBar);
