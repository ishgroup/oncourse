/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo } from "react";
import CatalogWithSearch from "../../../../common/components/layout/catalog/CatalogWithSearch";
import { useAppSelector } from "../../../../common/utils/hooks";
import { CatalogItemType } from "../../../../model/common/Catalog";

const ScriptsCatalog = () => {
  const scripts = useAppSelector(state => state.automation.script.scripts);

  const items = useMemo<CatalogItemType[]>(() => scripts?.map(s => ({
    title: s.name,
    category: s.custom ? "Custom" : "System",
    installed: true,
    enabled: s.grayOut,
    tag: null,
    shortDescription: null
  })) || [], [scripts]);
  
  return (
    <CatalogWithSearch
      addNewItem={{
        title: "Custom script",
        category: "Advanced",
        shortDescription: "Create a new script from scratch"
      }}
      items={items}
      title="Automations"
      itemsListTitle="installed automations"
      setSearch={() => null}
      description="Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab, assumenda, cum cupiditate dignissimos doloribus iure modi nisi nobis possimus quos ratione recusandae tenetur totam! Aliquam laudantium nesciunt ratione tempora totam!"
    />
  );
};

export default ScriptsCatalog;