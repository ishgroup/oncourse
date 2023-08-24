/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from "react";
import { connect } from "react-redux";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import { State } from "../../../../../reducers/state";
import CheckoutAppBar from "../../CheckoutAppBar";
import EnrolClassListView from "./EnrolClassListView";

const EnrolCourseClassView = React.memo<any>(props => {
  const {
    course,
    courseClasses,
    onClose,
    isClassesEmpty,
    currencySymbol,
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
          isClassesEmpty={isClassesEmpty}
          currencySymbol={currencySymbol}
          selectedItems={selectedItems}
        />
      </AppBarContainer>
    </>
  );
});

const mapStateToProps = (state: State) => ({
  isClassesEmpty: state.checkout.checkCourseClassEmpty,
  currencySymbol: state.currency && state.currency.shortCurrencySymbol
});

export default connect<any, any, any>(mapStateToProps, null)(EnrolCourseClassView);
