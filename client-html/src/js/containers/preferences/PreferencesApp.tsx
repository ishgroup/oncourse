/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

/**
 * preferences app layout
 * */

import React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { SidebarWithSearch } from "../../common/components/layout/sidebar-with-search/SidebarWithSearch";
import { Categories } from "../../model/preferences";
import { State } from "../../reducers/state";
import {
  getColumnsWidth,
  getDataCollectionForms,
  getDataCollectionRules,
  getGradingTypes,
  getPreferences,
  getTutorRoles,
  updateColumnsWidth
} from "./actions";
import SideBar from "./components/SideBar";
import preferencesRoutes from "./routes";

const PreferencesApp = ({
   onInit,
   history,
   match,
   updateColumnsWidth,
   preferenceLeftColumnWidth
  }) => (
    <SidebarWithSearch
      leftColumnWidth={preferenceLeftColumnWidth}
      updateColumnsWidth={updateColumnsWidth}
      onInit={onInit}
      SideBar={SideBar}
      routes={preferencesRoutes}
      history={history}
      match={match}
    />
    );

const mapStateToProps = (state: State) => ({
  preferenceLeftColumnWidth: state.preferences.columnWidth && state.preferences.columnWidth.preferenceLeftColumnWidth
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(getDataCollectionRules());
    dispatch(getDataCollectionForms());
    dispatch(getColumnsWidth());
    dispatch(getTutorRoles());
    dispatch(getGradingTypes());
    dispatch(getPreferences(Categories.licences));
    dispatch(getPreferences(Categories.plugins));
    dispatch(getPreferences(Categories.classTypes));
  },
  updateColumnsWidth: (preferenceLeftColumnWidth: number) => dispatch(updateColumnsWidth({ preferenceLeftColumnWidth }))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(PreferencesApp);