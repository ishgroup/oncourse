import React, { useEffect, useMemo } from "react";
import { isDirty, reset } from "redux-form";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { setSwipeableDrawerDirtyForm } from "../../common/components/layout/swipeable-sidebar/actions";
import SideBar from "./components/SecuritySideBar";
import { Categories } from "../../model/preferences";
import { getApiTokens, getUserRoles, getUsers } from "./actions";
import { getColumnsWidth, getPreferences, updateColumnsWidth } from "../preferences/actions";
import { State } from "../../reducers/state";
import { LICENSE_ACCESS_CONTROL_KEY } from "../../constants/Config";
import { getUserPreferences } from "../../common/actions";
import { SidebarWithSearch } from "../../common/components/layout/sidebar-with-search/SidebarWithSearch";
import SecutityAppFrame from "./components/SecutityAppFrame";

const SecurityApp = React.memo<any>(
  ({
     onInit,
     history,
     match,
     updateColumnsWidth,
     securityLeftColumnWidth,
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
        leftColumnWidth={securityLeftColumnWidth}
        updateColumnsWidth={updateColumnsWidth}
        onInit={onInit}
        SideBar={SideBar}
        AppFrame={SecutityAppFrame}
        history={history}
        match={match}
      />
    );
  }
);

const getFormName = form => form && Object.keys(form)[0];

const mapStateToProps = (state: State) => ({
  securityLeftColumnWidth: state.preferences.columnWidth && state.preferences.columnWidth.securityLeftColumnWidth,
  formName: getFormName(state.form),
  dirty: isDirty(getFormName(state.form))(state)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(getApiTokens());
    dispatch(getUserRoles());
    dispatch(getUsers());
    dispatch(getPreferences(Categories.security));
    dispatch(getColumnsWidth());
    dispatch(getUserPreferences([LICENSE_ACCESS_CONTROL_KEY]));
  },
  updateColumnsWidth: (securityLeftColumnWidth: number) => dispatch(updateColumnsWidth({ securityLeftColumnWidth })),
  onSetSwipeableDrawerDirtyForm: (isDirty: boolean, formName: string) => dispatch(
    setSwipeableDrawerDirtyForm(isDirty, () => dispatch(reset(formName)))
  )
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(SecurityApp);
