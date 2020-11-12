import React from "react";
import TabsList, { TabsListItem } from "../../../../common/components/layout/TabsList";
import EnrolmentGeneralTab from "./EnrolmentGeneralTab";
import EnrolmentVetStudentLoans from "./EnrolmentVetStudentLoans";
import EnrolmentAttachmentsTab from "./EnrolmentAttachmentsTab";

const items: TabsListItem[] = [
  {
    label: "General",
    component: props => <EnrolmentGeneralTab {...props} />
  },
  {
    label: "VET Student Loans",
    component: props => <EnrolmentVetStudentLoans {...props} />
  },
  {
    label: "Attachments",
    component: props => <EnrolmentAttachmentsTab {...props} />
  }
];

const EnrolmentEditView = props => {
  const {
    isNew,
    isNested,
    nestedIndex,
    values,
    classes,
    dispatch,
    dirty,
    form,
    twoColumn,
    submitSucceeded,
    manualLink,
    showConfirm,
    syncErrors,
    openNestedEditView,
    onCloseClick,
    toogleFullScreenEditView,
    rootEntity,
    invalid
  } = props;

  return (
    <>
      <TabsList
        items={values ? items : []}
        itemProps={{
          isNew,
          isNested,
          values,
          classes,
          dispatch,
          dirty,
          invalid,
          form,
          nestedIndex,
          rootEntity,
          twoColumn,
          submitSucceeded,
          manualLink,
          showConfirm,
          syncErrors,
          openNestedEditView,
          onCloseClick,
          toogleFullScreenEditView
        }}
      />
    </>
  );
};

export default EnrolmentEditView;
