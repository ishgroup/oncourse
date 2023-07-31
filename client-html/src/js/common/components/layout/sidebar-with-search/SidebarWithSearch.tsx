import React, { useCallback, useEffect, useMemo, useState } from "react";
import clsx from "clsx";
import { Route, Switch } from "react-router-dom";
import Drawer from "../Drawer";
import LoadingIndicator from "../../progress/LoadingIndicator";
import { AnyArgFunction, NumberArgFunction } from  "ish-ui";
import HamburgerMenu from "../swipeable-sidebar/components/HamburgerMenu";
import { VARIANTS } from "../swipeable-sidebar/utils";
import SidebarSearch from "./components/SidebarSearch";
import { CommonListFilter } from "../../../../model/common/sidebar";
import FiltersList from "./components/FiltersList";
import { MainRoute } from "../../../../routes";
import { LIST_SIDE_BAR_DEFAULT_WIDTH } from "../../../../constants/Config";
import { ResizableWrapper } from "ish-ui";

interface Props {
  leftColumnWidth: number;
  onInit?: AnyArgFunction;
  updateColumnsWidth: NumberArgFunction;
  SideBar: React.ComponentType<any>;
  AppFrame?: React.ComponentType<any>;
  history: any;
  match: any;
  filters?: CommonListFilter[];
  noSearch?: boolean;
  appFrameClass?: string;
  routes?: MainRoute[];
}

export const SidebarWithSearch = (props: Props) => {
  const {
    leftColumnWidth,
    updateColumnsWidth,
    onInit,
    history,
    match,
    SideBar,
    AppFrame,
    noSearch,
    routes,
    filters = [],
    appFrameClass
  } = props;

  const [sidebarWidth, setSidebarWidth] = useState(leftColumnWidth || LIST_SIDE_BAR_DEFAULT_WIDTH);
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
        minWidth="244px"
        maxWidth="50%"
      >
        <Drawer>
          <div className="pl-2">
            <HamburgerMenu variant={VARIANTS.temporary}/>
          </div>
          {!noSearch && <SidebarSearch setParentSearch={setSearch} smallIcons/>}
          {Boolean(filters.length) && (
            <FiltersList filters={filters} activeFilters={activeFilters} setActveFilters={setActveFilters}/>
          )}
          <SideBar search={search} activeFiltersConditions={activeFiltersConditions} history={history} match={match}/>
        </Drawer>
      </ResizableWrapper>

      <div className={clsx("appFrame", appFrameClass)}>
        <LoadingIndicator/>
        {AppFrame ? <AppFrame match={match} routes={routes}/>
          : (
            <Switch>
              {routes.map((route, index) => (
                <Route exact key={index} path={route.path} component={route.main}/>
              ))}
            </Switch>
          )}
      </div>
    </div>
  );
};
