import React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getUserPreferences } from "../../common/actions";
import { SidebarWithSearch } from "../../common/components/layout/sidebar-with-search/SidebarWithSearch";
import { LICENSE_ACCESS_CONTROL_KEY } from "../../constants/Config";
import { Categories } from "../../model/preferences";
import { State } from "../../reducers/state";
import { getColumnsWidth, getPreferences, updateColumnsWidth } from "../preferences/actions";
import { getApiTokens, getUserRoles, getUsers } from "./actions";
import SideBar from "./components/SecuritySideBar";
import securityRoutes from "./routes";

const SecurityApp = React.memo<any>(
  ({
     onInit,
     history,
     match,
     updateColumnsWidth,
     securityLeftColumnWidth
  }) => (
    <SidebarWithSearch
      leftColumnWidth={securityLeftColumnWidth}
      updateColumnsWidth={updateColumnsWidth}
      onInit={onInit}
      SideBar={SideBar}
      routes={securityRoutes}
      history={history}
      match={match}
    />
    )
);

const mapStateToProps = (state: State) => ({
  securityLeftColumnWidth: state.preferences.columnWidth && state.preferences.columnWidth.securityLeftColumnWidth
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
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(SecurityApp);