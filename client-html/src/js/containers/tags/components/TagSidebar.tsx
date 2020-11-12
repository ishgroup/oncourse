import React from "react";
import CollapseMenuList from "../../../common/components/layout/side-bar-list/CollapseSideBarList";
import { State } from "../../../reducers/state";
import { connect } from "react-redux";

const SideBar = React.memo<any>(({ search, match, history, tags, activeFiltersConditions }) => (
  <CollapseMenuList
    name="Tag Groups"
    basePath="/tags/"
    plusIconPath="new"
    data={tags}
    sharedProps={{
      search,
      history,
      activeFiltersConditions,
      category: "Tags"
    }}
    disableCollapse
  />
));

const mapStateToProps = (state: State) => ({
  tags: state.tags.allTags
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(SideBar);
