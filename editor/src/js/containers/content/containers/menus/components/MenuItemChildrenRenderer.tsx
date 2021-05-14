import React from "react";
import MenuItem from "./MenuItem";

interface Props {
  item: any;
  index: any;
  removeItem: (menuItemsWithIds: any[], item: any) => void;
  menuItemsWithIds: any[];
  changeNode: (value: string, type: string, itemId: number) => void;
}

const MenuItemChildrenRenderer = (props: Props) => {
  const {changeNode, index, item, menuItemsWithIds, removeItem} = props;

  return (
    <MenuItem
      removeItem={removeItem}
      index={index}
      item={item}
      menuItemsWithIds={menuItemsWithIds}
      changeNode={changeNode}
    />
  )
};

export default MenuItemChildrenRenderer;