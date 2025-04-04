/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Divider from '@mui/material/Divider';
import Typography from '@mui/material/Typography';
import $t from '@t';
import React, { useMemo } from 'react';
import { useSelector } from 'react-redux';
import NestedEntity from '../../../../../common/components/form/nestedEntity/NestedEntity';
import { EditViewProps } from '../../../../../model/common/ListView';
import { CourseClassExtended } from '../../../../../model/entities/CourseClass';
import { State } from '../../../../../reducers/state';

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
    <div className="pl-3 pr-3">
      <Divider className="mb-1" />
      {isNew ? (
        <div className="pb-1 pt-1">
          <div className="heading pb-1">{$t('enrolments4')}</div>
          <Typography variant="caption" color="textSecondary">
            {$t('please_save_your_new_class_before_adding_enrolment')}
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
      <Divider className="mt-1" />
    </div>
  );
};

export default CourseClassEnrolmentsTab;
