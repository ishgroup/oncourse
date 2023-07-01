/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import EnrolmentGeneralTab from "./EnrolmentGeneralTab";
import EnrolmentVetStudentLoans from "./EnrolmentVetStudentLoans";
import EnrolmentAttachmentsTab from "./EnrolmentAttachmentsTab";

const items: TabsListItem[] = [
  {
    label: "General",
    component: props => <EnrolmentGeneralTab {...props} />
  },
  {
    label: "VET Student Loans",
    component: props => <div className="pl-3 pr-3" ><EnrolmentVetStudentLoans {...props} /></div>
  },
  {
    label: "Attachments",
    component: props => <EnrolmentAttachmentsTab {...props} />
  }
];

const EnrolmentEditView = props => {
  const {
    isNew,
    isNested,
    nestedIndex,
    values,
    classes,
    dispatch,
    dirty,
    form,
    twoColumn,
    submitSucceeded,
    manualLink,
    showConfirm,
    syncErrors,
    onCloseClick,
    toogleFullScreenEditView,
    rootEntity,
    invalid,
    onEditViewScroll,
    isScrollingRoot,
    onScroll
  } = props;

  return (
    <>
      <TabsList
        onParentScroll={onScroll}
        items={values ? items : []}
        itemProps={{
          isNew,
          isNested,
          values,
          classes,
          dispatch,
          dirty,
          invalid,
          form,
          nestedIndex,
          rootEntity,
          twoColumn,
          submitSucceeded,
          manualLink,
          showConfirm,
          syncErrors,
          onCloseClick,
          toogleFullScreenEditView,
          onEditViewScroll,
          isScrollingRoot,
        }}
      />
    </>
  );
};

export default EnrolmentEditView;
