/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React, { useEffect } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import Button from "@material-ui/core/Button";
import { AppBarTitle } from "../../CheckoutSelection";
import EnrolClassListView from "./EnrolClassListView";
import { addItem } from "../../../actions";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import { State } from "../../../../../reducers/state";

const EnrolCourseClassView = React.memo<any>(props => {
  const {
    course,
    courseClasses,
    onClose,
    setSelectedCourse,
    addSelectedItem,
    isClassesEmpty,
    selectedItems,
    currencySymbol,
    onClassSelect
  } = props;

  useEffect(() => {
    if (!course.class || selectedItems.some(i => i.id === course.id && i.type === course.type)) {
      return;
    }

    setSelectedCourse(course);
    addSelectedItem(course);
  }, [course]);

  return (
    <div className="flex-column w-100">
      <CustomAppBar>
        <AppBarTitle
          title={course && course.name}
          type="course"
          link={course.courseId}
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
        selectedItems={selectedItems}
        currencySymbol={currencySymbol}
      />
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  courseClasses: state.checkout.courseClasses,
  isClassesEmpty: state.checkout.checkCourseClassEmpty,
  currencySymbol: state.currency && state.currency.shortCurrencySymbol
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  addSelectedItem: (item: any) => dispatch(addItem(item))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EnrolCourseClassView);
