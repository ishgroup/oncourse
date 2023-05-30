/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo, useState } from "react";
import { Contact } from "@api/model";
import { EditViewProps } from "../../../../model/common/ListView";
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import VetReportingStudent from "./VetReportingStudent";
import VetReportingEnrolments from "./VetReportingEnrolments";
import VetReportingOutcomes from "./VetReportingOutcomes";
import { VetReport } from "../../../../model/entities/VetReporting";

const items: TabsListItem<VetReport>[] = [
  {
    label: "Student",
    labelAdornment: "Contact\nVET",
    component: props => props.values?.student && <VetReportingStudent {...props} />,
    expandable: true
  },
  {
    label: "Enrolments",
    labelAdornment: "VET student loans\nCredit & RPL\nAssessment submissions",
    component: props => props.values?.enrolment && <VetReportingEnrolments {...props} />,
    expandable: true
  },
  {
    label: "Outcomes",
    component: props => props.values?.outcome && <VetReportingOutcomes {...props} />,
    expandable: true
  }
];

const VetReportingEditView = ({ onScroll, values, ...rest }: EditViewProps<Contact>) => {

  const [usiUpdateLocked, setUsiUpdateLocked] = useState(true);

  const usiLocked = useMemo(
    () => values.student && values.student.usiStatus === "Verified" && usiUpdateLocked,
    [values.student && values.student.usiStatus, usiUpdateLocked]
  );

  return (
    <TabsList
      items={items}
      onParentScroll={onScroll}
      itemProps={{
        setUsiUpdateLocked,
        usiLocked,
        values,
        ...rest
      }}
    />
  );
};

export default props => props.values
  ? <VetReportingEditView {...props}/>
  : null;