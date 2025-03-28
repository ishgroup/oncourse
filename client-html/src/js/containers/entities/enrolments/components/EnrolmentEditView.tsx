/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo } from 'react';
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import { useAppSelector } from '../../../../common/utils/hooks';
import EnrolmentAttachmentsTab from "./EnrolmentAttachmentsTab";
import EnrolmentGeneralTab from "./EnrolmentGeneralTab";
import EnrolmentVetStudentLoans from "./EnrolmentVetStudentLoans";

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

  const hideAUSReporting = useAppSelector(state => state.location.countryCode !== 'AU');

  const activeItems = useMemo(() => {
    if (hideAUSReporting) {
      return items.filter(i => i.label !== "VET Student Loans");
    }
    return items;
  }, [hideAUSReporting]);

  return (
    <TabsList
      onParentScroll={onScroll}
      items={values ? activeItems : []}
      itemProps={{
        isNew,
        isNested,
        values,
        classes,
        dispatch,
        dirty,
        invalid,
        form,
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
  );
};

export default EnrolmentEditView;
