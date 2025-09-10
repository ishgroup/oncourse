import { DataCollectionType } from '@api/model';
import { MenuItem } from '@mui/material';
import Menu from '@mui/material/Menu/Menu';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { getUserPreferences } from '../../../common/actions';
import CollapseMenuList from '../../../common/components/layout/side-bar-list/CollapseSideBarList';
import { LICENSE_ACCESS_CONTROL_KEY, SPECIAL_TYPES_DISPLAY_KEY } from '../../../constants/Config';
import { SidebarSharedProps } from '../../../model/common/sidebar';
import { State } from '../../../reducers/state';
import Avetmiss from '../containers/avetmiss/Avetmiss';
import ClassTypes from '../containers/class-types/ClassTypes';
import CourseTypes from '../containers/course-types/CourseTypes';
import CollectionForms from '../containers/data-collection-forms/CollectionFormContainer';
import CollectionRules from '../containers/data-collection-rules/CollectionRuleFormContainer';
import LDAP from '../containers/ldap/LDAP';
import Subjects from '../containers/subjects/Subjects';
import TutorRoleForm from '../containers/tutor-roles/TutorRoleFormContainer';
import routes from '../routes';

const formTypes = Object.keys(DataCollectionType).map(type => {
  const response = { type, displayName: type };

  if (type === "Survey") {
    response.displayName = "Student Feedback";
  }
  return response;
});

const DataCollectionTypesMenu = React.memo<{ anchorEl, history, onClose }>(({ anchorEl, history, onClose }) => {
  const handleMenuClick = useCallback(e => {
    history.push(`/preferences/collectionForms/new/${e.target.getAttribute("role")}/`);
    onClose();
  }, []);

  return (
    <Menu id="types-menu" anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={onClose}>
      {formTypes.map(val => (
        <MenuItem key={val.type} role={val.type} onClick={handleMenuClick}>
          {val.displayName}
        </MenuItem>
      ))}
    </Menu>
  );
});

const SideBar = React.memo<any>(
  ({
 search, history, match, hideAUSReporting, collectionForms, collectionRules, activeFiltersConditions, tutorRoles, accessLicense, accessTypes, onInit
}) => {
    const [anchorEl, setAnchorEl] = useState(null);
    useEffect(onInit, []);

    const dataCollectionTypesMenuOpen = useCallback(e => setAnchorEl(e.currentTarget), []);
    const dataCollectionTypesMenuClose = useCallback(() => setAnchorEl(null), []);

    const dataCollectionFormsLinkCondition = useCallback(
      i => `/preferences/collectionForms/edit/${i.type}/${i.id}`,
      []
    );
    const dataCollectionRulesLinkCondition = useCallback(i => `/preferences/collectionRules/edit/${i.id}`, []);
    const preferencesLinkCondition = useCallback(i => i.url, []);
    const tutorRolesLinkCondition = useCallback(i => `/preferences/tutorRoles/${i.id}`, []);

    const sharedProps = useMemo<SidebarSharedProps>(
      () => ({
       history, search, match, activeFiltersConditions, category: "Preferences"
      }),
      [history, search, activeFiltersConditions, match]
    );

    const preferencesItems = routes
      .filter(({ main }) => {
        switch (main) {
          case CollectionRules:
          case CollectionForms:
          case TutorRoleForm:
            return false;
          case LDAP:
            return accessLicense;
          case ClassTypes:
          case CourseTypes:
          case Subjects:
            return accessTypes;
          case Avetmiss:
            return !hideAUSReporting;
        }
        return true;
      })
      .map(item => ({ ...item, name: item.title }));

    return (
      <>
        <DataCollectionTypesMenu anchorEl={anchorEl} history={history} onClose={dataCollectionTypesMenuClose} />

        <CollapseMenuList
          name="Data Collection Forms"
          basePath="/preferences/collectionForms/"
          customPlusHandler={dataCollectionTypesMenuOpen}
          linkCondition={dataCollectionFormsLinkCondition}
          data={collectionForms}
          sharedProps={sharedProps}
          defaultCollapsed
        />

        <CollapseMenuList
          name="Data Collection Rules"
          basePath="/preferences/collectionRules/"
          plusIconPath="new"
          linkCondition={dataCollectionRulesLinkCondition}
          data={collectionRules}
          sharedProps={sharedProps}
          defaultCollapsed
        />

        <CollapseMenuList
          name="Tutor Pay Rates"
          basePath="/preferences/tutorRoles/"
          plusIconPath="new"
          linkCondition={tutorRolesLinkCondition}
          data={tutorRoles}
          sharedProps={sharedProps}
          defaultCollapsed
        />

        <CollapseMenuList
          name="Preferences"
          basePath="/preferences/"
          linkCondition={preferencesLinkCondition}
          data={preferencesItems}
          sharedProps={sharedProps}
        />
      </>
    );
  }
);

const mapStateToProps = (state: State) => ({
  collectionForms: state.preferences.dataCollectionForms,
  collectionRules: state.preferences.dataCollectionRules,
  tutorRoles: state.preferences.tutorRoles,
  accessLicense: state.userPreferences[LICENSE_ACCESS_CONTROL_KEY] === 'true',
  accessTypes: state.userPreferences[SPECIAL_TYPES_DISPLAY_KEY] === 'true',
  hideAUSReporting: state.location.countryCode !== 'AU'
});
 
const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(getUserPreferences([LICENSE_ACCESS_CONTROL_KEY]));
  },
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(SideBar);