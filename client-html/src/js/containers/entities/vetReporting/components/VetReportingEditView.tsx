/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useMemo, useState } from "react";
import { EditViewProps } from "../../../../model/common/ListView";
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import VetReportingStudent from "./VetReportingStudent";
import VetReportingEnrolments from "./VetReportingEnrolments";
import VetReportingOutcomes from "./VetReportingOutcomes";
import { VetReport } from "../../../../model/entities/VetReporting";

const studentItem = {
  label: "Student",
  labelAdornment: "Contact\nVET",
  component: props => props.values?.student && <VetReportingStudent {...props} />,
  expandable: true
};

const enrolmentsItem = {
  label: "Enrolments",
  labelAdornment: "VET student loans\nCredit & RPL\nAssessment submissions",
  component: props => props.values?.student && <VetReportingEnrolments {...props} />,
  expandable: true
};

const outcomesItem = {
  label: "Outcomes",
  component: props => props.values?.enrolment && <VetReportingOutcomes {...props} />,
  expandable: true
};

const VetReportingEditView = ({ onScroll, values, ...rest }: EditViewProps<VetReport>) => {

  const [usiUpdateLocked, setUsiUpdateLocked] = useState(true);
  const [currentItems, setCurrentItems] = useState<TabsListItem<VetReport>[]>([studentItem, enrolmentsItem]);

  const usiLocked = useMemo(
    () => values.student && values.student.student.usiStatus === "Verified" && usiUpdateLocked,
    [values.student && values.student.student.usiStatus, usiUpdateLocked]
  );
  
  useEffect(() => {
    const hasOutcomeItem = currentItems.find(i => i.label === "Outcomes")
    if (values.enrolment.id && !hasOutcomeItem) {
      setCurrentItems([studentItem, enrolmentsItem, outcomesItem]);
    }
    if (!values.enrolment.id && hasOutcomeItem) {
      setCurrentItems([studentItem, enrolmentsItem]);
    }
  }, [values.enrolment.id]);

  return (
    <TabsList
      items={currentItems}
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