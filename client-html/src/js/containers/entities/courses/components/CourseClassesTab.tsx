/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Grid from "@material-ui/core/Grid";
import React, { useEffect, useState } from "react";
import NestedEntity from "../../../../common/components/form/nestedEntity/NestedEntity";
import { EntityType } from "../../../../model/common/NestedEntity";
import { Classes } from "../../../../model/entities/CourseClass";
import { getNestedCourseClassItem } from "../../courseClasses/utils";

const CourseClassesTab: React.FC<any> = ({
   values,
   dirty,
   showConfirm,
   twoColumn,
   isNew
}) => {
  const [classesTypes, setClassesTypes] = useState<EntityType[]>([]);

  useEffect(() => {
    const types = [];

    if (values.currentClassesCount) {
      types.push(getNestedCourseClassItem("Current", values.currentClassesCount, values.id));
    }

    if (values.futureClasseCount) {
      types.push(getNestedCourseClassItem("Future", values.futureClasseCount, values.id));
    }

    if (values.selfPacedclassesCount) {
      types.push(getNestedCourseClassItem("Self Paced", values.selfPacedclassesCount, values.id));
    }

    if (values.unscheduledClasseCount) {
      types.push(getNestedCourseClassItem("Unscheduled", values.unscheduledClasseCount, values.id));
    }

    if (values.passedClasseCount) {
      types.push(getNestedCourseClassItem("Finished", values.passedClasseCount, values.id));
    }

    if (values.cancelledClassesCount) {
      types.push(getNestedCourseClassItem("Cancelled", values.cancelledClassesCount, values.id));
    }

    setClassesTypes(types);
  }, [
    values.id,
    values.currentClassesCount,
    values.futureClasseCount,
    values.selfPacedclassesCount,
    values.unscheduledClasseCount,
    values.passedClasseCount,
    values.cancelledClassesCount
  ]);

  return (
    <Grid container item xs={12} className="pl-3 pr-3">
      <NestedEntity
        entityName="Classes"
        goToLink="/class"
        entityTypes={classesTypes}
        addLink={`/${Classes.path}/new?courseId=${values.id}`}
        dirty={dirty}
        showConfirm={showConfirm}
        twoColumn={twoColumn}
        isNew={isNew}
        preventAddMessage="Save course before adding classes"
      />
    </Grid>
);
};

export default CourseClassesTab;
