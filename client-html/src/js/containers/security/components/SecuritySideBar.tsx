import { User } from '@api/model';
import { library } from '@fortawesome/fontawesome-svg-core';
import { faEnvelopeOpenText } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import ScreenLockPortrait from '@mui/icons-material/ScreenLockPortrait';
import MenuItem from '@mui/material/MenuItem';
import Typography from '@mui/material/Typography';
import $t from '@t';
import React, { useMemo } from 'react';
import { connect } from 'react-redux';
import { NavLink } from 'react-router-dom';
import CollapseMenuList from '../../../common/components/layout/side-bar-list/CollapseSideBarList';
import { LICENSE_ACCESS_CONTROL_KEY } from '../../../constants/Config';
import { SidebarSharedProps } from '../../../model/common/sidebar';
import { State } from '../../../reducers/state';

library.add(faEnvelopeOpenText);

const UserIconRenderer = ({ item, ...rest }: { item: User }) => {
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

    const { location: { pathname } } = history;

    return (
      <div className={`mt-2 ${className}`}>
        <NavLink to="/security/settings" className="link">
          <MenuItem disableGutters className="heading" selected={pathname === "/security/settings"}>
            <Typography className="heading pl-3 pr-2" variant="h6" color="primary">
              {$t('settings')}
            </Typography>
          </MenuItem>
        </NavLink>

        <NavLink to="/security/api-tokens" className="link">
          <MenuItem disableGutters className="heading mt-1" selected={pathname === "/security/api-tokens"}>
            <Typography className="heading pl-3 pr-2" variant="h6" color="primary">
              {$t('api_tokens')}
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
