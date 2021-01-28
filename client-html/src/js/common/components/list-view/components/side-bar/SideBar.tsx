import * as React from "react";
import withStyles from "@material-ui/core/styles/withStyles";
import { createStyles } from "@material-ui/core";
import clsx from "clsx";
import FilterGroupComp from "./components/FilterGroup";
import StubFilterItem from "./components/StubFilterItem";
import ListTagGroups from "./components/ListTagGroups";
import HamburgerMenu from "../../../layout/swipeable-sidebar/components/HamburgerMenu";
import { VARIANTS } from "../../../layout/swipeable-sidebar/utils";
import { FilterGroup } from "../../../../../model/common/ListView";

const styles = theme =>
  createStyles({
    root: {
      padding: theme.spacing(0, 0, 2, 2)
    },
    header: {
      height: "64px",
      display: "flex",
      justifyContent: "space-between",
      flexDirection: "row",
      alignItems: "center",
      padding: theme.spacing(0, 3)
    },
    hamburgerMenu: {
      position: "relative",
      zIndex: theme.zIndex.drawer + 1
    }
  });

interface Props {
  classes: any;
  onChangeFilters: any;
  filterGroups: FilterGroup[]
  deleteFilter: any;
  rootEntity: string;
  savingFilter: any;
  fetching: boolean;
}

const SideBar: React.FC<Props> = props => {
  const {
   classes, onChangeFilters, filterGroups, deleteFilter, rootEntity, savingFilter, fetching
  } = props;

  const hasCustomFilters = filterGroups.some(i => i.title === "Custom Filters");

  const UpdateFilters = (index, value) => {
    const groupIndex = Number(index.split("/")[0]);
    const filterIndex = Number(index.split("/")[1]);

    const clone = filterGroups.map((fg, gi) => ({
      ...fg,
      filters: fg.filters.map((f, fi) => ({
        ...f,
        active: gi === groupIndex && fi === filterIndex ? value : f.active
      }))
    }));
    onChangeFilters(clone, "filters");
  };

  return (
    <div>
      <div className={clsx("pl-2", classes.hamburgerMenu)}>
        <HamburgerMenu variant={VARIANTS.temporary} />
      </div>
      <nav className={clsx(classes.root, fetching && "disabled")}>
        {filterGroups.map((i, index) => (
          <FilterGroupComp
            key={index}
            groupIndex={index}
            deleteFilter={deleteFilter}
            rootEntity={rootEntity}
            onUpdate={UpdateFilters}
            title={i.title}
            filters={i.filters}
          />
        ))}

        {savingFilter && !hasCustomFilters && <div className="heading mt-2">Custom Filters</div>}

        {savingFilter && <StubFilterItem rootEntity={rootEntity} savingFilter={savingFilter} />}

        <ListTagGroups onChangeTagGroups={onChangeFilters} />
      </nav>
    </div>
  );
};
export default withStyles(styles)(SideBar);
