/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import TabsList, { TabsListItem } from "../../../../common/components/layout/TabsList";
import LeadGeneral from "./LeadGeneral";
import LeadSites from "./LeadSites";
import LeadAttachmentsTab from "./LeadAttachmentsTab";

const items: TabsListItem[] = [
  {
    label: "General",
    component: props => <LeadGeneral {...props} />
  },
  {
    label: "Sites",
    component: props => <LeadSites {...props} />
  },
  {
    label: "Attachments",
    component: props => <LeadAttachmentsTab {...props} />
  },
];

const LeadEditView = props => props.values ? (
  <TabsList
    items={items}
    itemProps={{
      ...props
    }}
  />
) : null;

export default LeadEditView;
