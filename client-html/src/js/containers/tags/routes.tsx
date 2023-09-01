/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import ChecklistsCatalog from "./ChecklistsCatalog";
import { ChecklistsForm, TagsForm } from "./containers";
import TagsCatalog from "./TagsCatalog";

const tagRoutes = [
  {
    path: "/tags/tagGroups",
    url: "/tags/tagGroups",
    noMenuLink: true,
    main: TagsCatalog
  },
  {
    path: "/tags/tagGroup/:id",
    url: "/tags/tagGroup",
    noMenuLink: true,
    main: TagsForm
  },
  {
    path: "/tags/checklists",
    url: "/tags/checklists",
    noMenuLink: true,
    main: ChecklistsCatalog
  },
  {
    path: "/tags/checklist/:id",
    url: "/tags/checklist",
    noMenuLink: true,
    main: ChecklistsForm
  },
];

export default tagRoutes;
