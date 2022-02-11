/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo } from "react";
import { RouteComponentProps } from "react-router";
import CatalogWithSearch from "../../../../common/components/layout/catalog/CatalogWithSearch";
import { useAppSelector } from "../../../../common/utils/hooks";
import { CatalogItemType } from "../../../../model/common/Catalog";

// TODO: remove on api model change
const getMockedCategory = index => {
  if (index < 6) {
    return "Course Notifications";
  }
  if (index < 60) {
    return "Payroll";
  }
    return "Rostering";
};

const ScriptsCatalog = ({ history }: RouteComponentProps) => {
  const scripts = useAppSelector(state => state.automation.script.scripts);

  const items = useMemo<CatalogItemType[]>(() => scripts?.map((s, index) => ({
    id: s.id,
    title: s.name,
    category: getMockedCategory(index),
    installed: true,
    enabled: s.grayOut,
    tag: index === 4 ? "New" : index === 5 ? "Popular" : null,
    shortDescription: null
  })) || [], [scripts]);

  const onOpen = id => {
    history.push(`/automation/script/${id}`);
  };
  
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
      onOpen={onOpen}
      description="Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab, assumenda, cum cupiditate dignissimos doloribus iure modi nisi nobis possimus quos ratione recusandae tenetur totam! Aliquam laudantium nesciunt ratione tempora totam!"
    />
  );
};

export default ScriptsCatalog;