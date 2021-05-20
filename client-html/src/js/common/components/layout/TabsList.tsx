/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useRef, useState
} from "react";
import Typography from "@material-ui/core/Typography";
import { withStyles } from "@material-ui/core/styles";
import Grid, { GridSize } from "@material-ui/core/Grid";
import clsx from "clsx";
import ListItem from "@material-ui/core/ListItem";
import createStyles from "@material-ui/core/styles/createStyles";
import { RouteComponentProps, withRouter } from "react-router";
import { APP_BAR_HEIGHT, APPLICATION_THEME_STORAGE_NAME } from "../../../constants/Config";
import { LSGetItem } from "../../utils/storage";

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
      color: theme.palette.common.white,
      fontWeight: 600,
      opacity: 0.6,
      padding: 0,
      "&$selected": {
        opacity: 1,
        backgroundColor: "inherit"
      }
    },
    listItemText: {
      fontWeight: "inherit",
      width: "100%"
    },
    indicator: {
      display: "none"
    },
    selected: {}
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
}

interface ScrollNodes {
  [key: string]: HTMLElement;
}

const SCROLL_TARGET_ID = "TabsListScrollTarget";

const getLayoutArray = (twoColumn: boolean): { [key: string]: GridSize }[] => (twoColumn ? [{ xs: 10 }, { xs: 12 }, { xs: 2 }] : [{ xs: 12 }, { xs: 12 }, { xs: 2 }]);

const TabsList = React.memo<Props & RouteComponentProps>(({
   classes, items, customLabels, customAppBar, itemProps = {}, history, location
  }) => {
  const scrolledPX = useRef<number>(0);
  const scrollNodes = useRef<ScrollNodes>({});

  const [selected, setSelected] = useState<string>(null);
  const [expanded, setExpanded] = useState<number[]>([]);

  useEffect(() => {
    if (items.length) {
      setSelected(items[0].label);
    }
  }, [items.length]);

  useEffect(() => {
    if (location.search) {
      const search = new URLSearchParams(location.search);
      const expandTab = Number(search.get("expandTab"));

      if (search.has("expandTab") && !isNaN(expandTab)) {
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

  const scrollToSelected = useCallback(
    (i: TabsListItem, index) => {
      setSelected(i.label);
      scrollNodes.current[i.label].scrollIntoView({ block: "start", inline: "nearest", behavior: "smooth" });
      if (i.expandable && !expanded.includes(index)) {
        setExpanded([...expanded, index]);
      }
    },
    [expanded]
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
              {items.map((i, index) => (
                <ListItem
                  button
                  selected={i.label === selected}
                  classes={{
                    root: classes.listItemRoot,
                    selected: classes.selected
                  }}
                  onClick={() => scrollToSelected(i, index)}
                  key={index}
                >
                  <Typography variant="body2" component="div" classes={{ root: classes.listItemText }} color="inherit">
                    <div className="text-uppercase">{customLabels && customLabels[index] ? customLabels[index] : i.label}</div>
                    {i.labelAdornment && (
                      <Typography variant="caption" component="div" className="text-pre-wrap">
                        {i.labelAdornment}
                      </Typography>
                    )}
                  </Typography>
                </ListItem>
              ))}
            </div>
          </div>
        </Grid>
      )}
    </Grid>
  );
});

export default withStyles(styles)(withRouter(TabsList));
