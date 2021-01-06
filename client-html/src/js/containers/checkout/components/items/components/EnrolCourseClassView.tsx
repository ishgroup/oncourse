/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from "react";
import { connect } from "react-redux";
import Button from "@material-ui/core/Button";
import { AppBarTitle } from "../../CheckoutSelection";
import EnrolClassListView from "./EnrolClassListView";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import { State } from "../../../../../reducers/state";

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
    <div className="flex-column w-100">
      <CustomAppBar>
        <AppBarTitle
          type="course"
          title={course && course.name}
          link={course && course.courseId}
        />
        <div>
          <Button
            classes={{
              root: "whiteAppBarButton",
              disabled: "whiteAppBarButtonDisabled"
            }}
            onClick={onClose}
          >
            Close
          </Button>
        </div>
      </CustomAppBar>
      <EnrolClassListView
        course={course}
        courseClasses={courseClasses}
        onSelect={onClassSelect}
        isClassesEmpty={isClassesEmpty}
        currencySymbol={currencySymbol}
        selectedItems={selectedItems}
      />
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  isClassesEmpty: state.checkout.checkCourseClassEmpty,
  currencySymbol: state.currency && state.currency.shortCurrencySymbol
});

export default connect<any, any, any>(mapStateToProps, null)(EnrolCourseClassView);
