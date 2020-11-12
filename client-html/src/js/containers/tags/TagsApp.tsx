import React, { useEffect, useMemo } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { isDirty, reset } from "redux-form";
import { State } from "../../reducers/state";
import { setSwipeableDrawerDirtyForm } from "../../common/components/layout/swipeable-sidebar/actions";
import { SidebarWithSearch } from "../../common/components/layout/sidebar-with-search/SidebarWithSearch";
import { getColumnsWidth, updateColumnsWidth } from "../preferences/actions";
import { getAllTags } from "./actions";
import TagSidebar from "./components/TagSidebar";
import Tags from "./Tags";

const TagsApp = React.memo<any>(
  ({
     onInit,
     history,
     match,
     updateColumnsWidth,
     tagLeftColumnWidth,
     location: {
       pathname
     },
     formName,
     dirty,
     onSetSwipeableDrawerDirtyForm
  }) => {
    const isNew = useMemo(() => {
      const pathArray = pathname.split("/");
      return pathArray.length > 2 && pathArray[2] === "new";
    }, [pathname]);

    useEffect(() => {
      onSetSwipeableDrawerDirtyForm(dirty || isNew, formName);
    }, [isNew, dirty, formName]);

    return (
      <SidebarWithSearch
        leftColumnWidth={tagLeftColumnWidth}
        updateColumnsWidth={updateColumnsWidth}
        onInit={onInit}
        SideBar={TagSidebar}
        AppFrame={Tags}
        history={history}
        match={match}
      />
    );
  }
);

const getFormName = form => form && Object.keys(form)[0];

const mapStateToProps = (state: State) => ({
  tagLeftColumnWidth: state.preferences.columnWidth && state.preferences.columnWidth.tagLeftColumnWidth,
  formName: getFormName(state.form),
  dirty: isDirty(getFormName(state.form))(state)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(getAllTags());
    dispatch(getColumnsWidth());
  },
  updateColumnsWidth: (tagLeftColumnWidth: number) => dispatch(updateColumnsWidth({ tagLeftColumnWidth })),
  onSetSwipeableDrawerDirtyForm: (isDirty: boolean, formName: string) => dispatch(
    setSwipeableDrawerDirtyForm(isDirty, () => dispatch(reset(formName)))
  )
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(TagsApp);
