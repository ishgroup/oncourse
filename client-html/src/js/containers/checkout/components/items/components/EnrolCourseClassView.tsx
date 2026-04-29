/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from 'react';
import { connect } from 'react-redux';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { State } from '../../../../../reducers/state';
import CheckoutAppBar from '../../CheckoutAppBar';
import EnrolClassListView from './EnrolClassListView';

const EnrolCourseClassView = React.memo<any>(props => {
  const {
    course,
    courseClasses,
    onClose,
    currencySymbol,
    onClassSelect,
    selectedItems,
    onLoadMoreClasses
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
          currencySymbol={currencySymbol}
          selectedItems={selectedItems}
          onLoadMoreClasses={onLoadMoreClasses}
        />
      </AppBarContainer>
    </>
  );
});

const mapStateToProps = (state: State) => ({
  currencySymbol: state.location.currency && state.location.currency.shortCurrencySymbol
});

export default connect<any,any,any>(mapStateToProps)(EnrolCourseClassView);
