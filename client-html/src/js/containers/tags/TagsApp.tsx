import React, { useEffect } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { SidebarWithSearch } from "../../common/components/layout/sidebar-with-search/SidebarWithSearch";
import { State } from "../../reducers/state";
import { getColumnsWidth, updateColumnsWidth } from "../preferences/actions";
import { getAllTags } from "./actions";
import TagSidebar from "./components/TagSidebar";
import tagRoutes from "./routes";

const TagsApp = React.memo<any>(
  ({
     onInit,
     history,
     match,
     updateColumnsWidth,
     tagLeftColumnWidth,
     location: {
       pathname
     }
  }) => {

    useEffect(() => {
      if (pathname === "/tags") {
        history.replace("/tags/tagGroups");
      }
    }, []);

    return (
      <SidebarWithSearch
        leftColumnWidth={tagLeftColumnWidth}
        updateColumnsWidth={updateColumnsWidth}
        onInit={onInit}
        SideBar={TagSidebar}
        routes={tagRoutes}
        history={history}
        match={match}
        noSearch
      />
    );
  }
);

const mapStateToProps = (state: State) => ({
  tagLeftColumnWidth: state.preferences.columnWidth && state.preferences.columnWidth.tagLeftColumnWidth
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(getAllTags());
    dispatch(getColumnsWidth());
  },
  updateColumnsWidth: (tagLeftColumnWidth: number) => dispatch(updateColumnsWidth({ tagLeftColumnWidth }))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(TagsApp);
