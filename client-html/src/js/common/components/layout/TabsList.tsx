/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useRef, useState
} from "react";
import Typography from "@mui/material/Typography";
import { withStyles } from "@mui/styles";
import Grid, { GridSize } from "@mui/material/Grid";
import clsx from "clsx";
import ListItem from "@mui/material/ListItem";
import createStyles from "@mui/styles/createStyles";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import { RouteComponentProps, withRouter } from "react-router";
import { APP_BAR_HEIGHT, APPLICATION_THEME_STORAGE_NAME, STICKY_HEADER_EVENT } from "../../../constants/Config";
import { LSGetItem, LSSetItem } from "../../utils/storage";
import { EditViewProps } from "../../../model/common/ListView";
import { useStickyScrollSpy } from "../../utils/hooks";
import NewsRender from "../news/NewsRender";
import { useStickyScrollSpy } from "../../utils/hooks";

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
    cursor: 'pointer',
    "&$selected": {
      opacity: 1,
      backgroundColor: "inherit",
      color: theme.tabList.listItemRoot.selectedColor,
      "& $arrowIcon": {
        transform: "translateX(0)",
      },
      "& $listItemLabel": {
        paddingLeft: 30,
      },
    },
    "&:hover": {
      opacity: 0.8,
    }
  },
  listItemText: {
    fontWeight: "inherit",
    width: "100%",
  },
  indicator: {
    display: "none"
  },
  listItemLabel: {
    textTransform: 'uppercase',
    transition: "all 0.2s ease-in-out",
  },
  selected: {},
  arrowIcon: {
    position: "absolute",
    transform: "translateX(-30px)",
    transition: "all 0.2s ease-in-out",
  },
  fullScreenContentItem: {
    height: "100vh",
    marginTop: -64,
    paddingTop: theme.spacing(2),
  },
  threeColumnContentItem: {
    height: "calc(100vh - 64px)",
  }
});

export interface TabsListItem {
  readonly type?: string;
  component: (props: any) => React.ReactNode;
  labelAdornment?: React.ReactNode;
  expandable?: boolean;
  label: string;
}

interface Props {
  classes?: any;
  itemProps?: EditViewProps & any;
  customAppBar?: boolean;
  items: TabsListItem[];
}

interface ScrollNodes {
  [key: string]: HTMLElement;
}

const SCROLL_TARGET_ID = "TabsListScrollTarget";

const TABLIST_LOCAL_STORAGE_KEY = "localstorage_key_tab_list";

const getLayoutArray = (twoColumn: boolean): { [key: string]: GridSize }[] => (twoColumn ? [{ xs: 10 }, { xs: 12 }, { xs: 2 }] : [{ xs: 12 }, { xs: 12 }, { xs: 2 }]);

const TabsList = React.memo<Props & RouteComponentProps>(({
   classes, items, customAppBar, itemProps = {}, history, location
  }) => {
  const { scrollSpy } = useStickyScrollSpy();

  const scrolledPX = useRef<number>(0);
  const scrollNodes = useRef<ScrollNodes>({});

  const [selected, setSelected] = useState<string>(null);
  const [expanded, setExpanded] = useState<number[]>([]);

  useEffect(() => {
    const stored = JSON.parse(LSGetItem(TABLIST_LOCAL_STORAGE_KEY) || "");
    if (stored && stored[itemProps.rootEntity]) {
      setExpanded(stored[itemProps.rootEntity]);
    }
  }, []);

  useEffect(() => {
    const stored = JSON.parse(LSGetItem(TABLIST_LOCAL_STORAGE_KEY) || "");
    let updated = {};
    if (stored) {
      updated = { ...stored };
    }
    updated[itemProps.rootEntity] = expanded;
    LSSetItem(TABLIST_LOCAL_STORAGE_KEY, JSON.stringify(updated));
  }, [expanded, itemProps.rootEntity]);

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
      scrollSpy(e);

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
            <NewsRender page />
            {items.map((i, tabIndex) => (
              <div
                id={i.label}
                key={tabIndex}
                ref={setScrollNode}
                className={!itemProps.twoColumn && tabIndex === items.length - 1 && "saveButtonTableOffset"}
              >
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
                    selected={itemSelected}
                    classes={{
                      root: classes.listItemRoot,
                      selected: classes.selected
                    }}
                    onClick={() => scrollToSelected(i, index)}
                    key={index}
                  >

                    <Typography variant="body2" component="div" classes={{ root: classes.listItemText }} color="inherit">
                      <ArrowForwardIcon color="inherit" fontSize="small" className={classes.arrowIcon} />
                      <div className={classes.listItemLabel}>{i.label}</div>
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

export default withStyles(styles)(withRouter(TabsList));