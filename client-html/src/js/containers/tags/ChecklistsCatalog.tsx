/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { RouteComponentProps } from "react-router";
import CatalogWithSearch from "../../common/components/layout/catalog/CatalogWithSearch";
import { useAppSelector } from "../../common/utils/hooks";

export const ChecklistsCatalog = ({ history }: RouteComponentProps) => {
  const allChecklists = useAppSelector(state => state.tags.allChecklists);

  const onOpen = id => {
    history.push(`/tags/checklist/${id}`);
  };

  const onClickNew = () => {
    history.push("/tags/checklist/new");
  };

  return (
    <CatalogWithSearch
      items={allChecklists}
      title="Checklists"
      itemsListTitle="Checklists"
      onOpen={onOpen}
      customAddNew={onClickNew}
    />
  );
};

export default ChecklistsCatalog;