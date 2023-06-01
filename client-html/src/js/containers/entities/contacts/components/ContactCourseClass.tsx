/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React, { useEffect, useState } from "react";
import Grid from "@mui/material/Grid";
import { EntityType } from "../../../../model/common/NestedEntity";
import NestedEntity from "../../../../common/components/form/nestedEntity/NestedEntity";
import { getNestedTutorClassItem } from "../utils";

const ContactCourseClass = React.memo<any>(props => {
  const {
 showConfirm, twoColumn, values, isNew, dirty 
} = props;
  const [classesTypes, setClassesTypes] = useState<EntityType[]>([]);

  useEffect(() => {
    const types = [];
    const { tutor } = values;

    if (tutor) {
      if (tutor.currentClassesCount) {
        types.push(getNestedTutorClassItem("Current", tutor.currentClassesCount, tutor.id));
      }

      if (tutor.futureClasseCount) {
        types.push(getNestedTutorClassItem("Future", tutor.futureClasseCount, tutor.id));
      }

      if (tutor.selfPacedclassesCount) {
        types.push(getNestedTutorClassItem("Self Paced", tutor.selfPacedclassesCount, tutor.id));
      }

      if (tutor.unscheduledClasseCount) {
        types.push(getNestedTutorClassItem("Unscheduled", tutor.unscheduledClasseCount, tutor.id));
      }

      if (tutor.passedClasseCount) {
        types.push(getNestedTutorClassItem("Finished", tutor.passedClasseCount, tutor.id));
      }

      if (tutor.cancelledClassesCount) {
        types.push(getNestedTutorClassItem("Cancelled", tutor.cancelledClassesCount, tutor.id));
      }
    }

    setClassesTypes(types);
  }, [
    values.tutor && values.tutor.id,
    values.tutor && values.tutor.currentClassesCount,
    values.tutor && values.tutor.futureClasseCount,
    values.tutor && values.tutor.selfPacedclassesCount,
    values.tutor && values.tutor.unscheduledClasseCount,
    values.tutor && values.tutor.passedClasseCount,
    values.tutor && values.tutor.cancelledClassesCount
  ]);

  return (
    <>
      <Grid item xs={12} className="centeredFlex">
        <NestedEntity
          entityName="Classes"
          goToLink="/class"
          entityTypes={classesTypes}
          dirty={dirty}
          showConfirm={showConfirm}
          twoColumn={twoColumn}
          isNew={isNew}
          preventAddMessage="Save course before adding classes"
          secondaryHeading
        />
      </Grid>
    </>
  );
});

export default ContactCourseClass;
