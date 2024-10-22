/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import NestedEntity from "../../../../../common/components/form/nestedEntity/NestedEntity";
import { EditViewProps } from "../../../../../model/common/ListView";
import { CourseClassExtended } from "../../../../../model/entities/CourseClass";

const CourseClassOutcomesTab: React.FC<EditViewProps<CourseClassExtended>> = props => {
  const {
 values, isNew, dirty, showConfirm, twoColumn
} = props;

  const entityTypes = useMemo(() => {
    const types = [];

    if (values.inProgressOutcomesCount) {
      types.push({
        name: "in progress",
        count: values.inProgressOutcomesCount,
        link: `/outcome?search=enrolment.courseClass.id is ${values.id}&filter=@In_progress`
      });
    }
    if (values.passOutcomesCount) {
      types.push({
        name: "Passed",
        count: values.passOutcomesCount,
        link: `/outcome?search=enrolment.courseClass.id is ${values.id}&filter=@Pass`
      });
    }
    if (values.failedOutcomesCount) {
      types.push({
        name: "Failed",
        count: values.failedOutcomesCount,
        link: `/outcome?search=enrolment.courseClass.id is ${values.id}&filter=@Fail`
      });
    }
    if (values.withdrawnOutcomesCount) {
      types.push({
        name: "Withdrawn",
        count: values.withdrawnOutcomesCount,
        link: `/outcome?search=enrolment.courseClass.id is ${values.id}&filter=@Withdrawn`
      });
    }
    if (values.otherOutcomesCount) {
      types.push({
        name: "Other",
        count: values.otherOutcomesCount,
        link: `/outcome?search=enrolment.courseClass.id is ${values.id}&filter=@Other`
      });
    }
    if (values.allOutcomesCount) {
      types.push({
        name: "Show all outcomes",
        count: values.allOutcomesCount,
        link: `/outcome?search=enrolment.courseClass.id is ${values.id}`
      });
    }
    return types;
  }, [
    values.id,
    values.inProgressOutcomesCount,
    values.passOutcomesCount,
    values.failedOutcomesCount,
    values.withdrawnOutcomesCount,
    values.otherOutcomesCount,
    values.allOutcomesCount
  ]);

  return (
    <div className="pl-3 pr-3 mt-1 saveButtonTableOffset">
      <NestedEntity
        entityName="Outcomes"
        goToLink="/outcome"
        entityTypes={entityTypes}
        dirty={dirty}
        showConfirm={showConfirm}
        twoColumn={twoColumn}
        isNew={isNew}
      />
    </div>
  );
};

export default CourseClassOutcomesTab;
