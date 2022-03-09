import React, {
  useCallback, useEffect, useMemo, useState
} from "react";
import clsx from "clsx";
import { ColumnWidth } from "@api/model";
import { ListSideBarDefaultWidth } from "../../list-view/ListView";
import ResizableWrapper from "../resizable/ResizableWrapper";
import Drawer from "../Drawer";
import LoadingIndicator from "../LoadingIndicator";
import { AnyArgFunction, NumberArgFunction } from "../../../../model/common/CommonFunctions";
import HamburgerMenu from "../swipeable-sidebar/components/HamburgerMenu";
import { VARIANTS } from "../swipeable-sidebar/utils";
import SidebarSearch from "./components/SidebarSearch";
import { CommonListFilter } from "../../../../model/common/sidebar";
import FiltersList from "./components/FiltersList";

interface Props {
  leftColumnWidth: number;
  onInit?: AnyArgFunction;
  updateColumnsWidth: NumberArgFunction;
  SideBar: React.ComponentType<any>;
  AppFrame: React.ComponentType<any>;
  history: any;
  match: any;
  filters?: CommonListFilter[];
  noSearch?: boolean;
  appFrameClass?: string;
}

export const SidebarWithSearch = React.memo<Props>(props => {
  const {
    leftColumnWidth, updateColumnsWidth, onInit, history, match, SideBar, AppFrame, noSearch, filters = [], appFrameClass
  } = props;

  const [sidebarWidth, setSidebarWidth] = useState(leftColumnWidth || ListSideBarDefaultWidth);
  const [activeFilters, setActveFilters] = useState<boolean[]>(Array(filters.length).fill(false));
  const [search, setSearch] = useState("");

  useEffect(onInit, []);

  useEffect(() => {
    if (sidebarWidth !== leftColumnWidth) {
      setSidebarWidth(leftColumnWidth);
    }
  }, [leftColumnWidth]);

  const handleResizeCallback = useCallback(
    (...props) => {
      setSidebarWidth(props[2].getClientRects()[0].width);
    },
    [sidebarWidth]
  );

  const handleResizeStopCallback = useCallback(
    (...props) => {
      updateColumnsWidth(props[2].getClientRects()[0].width);
    },
    [sidebarWidth]
  );

  const activeFiltersConditions = useMemo(() => filters.filter((f, i) => activeFilters[i]).map(f => f.condition), [
    filters,
    activeFilters
  ]);

  return (
    <div className="root">
      <ResizableWrapper
        onResizeStop={handleResizeStopCallback}
        onResize={handleResizeCallback}
        sidebarWidth={sidebarWidth}
      >
        <Drawer>
          <div className="pl-2">
            <HamburgerMenu variant={VARIANTS.temporary} />
          </div>
          {!noSearch && <SidebarSearch setParentSearch={setSearch} smallIcons />}
          {Boolean(filters.length) && (
            <FiltersList filters={filters} activeFilters={activeFilters} setActveFilters={setActveFilters} />
          )}
          <SideBar search={search} activeFiltersConditions={activeFiltersConditions} history={history} match={match} />
        </Drawer>
      </ResizableWrapper>

      <div className={clsx("appFrame", appFrameClass)}>
        <LoadingIndicator />
        <AppFrame match={match} />
      </div>
    </div>
  );
});
