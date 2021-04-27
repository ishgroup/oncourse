/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import { useSelector } from "react-redux";
import Typography from "@material-ui/core/Typography";
import NestedEntity from "../../../../../common/components/form/nestedEntity/NestedEntity";
import { EditViewProps } from "../../../../../model/common/ListView";
import { CourseClassExtended } from "../../../../../model/entities/CourseClass";
import { State } from "../../../../../reducers/state";

const CourseClassEnrolmentsTab: React.FC<EditViewProps<CourseClassExtended>> = props => {
  const {
   values, isNew, dirty, showConfirm, twoColumn
  } = props;

  const hasQePermissions = useSelector<State, any>(state => state.access["ENROLMENT_CREATE"]);

  const entityTypes = useMemo(() => {
    const types = [];

    if (values.successAndQueuedEnrolmentsCount) {
      types.push({
        name: "Successful and Queued",
        count: values.successAndQueuedEnrolmentsCount,
        link: `/enrolment?search=courseClass.id is ${values.id}&filter=@Current_active,@Completed_active`
      });
    }
    if (values.failedEnrolmentsCount) {
      types.push({
        name: "Failed",
        count: values.failedEnrolmentsCount,
        link: `/enrolment?search=courseClass.id is ${values.id}&filter=@Failed`
      });
    }
    if (values.canceledEnrolmentsCount) {
      types.push({
        name: "Cancelled",
        count: values.canceledEnrolmentsCount,
        link: `/enrolment?search=courseClass.id is ${values.id}&filter=@Cancelled`
      });
    }
    return types;
  }, [values.id, values.failedEnrolmentsCount, values.successAndQueuedEnrolmentsCount, values.canceledEnrolmentsCount]);

  return (
    <div className="pl-3 pr-3 pb-2">
      {isNew ? (
        <div className="pb-1 pt-2">
          <div className="heading pb-1">Enrolments</div>
          <Typography variant="caption" color="textSecondary">
            Please save your new class before adding enrolments
          </Typography>
        </div>
      ) : (
        <NestedEntity
          entityName="Enrolments"
          goToLink="/enrolment"
          addLink={hasQePermissions ? `/checkout?courseClassId=${values.id}` : undefined}
          entityTypes={entityTypes}
          dirty={dirty}
          showConfirm={showConfirm}
          twoColumn={twoColumn}
          isNew={isNew}
        />
      )}
    </div>
  );
};

export default CourseClassEnrolmentsTab;
