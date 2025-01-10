import { DataRow } from "@api/model";
import MenuItem from "@mui/material/MenuItem";
import { openInternalLink } from "ish-ui";
import * as React from "react";

const AuditFindRelatedMenu = props => {
  const {
 findRelated = [], selection, rootEntity, records
} = props;

  const ENTITY_IDENTIFIER_INDEX = 2;

  const findRelatedMap = new Map();
  records.rows.forEach((row: DataRow) => {
    selection.forEach((selectionItem: string) => {
      if (selectionItem === row.id) {
        if (findRelatedMap[row.values[ENTITY_IDENTIFIER_INDEX]] === undefined) {
          findRelatedMap[row.values[ENTITY_IDENTIFIER_INDEX]] = [];
        }
        findRelatedMap[row.values[ENTITY_IDENTIFIER_INDEX]].push(row.values[4]);
      }
    });
  });

  const handleRelatedLinkClick = (item: any, concreteFindRelatedItems: string[], rootEntity: string) => {
    if (item.list && item.expression) {
      openInternalLink(`/${item.list}?search=${item.expression} in (${concreteFindRelatedItems.join(", ")})`);
    } else {
      window.location.href = `/find/related?destList=${
        item.destination
      }&sourceList=${rootEntity}&ids=${concreteFindRelatedItems.join(",")}`;
    }
  };

  findRelated.sort((a, b) => (a.title > b.title ? 1 : -1));

  return (
    findRelated
    && findRelated
      .filter((findRelatedItem: any) => !(findRelatedMap[findRelatedItem.entityIdentifier] === undefined))
      .map((findRelatedItem: any, index: number) => (
        <MenuItem
          key={index}
          onClick={() =>
            handleRelatedLinkClick(findRelatedItem, findRelatedMap[findRelatedItem.entityIdentifier], rootEntity)}
          classes={{
            root: "listItemPadding"
          }}
        >
          {findRelatedItem.title}
        </MenuItem>
      ))
  );
};

export default AuditFindRelatedMenu;
