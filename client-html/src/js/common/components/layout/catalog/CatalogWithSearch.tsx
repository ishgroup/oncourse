/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  memo, useMemo, useState
} from "react";
import AddIcon from "@mui/icons-material/Add";
import Fab from "@mui/material/Fab";
import { Fade, IconButton, Typography } from "@mui/material";
import clsx from "clsx";
import { areEqual } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import { Delete } from "@mui/icons-material";
import { makeAppStyles } from "../../../../../ish-ui/styles/makeStyles";
import AddButton from "../../../../../ish-ui/buttons/AddButton";
import CatalogItem from "./CatalogItem";
import { CatalogData, CatalogItemType } from "../../../../model/common/Catalog";
import NewsRender from "../../news/NewsRender";
import DynamicSizeList from "../../../../../ish-ui/dynamicSizeList/DynamicSizeList";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import ExpandableContainer from "../expandable/ExpandableContainer";
import UserSearch from "../swipeable-sidebar/components/UserSearch";

const Row = memo<any>(
  ({
   style, item, onOpen, onRemove, forwardedRef
  }) => (
    <div style={style} ref={forwardedRef}>
      <CatalogItem
        item={item}
        onOpen={onOpen}
        secondaryAction={item.keyCode?.startsWith("ish.") ? (
          <IconButton
            onMouseDown={e => e.stopPropagation()}
            onClick={e => {
              e.stopPropagation();
              onRemove(item);
            }}
            className="lightGrayIconButton"
            size="small"
          >
            <Delete fontSize="inherit" />
          </IconButton>
        ) : null}
        grayOut={!item.enabled}
        showDot={!item.hideDot}
        hoverSecondary
      />
    </div>
  ),
  areEqual
  );

const RowRenderer = React.forwardRef<any, any>(({ data, index, style }, ref) => {
  const { items, ...rest } = data;
  return (
    <Row
      key={items[index]?.id}
      item={items[index]}
      style={style}
      forwardedRef={ref}
      {...rest}
    />
  );
});

interface Props {
  items: CatalogItemType[];
  title: string;
  itemsListTitle: string;
  onOpen: AnyArgFunction;
  toggleInstall?: (item: CatalogItemType) => void;
  addNewItem?: Partial<CatalogItemType>;
  onClickNew?: AnyArgFunction;
  customAddNew?: AnyArgFunction;
  description?: string;
}

const useStyles = makeAppStyles(theme => ({
  root: {
    padding: theme.spacing(4),
    marginTop: theme.spacing(1),
    display: "flex",
    flexDirection: "column",
    height: "100vh"
  },
  fabContainer: {
    zIndex: 1,
    position: "relative",
    "&$fabOpened": {
      "& $fabTip": {
        paddingLeft: theme.spacing(2),
        transform: "translateX(0)",
        visibility: "visible"
      },
      "& $fab": {
        backgroundColor: theme.palette.primary.main,
        transform: "rotate(45deg)"
      }
    },
  },
  fabTip: {
    zIndex: -1,
    top: 12,
    visibility: "hidden",
    position: "absolute",
    transform: "translateX(-30px)",
    willChange: "transform,visibility",
    transition: "transform 0.2s ease-in-out,visibility 0.2s ease-in-out",
  },
  fab: {
    willChange: "transform,backgroundColor",
    backgroundColor: "black",
    transition: "backgroundColor 0.2s ease-in-out, transform 0.2s ease-in-out",
  },
  fabOpened: {},
}));

const CatalogWithSearch = React.memo<Props>((
  {
    title,
    items,
    onOpen,
    toggleInstall,
    addNewItem,
    description,
    itemsListTitle,
    customAddNew,
    onClickNew
  }
) => {
  const [search, setSearch] = useState("");
  const [opened, setOpened] = useState(false);
  const [expanded, setExpanded] = useState([0]);
  const classes = useStyles();

  const open = () => (customAddNew ? customAddNew() : setOpened(prev => !prev));

  const filteredItems = useMemo<CatalogData>(() => {
    const result = {
      installed: [],
      categories: {
      },
      other: []
    };

    items
    .filter(i => i.title.toLowerCase().includes(search.toLowerCase()))
    .forEach(i => {
      if (i.installed) {
        result.installed.push(i);
      }
      if (i.category) {
        if (!result.categories[i.category]) {
          result.categories[i.category] = [];
        }
        result.categories[i.category].push(i);
      } else if (i.keyCode?.startsWith("ish.")) {
        result.other.push(i);
      }
    });

   return result;
  }, [items, search]);
  
  const categoryKeys = Object.keys(filteredItems.categories);

  return (
    <div className={classes.root}>
      <div className="centeredFlex">
        <div className={clsx(classes.fabContainer, opened && classes.fabOpened)}>
          <Fab
            type="button"
            size="large"
            color="primary"
            className={classes.fab}
            onClick={open}
          >
            <AddIcon />
          </Fab>
          <Typography className={classes.fabTip} variant="overline" color="primary" fontWeight="bold">Close</Typography>
        </div>
        <div className="flex-fill" />
        <UserSearch getSearchResults={setSearch} placeholder="Filter items" />
      </div>
      <Typography variant="h4" className="mt-5 mb-3">{title}</Typography>
      <NewsRender page className="mb-3" />
      <div className="relative flex-fill flex-column">
        <Fade in={opened} className="flex-fill flex-column absolute w-100 h-100 overflow-auto">
          <div>
            {description && (
              <div className="mb-3">
                {description}
              </div>
            )}
            {addNewItem && (
              <div className="mb-2">
                <div className="heading">
                  {addNewItem.category}
                </div>
                <CatalogItem item={{ ...addNewItem, installed: true, enabled: true }} onOpen={onClickNew} />
              </div>
            )}
            <div>
              {categoryKeys.map((c, index) => (
                <ExpandableContainer
                  key={c + index}
                  index={index}
                  header={c}
                  expanded={search ? [index] : expanded}
                  setExpanded={setExpanded}
                  noDivider
                >
                  {filteredItems.categories[c].map(i => (
                    <CatalogItem
                      item={i}
                      onOpen={() => toggleInstall(i)}
                      key={i.id}
                      secondaryAction={i.installed && <Typography variant="caption">Added</Typography>}
                      disabled={i.installed}
                    />
                  ))}
                </ExpandableContainer>
              ))}
              {Boolean(filteredItems.other.length) && (
                <ExpandableContainer
                  index={categoryKeys.length}
                  header="Other"
                  expanded={search ? [categoryKeys.length] : expanded}
                  setExpanded={setExpanded}
                  noDivider
                >
                  {filteredItems.other.map(i => (
                    <CatalogItem
                      item={i}
                      onOpen={() => toggleInstall(i)}
                      key={i.id}
                      secondaryAction={i.installed && <Typography variant="caption">Added</Typography>}
                      disabled={i.installed}
                    />
                  ))}
                </ExpandableContainer>
              )}
            </div>
          </div>
        </Fade>
        <Fade in={!opened} className="flex-fill flex-column absolute w-100 h-100">
          <div>
            <div className="centeredFlex">
              <div className="heading">
                {itemsListTitle}
              </div>
              <div className="flex-fill" />
              <div className="centeredFlex primaryColor">
                <Typography variant="button">
                  Add new
                </Typography>
                <AddButton className="p-1" onClick={open} />
              </div>
            </div>
            <div className="flex-fill">
              <AutoSizer>
                {({ width, height }) => (
                  <DynamicSizeList
                    height={height}
                    width={width}
                    itemCount={filteredItems.installed.length}
                    estimatedItemSize={54}
                    itemData={{
                      items: filteredItems.installed,
                      onOpen,
                      onRemove: toggleInstall
                    }}
                  >
                    {RowRenderer}
                  </DynamicSizeList>
                )}
              </AutoSizer>
            </div>
          </div>
        </Fade>
      </div>
    </div>
);
});

export default CatalogWithSearch;