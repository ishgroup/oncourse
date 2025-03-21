/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Contact } from '@api/model';
import { formatCurrency, useAppTheme } from 'ish-ui';
import React, { useMemo, useState } from 'react';
import AvailabilityFormComponent
  from '../../../../common/components/form/availabilityComponent/AvailabilityFormComponent';
import OwnApiNotes from '../../../../common/components/form/notes/OwnApiNotes';
import TabsList, { TabsListItem } from '../../../../common/components/navigation/TabsList';
import { useAppSelector } from '../../../../common/utils/hooks';
import { EditViewProps } from '../../../../model/common/ListView';
import ContactsDetails from './ContactDetails';
import ContactsDocuments from './ContactsDocuments';
import ContactsEducation from './ContactsEducation';
import ContactsFinancial from './ContactsFinancial';
import ContactsGeneral from './ContactsGeneral';
import ContactsMessages from './ContactsMessages';
import ContactsResume from './ContactsResume';
import ContactsTutor from './ContactsTutor';
import ContactsVET from './ContactsVET';

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
    label: "Tutor",
    component: props => <ContactsTutor {...props} />,
    expandable: true
  },
  {
    label: "Resume",
    component: props => <ContactsResume {...props} />,
    expandable: true
  },
];

const items: TabsListItem[] = [
  {
    label: "General",
    component: props => <ContactsGeneral {...props} />
  },
  {
    label: "Contact",
    component: props => <ContactsDetails {...props} />,
    expandable: true
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
    component: props => <OwnApiNotes {...props} className="pl-3 pr-3" />
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

interface Props {
  classes?: any;
  leftOffset?: number;
}

const ContactEditView = (props: Partial<EditViewProps<Contact>> & Props) => {
  const {
    isNew,
    isNested,
    values,
    classes,
    dispatch,
    dirty,
    form,
    rootEntity,
    twoColumn,
    showConfirm,
    manualLink,
    invalid,
    syncErrors,
    onScroll,
    leftOffset
  } = props;

  const [isStudent, setIsStudent] = useState(false);
  const [isTutor, setIsTutor] = useState(false);
  const [isCompany, setIsCompany] = useState(false);
  const [usiUpdateLocked, setUsiUpdateLocked] = useState(true);

  const hideAUSReporting = useAppSelector(state => state.location.countryCode !== 'AU');
  const currencySymbol = useAppSelector(state => state.location.currency.shortCurrencySymbol);

  const theme = useAppTheme();

  const getActiveItems = () => {
    let activeItems = [...items];

    let totalOwing = 0;
    if (values.financialData && values.financialData.length > 0) {
      values.financialData.forEach(fd => {
        totalOwing += fd.owing;
      });
    }

    activeItems[activeItems.findIndex(i => i.label === "Financial")].labelAdornment = twoColumn ? (
          <span className="money centeredFlex">
            {`(Owing ${formatCurrency(totalOwing, currencySymbol)})`}
          </span>
        ) : null;

    if (isStudent) {
      activeItems = [...activeItems, ...studentItems.filter(i => i.label === 'VET' ? !hideAUSReporting : true)];
    }

    if (isTutor) {
      activeItems = [...activeItems, ...tutorItems];
    }

    return activeItems;
  };

  const usiLocked = useMemo(
    () => values && values.student && values.student.usiStatus === "Verified" && usiUpdateLocked,
    [values && values.student && values.student.usiStatus, usiUpdateLocked]
  );

  return (
    <TabsList
      onParentScroll={onScroll}
      items={values ? getActiveItems() : []}
      newsOffset={twoColumn ? theme.spacing(6) : null}
      itemProps={{
        isNew,
        isNested,
        values,
        classes,
        dispatch,
        dirty,
        invalid,
        form,
        rootEntity,
        twoColumn,
        showConfirm,
        manualLink,
        isStudent,
        isTutor,
        isCompany,
        setIsStudent,
        setIsTutor,
        setIsCompany,
        usiLocked,
        setUsiUpdateLocked,
        syncErrors,
        onScroll,
        leftOffset
      }}
    />
  );
};

export default ContactEditView;