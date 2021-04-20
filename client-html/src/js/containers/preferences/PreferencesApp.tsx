/**
 * preferences app layout
 * */

import React, { useEffect, useMemo } from "react";
import { isDirty, reset } from "redux-form";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { State } from "../../reducers/state";
import { Categories } from "../../model/preferences";
import { SidebarWithSearch } from "../../common/components/layout/sidebar-with-search/SidebarWithSearch";
import { setSwipeableDrawerDirtyForm } from "../../common/components/layout/swipeable-sidebar/actions";
import {
  getColumnsWidth,
  getDataCollectionForms,
  getDataCollectionRules, getGradingTypes,
  getPreferences,
  getTutorRoles,
  updateColumnsWidth
} from "./actions";
import AppFrame from "./components/AppFrame";
import SideBar from "./components/SideBar";

const PreferencesApp = React.memo<any>(
  ({
     onInit,
     history,
     match,
     updateColumnsWidth,
     preferenceLeftColumnWidth,
     location: {
       pathname
     },
    formName,
    dirty,
    onSetSwipeableDrawerDirtyForm
  }) => {
    const isNew = useMemo(() => {
      const pathArray = pathname.split("/");
      return pathArray.length > 3 && pathArray[3] === "new";
    }, [pathname]);

    useEffect(() => {
      onSetSwipeableDrawerDirtyForm(dirty || isNew, formName);
    }, [isNew, dirty, formName]);

    return (
      <SidebarWithSearch
        leftColumnWidth={preferenceLeftColumnWidth}
        updateColumnsWidth={updateColumnsWidth}
        onInit={onInit}
        SideBar={SideBar}
        AppFrame={AppFrame}
        history={history}
        match={match}
      />
    );
  }
);

const getFormName = form => form && Object.keys(form)[0];

const mapStateToProps = (state: State) => ({
  preferenceLeftColumnWidth: state.preferences.columnWidth && state.preferences.columnWidth.preferenceLeftColumnWidth,
  formName: getFormName(state.form),
  dirty: isDirty(getFormName(state.form))(state)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(getDataCollectionRules());
    dispatch(getDataCollectionForms());
    dispatch(getColumnsWidth());
    dispatch(getTutorRoles());
    dispatch(getGradingTypes());
    dispatch(getPreferences(Categories.licences));
  },
  updateColumnsWidth: (preferenceLeftColumnWidth: number) => dispatch(updateColumnsWidth({ preferenceLeftColumnWidth })),
  onSetSwipeableDrawerDirtyForm: (isDirty: boolean, formName: string) => dispatch(
    setSwipeableDrawerDirtyForm(isDirty, () => dispatch(reset(formName)))
  )
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(PreferencesApp);
