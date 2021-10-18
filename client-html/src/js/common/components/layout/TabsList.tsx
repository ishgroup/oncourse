/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useRef, useState
} from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import Typography from "@mui/material/Typography";
import { withStyles } from "@mui/styles";
import Grid, { GridSize } from "@mui/material/Grid";
import clsx from "clsx";
import ListItem from "@mui/material/ListItem";
import createStyles from "@mui/styles/createStyles";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import { RouteComponentProps, withRouter } from "react-router";
import { APP_BAR_HEIGHT, APPLICATION_THEME_STORAGE_NAME } from "../../../constants/Config";
import { State } from "../../../reducers/state";
import { LSGetItem } from "../../utils/storage";
import { toggleTabListExpanded } from "./reducers/tabListReducer";
import { TabsListState } from "./actions/tabListActions";

const styles = theme => createStyles({
    listContainer: {
      flexDirection: "column",
      backgroundColor: theme.tabList.listContainer.backgroundColor,
      padding: theme.spacing(4)
    },
    listContainerInner: {
      marginBottom: theme.spacing(8)
    },
    listItemRoot: {
      alignItems: "flex-start",
      marginBottom: theme.spacing(3),
      color: theme.tabList.listItemRoot.color,
      fontWeight: 600,
      opacity: 0.6,
      padding: 0,
      overflow: "hidden",
      position: "relative",
      "&$selected": {
        opacity: 1,
        backgroundColor: "inherit",
        color: theme.tabList.listItemRoot.selectedColor,
        "& $arrowIcon": {
          transform: "translateX(0)",
        },
        "& $listItemText": {
          paddingLeft: 30,
        },
      }
    },
    listItemText: {
      fontWeight: "inherit",
      width: "100%",
      transition: "all 0.2s ease-in-out",
    },
    indicator: {
      display: "none"
    },
    selected: {},
    arrowIcon: {
      position: "absolute",
      transform: "translateX(-30px)",
      transition: "all 0.2s ease-in-out",
    },
  });

export interface TabsListItem {
  label: string;
  component: (props: any) => React.ReactNode;
  labelAdornment?: React.ReactNode;
  expandable?: boolean;
}

interface Props {
  classes?: any;
  itemProps?: any;
  customLabels?: any;
  customAppBar?: boolean;
  items: TabsListItem[];
  expandedEntity?: TabsListState;
  toggleExpanded?: (rootEntity: string, expanded: number[]) => void;
}

interface ScrollNodes {
  [key: string]: HTMLElement;
}

const SCROLL_TARGET_ID = "TabsListScrollTarget";

const getLayoutArray = (twoColumn: boolean): { [key: string]: GridSize }[] => (
  twoColumn ? [{ xs: 10 }, { xs: 12 }, { xs: 2 }] : [{ xs: 12 }, { xs: 12 }, { xs: 2 }]
);

const TabsList = React.memo<Props & RouteComponentProps>(({
  classes,
  items,
  customLabels,
  customAppBar,
  itemProps = {},
  history,
  location,
  expandedEntity,
  toggleExpanded,
}) => {
  const scrolledPX = useRef<number>(0);
  const scrollNodes = useRef<ScrollNodes>({});

  const [selected, setSelected] = useState<string>(null);
  const [expanded, setTabExpanded] = useState<number[]>([]);

  useEffect(() => {
    if (items.length) {
      setSelected(items[0].label);
    }
  }, [items.length]);

  const setExpanded = useCallback(expandedItem => {
    toggleExpanded(itemProps.rootEntity, expandedItem);
  }, [itemProps]);

  useEffect(() => {
    let expandedItem = [];

    if (expandedEntity[itemProps.rootEntity]) {
      expandedItem = expandedEntity[itemProps.rootEntity].expanded;
    }

    setTabExpanded(expandedItem);
  }, [expandedEntity]);

  const scrollToSelected = useCallback((i: TabsListItem, index) => {
    setSelected(i.label);
    scrollNodes.current[i.label].scrollIntoView({ block: "start", inline: "nearest", behavior: "smooth" });
    if (i.expandable && !expanded.includes(index)) {
      setExpanded([...expanded, index]);
    }
  }, [expanded]);

  useEffect(() => {
    if (location.search) {
      const search = new URLSearchParams(location.search);
      const expandTab = Number(search.get("expandTab"));

      if (search.has("expandTab") && !Number.isNaN(expandTab)) {
        setTimeout(() => {
          if (!expanded.includes(expandTab)) {
            setExpanded([...expanded, expandTab]);
          } else {
            scrollToSelected(items[expandTab], expandTab);
          }
          search.delete("expandTab");

          const updatedSearch = decodeURIComponent(search.toString());

          history.replace({
            pathname: location.pathname,
            search: updatedSearch ? `?${updatedSearch}` : ""
          });
        }, 300);
      }
    }
  }, [location.search]);

  const onScroll = useCallback(
    e => {
      if (!itemProps.twoColumn) {
        return;
      }

      if (e.target.id !== SCROLL_TARGET_ID) {
        return;
      }

      const isScrollingDown = scrolledPX.current < e.target.scrollTop;

      if (itemProps.onEditViewScroll) {
        itemProps.onEditViewScroll(e, isScrollingDown);
      }

      scrolledPX.current = e.target.scrollTop;

      const keys = Object.keys(scrollNodes.current);

      const selectedIndex = Object.keys(scrollNodes.current).indexOf(selected);

      // scrolled to bottom
      if (e.target.scrollTop + e.target.offsetHeight === e.target.scrollHeight) {
        setSelected(scrollNodes.current[keys[keys.length - 1]].id);
        return;
      }

      if (
        isScrollingDown
        && e.target.scrollTop
          >= scrollNodes.current[selected].offsetHeight + scrollNodes.current[selected].offsetTop - APP_BAR_HEIGHT
      ) {
        if (selectedIndex + 1 <= keys.length - 1) {
          setSelected(scrollNodes.current[keys[selectedIndex + 1]].id);
        }
        return;
      }

      if (!isScrollingDown && e.target.scrollTop < scrollNodes.current[selected].offsetTop - APP_BAR_HEIGHT) {
        if (selectedIndex - 1 >= 0) {
          setSelected(scrollNodes.current[keys[selectedIndex - 1]].id);
        }
      }
    },
    [selected, itemProps.twoColumn]
  );

  const setScrollNode = useCallback(node => {
    if (!node) {
      return;
    }

    if (!scrollNodes.current[node.id]) {
      scrollNodes.current[node.id] = node;
    }
  }, []);

  const layoutArray = getLayoutArray(itemProps.twoColumn);

  return (
    <Grid container className={clsx("overflow-hidden", { "root": customAppBar && itemProps.twoColumn })}>
      <Grid item xs={layoutArray[0].xs}>
        <Grid container>
          <Grid
            item
            xs={layoutArray[1].xs}
            className={clsx("overflow-y-auto", customAppBar && itemProps.twoColumn ? "appBarContainer" : "fullHeightWithoutAppBar")}
            onScroll={onScroll}
            id={SCROLL_TARGET_ID}
          >
            {items.map((i, tabIndex) => (
              <div id={i.label} key={i.label} ref={setScrollNode}>
                {i.component({
                 ...itemProps, expanded, setExpanded, tabIndex
                })}
              </div>
            ))}
          </Grid>
        </Grid>
      </Grid>
      {itemProps.twoColumn && (
        <Grid item xs={layoutArray[2].xs} className={classes.scrollContainer}>
          <div className={clsx("relative",
            classes.listContainer,
            customAppBar ? "appBarContainer" : "h-100",
            LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "christmas" && "christmasHeader")}
          >
            <div className={classes.listContainerInner}>
              {items.map((i, index) => {
                const itemSelected = i.label === selected;
                return (
                  <ListItem
                    // button
                    selected={itemSelected}
                    classes={{
                      root: classes.listItemRoot,
                      selected: classes.selected
                    }}
                    onClick={() => scrollToSelected(i, index)}
                    key={index}
                  >
                    <ArrowForwardIcon color="inherit" fontSize="small" className={classes.arrowIcon} />
                    <Typography variant="body2" component="div" classes={{ root: classes.listItemText }} color="inherit">
                      <div className="text-uppercase">{customLabels && customLabels[index] ? customLabels[index] : i.label}</div>
                      {i.labelAdornment && (
                        <Typography variant="caption" component="div" className="text-pre-wrap">
                          {i.labelAdornment}
                        </Typography>
                      )}
                    </Typography>
                  </ListItem>
                );
              })}
            </div>
          </div>
        </Grid>
      )}
    </Grid>
  );
});

const mapStateToProps = (state: State) => ({
  expandedEntity: state.tabsList,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  toggleExpanded: (rootEntity, expanded) => dispatch(toggleTabListExpanded(rootEntity, expanded)),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(
  withStyles(styles)(withRouter(TabsList))
);
