/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useState } from "react";
import { connect } from "react-redux";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import TabsList, { TabsListItem } from "../../../../common/components/layout/TabsList";
import ContactsGeneral from "./ContactsGeneral";
import ContactsFinancial from "./ContactsFinancial";
import ContactsMessages from "./ContactsMessages";
import ContactsVET from "./ContactsVET";
import ContactsEducation from "./ContactsEducation";
import ContactsDocuments from "./ContactsDocuments";
import ContactsResume from "./ContactsResume";
import ContactsTutor from "./ContactsTutor";
import AvailabilityFormComponent from "../../../../common/components/form/availabilityComponent/AvailabilityFormComponent";
import { State } from "../../../../reducers/state";
import { formatCurrency } from "../../../../common/utils/numbers/numbersNormalizing";

const studentItems: TabsListItem[] = [
  {
    label: "VET",
    component: props => <ContactsVET {...props} />,
    expandable: true
  },
  {
    label: "Education",
    component: props => <ContactsEducation {...props} />,
    expandable: true
  }
];

const tutorItems: TabsListItem[] = [
  {
    label: "Resume",
    component: props => <ContactsResume {...props} />,
    expandable: true
  },
  {
    label: "Tutor",
    component: props => <ContactsTutor {...props} />,
    expandable: true
  }
];

const items: TabsListItem[] = [
  {
    label: "General",
    component: props => <ContactsGeneral {...props} />
  },
  {
    label: "Financial",
    component: props => <ContactsFinancial {...props} />,
    expandable: true
  },
  {
    label: "Messages",
    component: props => <ContactsMessages {...props} />
  },
  {
    label: "Notes",
    component: props => <OwnApiNotes {...props} />
  },
  {
    label: "Documents",
    component: props => <ContactsDocuments {...props} />
  },
  {
    label: "Availability Rules",
    component: props => <AvailabilityFormComponent {...props} />
  }
];

const ContactEditView = props => {
  const {
    isNew,
    isNested,
    values,
    classes,
    dispatch,
    dirty,
    form,
    nestedIndex,
    rootEntity,
    twoColumn,
    showConfirm,
    openNestedEditView,
    manualLink,
    invalid,
    currencySymbol,
    syncErrors
  } = props;

  const [isStudent, setIsStudent] = useState(false);
  const [isTutor, setIsTutor] = useState(false);
  const [isCompany, setIsCompany] = useState(false);
  const [usiUpdateLocked, setUsiUpdateLocked] = useState(true);

  const getActiveItems = () => {
    let activeItems = [...items];

    let totalOwing = 0;
    if (values.financialData && values.financialData.length > 0) {
      values.financialData.forEach(fd => {
        totalOwing += fd.owing;
      });
    }

    activeItems[1].labelAdornment = React.useMemo(
      () =>
        (twoColumn ? (
          <span className="money centeredFlex">
            {`Owing ${formatCurrency(totalOwing, currencySymbol)}`}
          </span>
        ) : null),
       [twoColumn, values.financialData]
    );

    if (isStudent) {
      activeItems = [...activeItems, ...studentItems];
    }

    if (isTutor) {
      activeItems = [...activeItems, ...tutorItems];
    }

    return activeItems;
  };

  const getUsiLocked = useCallback(
    () => values && values.student && values.student.usiStatus === "Verified" && usiUpdateLocked,
    [values && values.student && values.student.usiStatus, usiUpdateLocked]
  );

  return (
    <>
      <TabsList
        items={values ? getActiveItems() : []}
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
          showConfirm,
          openNestedEditView,
          manualLink,
          isStudent,
          isTutor,
          isCompany,
          setIsStudent,
          setIsTutor,
          setIsCompany,
          usiLocked: getUsiLocked(),
          setUsiUpdateLocked,
          syncErrors
        }}
      />
    </>
  );
};

const mapStateToProps = (state: State) => ({
  currencySymbol: state.currency.shortCurrencySymbol
});

export default connect<any, any, any>(mapStateToProps)((props: any) =>
  (props.values ? <ContactEditView {...props} /> : null));
