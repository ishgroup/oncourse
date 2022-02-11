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
import CloseIcon from "@mui/icons-material/Close";
import Fab from "@mui/material/Fab";
import { Fade, List, Typography } from "@mui/material";
import clsx from "clsx";
import { FixedSizeList, areEqual } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import SidebarSearch from "../sidebar-with-search/components/SidebarSearch";
import { makeAppStyles } from "../../../styles/makeStyles";
import AddButton from "../../icons/AddButton";
import CatalogItem from "./CatalogItem";
import { CatalogItemType } from "../../../../model/common/Catalog";
import NewsRender from "../../news/NewsRender";
import DynamicSizeList from "../../form/DynamicSizeList";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import ExpandableContainer from "../expandable/ExpandableContainer";

const Row = memo<any>(
  props => {
    const { style, item, onOpen } = props;
    return (
      <div style={style}>
        <CatalogItem {...item} dotColor="#45AA05" onOpen={onOpen} />
      </div>
    );
  },
  areEqual
);

const RowRenderer = React.forwardRef<any, any>(({ data, index, style }, ref) => {
  const { items, ...rest } = data;
  return (
    <Row
      key={index}
      item={items[index]}
      style={style}
      forwardedRef={ref}
      {...rest}
    />
  );
});

interface Props {
  addNewItem: Partial<CatalogItemType>;
  items: CatalogItemType[];
  title: string;
  itemsListTitle: string;
  description?: string;
  onOpen: AnyArgFunction;
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

const CatalogWithSearch = (
  {
    title,
    items,
    itemsListTitle,
    description,
    onOpen,
    addNewItem
  }:Props
) => {
  const [search, setSearch] = useState("");
  const [opened, setOpened] = useState(false);
  const [expanded, setExpanded] = useState([0]);
  const classes = useStyles();

  const open = () => setOpened(prev => !prev);

  const filteredItems = useMemo(() => {
    const result = {
      installed: [],
      categories: {}
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
      }
    });

   return result;
  }, [items, search]);

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
        <SidebarSearch setParentSearch={setSearch} />
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
            <div className="mb-2">
              <div className="heading">
                {addNewItem.category}
              </div>
              <CatalogItem {...addNewItem} showAdded={false} enabled installed />
            </div>
            <div>
              {Object.keys(filteredItems.categories).map((c, index) => (
                <ExpandableContainer
                  key={c}
                  index={index}
                  header={c}
                  expanded={search ? [index] : expanded}
                  setExpanded={setExpanded}
                  noDivider
                >
                  {filteredItems.categories[c].map(i => <CatalogItem key={i.title + index} {...i} showAdded />)}
                </ExpandableContainer>
              ))}
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
                      onOpen
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
};

export default CatalogWithSearch;