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

class LeadEditView extends React.Component<any, any> {
  render() {
    const {
      values,
      isNew,
      isNested,
      classes,
      dispatch,
      dirty,
      form,
      nestedIndex,
      rootEntity,
      twoColumn,
      showConfirm,
      openNestedEditView,
      manualLink
    } = this.props;

    return values ? (
      <TabsList
        items={items}
        itemProps={{
          isNew,
          isNested,
          values,
          classes,
          dispatch,
          dirty,
          form,
          nestedIndex,
          rootEntity,
          twoColumn,
          showConfirm,
          openNestedEditView,
          manualLink
        }}
      />
    ) : null;
  }
}

export default LeadEditView;
