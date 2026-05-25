/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from 'react';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import CheckoutAppBar from '../../CheckoutAppBar';
import EnrolClassListView from './EnrolClassListView';

const EnrolCourseClassView =  props => {
  const {
    course,
    courseClasses,
    onClose,
    onClassSelect,
    selectedItems
  } = props;

  return (
    <>
      <AppBarContainer
        hideHelpMenu
        hideSubmitButton
        disableInteraction
        title={(
          <CheckoutAppBar
            type="course"
            title={course && course.name}
            link={course && course.courseId}
          />
        )}
        onCloseClick={onClose}
      >
        <EnrolClassListView
          course={course}
          courseClasses={courseClasses}
          onSelect={onClassSelect}
          selectedItems={selectedItems}
        />
      </AppBarContainer>
    </>
  );
};

export default EnrolCourseClassView;
