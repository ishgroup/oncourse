import { FindEntityAql, FindEntityState } from "../../../model/entities/common";
import { ENTITY_AQL_STORAGE_NAME } from "../../../constants/Config";
import { isInStandaloneMode } from "../common";
import { LSGetItem, LSSetItem } from "../storage";

export const openInternalLink = link => {
  window.open(
    (link.includes("http") ? "" : window.location.origin) + link,
    isInStandaloneMode() ? "_self" : "_blank"
  );
};

export const saveCategoryAQLLink = (aql: FindEntityAql) => {
  let entityState: FindEntityState = LSGetItem(ENTITY_AQL_STORAGE_NAME) as FindEntityState;
  if (entityState) {
    entityState = JSON.parse(entityState as string);
  } else {
    entityState = {};
  }

  if (!entityState.data) {
    entityState.data = [];
  }

  const aqlIndex = entityState.data.findIndex(i => i.id === aql.id);
  if (aql.action === "add") {
    aqlIndex === -1
      ? (entityState.data = [aql])
      : (entityState.data[aqlIndex] = aql);
  } else if (aql.action === "remove") {
    if (aqlIndex !== -1) {
      entityState.data.splice(aqlIndex, 1);
    }
  }

  LSSetItem(ENTITY_AQL_STORAGE_NAME, JSON.stringify(entityState));
};
