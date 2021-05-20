/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useMemo, useRef, useState
} from "react";
import { withStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import IconButton from "@material-ui/core/IconButton";
import Collapse from "@material-ui/core/Collapse";
import { AddCircle, ExpandMore } from "@material-ui/icons";
import createStyles from "@material-ui/core/styles/createStyles";
import debounce from "lodash.debounce";
import { CommonListItem, SidebarSharedProps } from "../../../../model/common/sidebar";
import { latestActivityStorageHandler } from "../../../utils/storage";
import CollapseSideBarListItem from "./CollapseSideBarListItem";

const styles = theme => createStyles({
    listItemPadding: {
      padding: `${theme.spacing(1) - 4}px ${theme.spacing(3)}px`,
      minHeight: "unset"
    },
    listHeadingPadding: {
      padding: theme.spacing(0, 0, 0, 3),
      height: "auto"
    },
    listLabelPadding: {
      padding: theme.spacing(1, 2.5, 1, 0)
    },
    expandIcon: {
      color: theme.palette.text.secondary,
      marginLeft: theme.spacing(1)
    },
    plusIconContainer: {
      marginLeft: "auto"
    },
    inactiveText: {
      opacity: 0.5
    },
    truncateLabel: {
      display: "block",
      textOverflow: "ellipsis",
      overflow: "hidden",
      whiteSpace: "nowrap"
    },
    collapseButton: {
      position: "absolute",
      bottom: theme.spacing(-0.5),
      height: theme.spacing(3),
      width: theme.spacing(3),
      transition: `transform ${theme.transitions.duration.shortest}ms ${theme.transitions.easing.easeInOut}`,
      padding: 0,
      marginLeft: theme.spacing(0.5)
    },
    collapseButtonReversed: {
      transform: "rotate(180deg)"
    },
    itemIcon: {
      fontSize: "14px",
      verticalAlign: "middle",
      marginLeft: theme.spacing(1)
    }
  });

interface Props {
  data: CommonListItem[] | any[];
  sharedProps: SidebarSharedProps;
  basePath: string;
  name: string;
  linkCondition?: (arg: any) => string;
  customPlusHandler?: (e: Event) => void;
  plusIconPath?: string;
  plusIconFullPath?: string;
  ItemIcon?: React.ComponentType<any>;
  classes?: any;
  disableCollapse?: boolean;
  defaultCollapsed?: boolean;
  ItemIconRenderer?: React.ReactNode;
}

const CollapseMenuListBase = React.memo<Props>(
  ({
    classes,
    plusIconPath,
    plusIconFullPath,
    basePath,
    data = [],
    name,
    customPlusHandler,
    ItemIcon,
    linkCondition,
    sharedProps: {
      search, history, activeFiltersConditions, category
    },
    disableCollapse,
    defaultCollapsed,
    ItemIconRenderer
  }) => {
    const [collapsed, setCollapsed] = useState<boolean>(defaultCollapsed || false);
    const [activeLink, setActiveLink] = useState<string>("");
    const [openedTooltip, setOpenOpenTooltip] = React.useState<number>(null);

    const openTooltipRef = useRef<number>(null);

    const debounceToogleTooltip = useCallback<any>(
      debounce(() => {
        setOpenOpenTooltip(openTooltipRef.current);
      }, 150),
      []
    );

    const handleOpenTooltip = useCallback<any>(index => {
      openTooltipRef.current = index;
      debounceToogleTooltip();
    }, []);

    const handleCloseTooltip = useCallback(() => {
      if (openedTooltip !== null) {
        setOpenOpenTooltip(null);
      }
      openTooltipRef.current = null;
      debounceToogleTooltip();
    }, [openedTooltip]);

    const isActiveLink = useCallback(
      (location, to, index, itemName) => {
        const match = location.pathname === to;

        if (match && activeLink !== index.toString()) {
          setActiveLink(index.toString());

          latestActivityStorageHandler({ name: itemName, date: new Date().toISOString(), id: to }, category);

          return true;
        }
        if (!match && activeLink === index.toString()) {
          setActiveLink("");
        }
        return false;
      },
      [activeLink]
    );

    const onClickCollapse = useCallback(() => setCollapsed(prev => !prev), []);

    const onClickPlus = useCallback(
      e => {
        e.stopPropagation();
        customPlusHandler ? customPlusHandler(e) : history.push(plusIconFullPath || basePath + plusIconPath);
      },
      [plusIconFullPath, basePath, plusIconPath, customPlusHandler]
    );

    const filtered = useMemo(() => {
      let filtered = data;

      if (search) {
        filtered = filtered.filter(i => i.name.toLowerCase().includes(search.toLowerCase()));
      }

      if (activeFiltersConditions.length) {
        filtered = filtered.filter(i => activeFiltersConditions.reduce((p, c) => p || c(i), false));
      }

      return filtered;
    }, [search, data, activeFiltersConditions]);

    useEffect(() => {
      if (history.location.pathname.replaceAll('/', '') === basePath.replaceAll('/', '') && filtered.length) {
        history.push(linkCondition ? linkCondition(filtered[0]) : basePath + filtered[0].id);
        if (collapsed) setCollapsed(false);
      } else if (history.location.pathname.replaceAll('/', '').includes(basePath.replaceAll('/', ''))) {
        if (collapsed) setCollapsed(false);
      }
    }, [history.location.pathname, basePath, linkCondition, filtered]);

    return (
      <List disablePadding className="pt-1">
        <ListItem
          disableGutters
          className={classes.listHeadingPadding}
          button={!disableCollapse as any}
          onClick={onClickCollapse}
        >
          <div className={clsx("heading", classes.listLabelPadding)}>
            <span className="relative">
              {name}
              <IconButton
                className={clsx(classes.collapseButton, "d-inline-flex", {
                  [classes.collapseButtonReversed]: collapsed,
                  "invisible": disableCollapse
                })}
              >
                <ExpandMore />
              </IconButton>
            </span>
          </div>

          {(plusIconPath || plusIconFullPath || customPlusHandler) && (
            <div className={classes.plusIconContainer}>
              <IconButton className="p-1" onClick={onClickPlus}>
                <AddCircle className="addButtonColor" width={20} />
              </IconButton>
            </div>
          )}
        </ListItem>

        <Collapse in={disableCollapse || Boolean(search) || !collapsed} mountOnEnter unmountOnExit>
          {filtered.map((item, index) => {
            const to = linkCondition ? linkCondition(item) : basePath + item.id;

            return (
              <CollapseSideBarListItem
                key={index}
                to={to}
                index={index}
                item={item}
                isActiveLink={isActiveLink}
                activeLink={activeLink}
                handleOpenTooltip={handleOpenTooltip}
                handleCloseTooltip={handleCloseTooltip}
                openedTooltip={openedTooltip}
                ItemIconRenderer={ItemIconRenderer}
                ItemIcon={ItemIcon}
                classes={classes}
              />
            );
          })}
        </Collapse>
      </List>
    );
  }
);

export default withStyles(styles)(CollapseMenuListBase);
