/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from 'react';
import classnames from 'classnames';
import moment from 'moment';
import { Application, Contact, CourseClass } from '../../../../model';
import { ClassHasCommenced } from '../Messages';
import { ItemWrapper } from './ItemWrapper';
import { getFormInitialValues } from '../../../../components/form/FieldFactory';
import CustomFieldsForm from './CustomFieldsForm';

export interface Props {
  contact: Contact;
  application: Application;
  courseClass: CourseClass;
  onChange?: (item, contact) => void;
  onChangeFields?: (form, type) => any;
  readonly?: boolean;
}

class ApplicationComp extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {
      application, courseClass, contact, onChange, onChangeFields, readonly
    } = this.props;
    const divClass = classnames('row', 'enrolmentItem', { disabled: !application.selected });
    const name = `application-${contact.id}-${application.classId}`;
    const title = (
      <span>
        <span className="checkout-course-type">Application for</span>
        {' '}
        {courseClass.course.name}
      </span>
    );

    let warning = application.warnings && application.warnings.length ? this.props.application.warnings[0] : null;
    const error = application.warnings && application.errors.length ? this.props.application.errors[0] : null;
    if (!warning && courseClass.start && moment(courseClass.start).isBefore(moment())) {
      warning = ClassHasCommenced;
    }
    return (
      <div className={divClass}>
        <ItemWrapper
          title={title}
          name={name}
          error={error}
          warning={warning}
          selected={application.selected}
          item={application}
          contact={contact}
          readonly={readonly}
          onChange={onChange}
        />

        {!readonly && (
        <CustomFieldsForm
          headings={application.fieldHeadings}
          classId={application.classId}
          selected={application.selected}
          form={`${application.contactId}-${application.classId}`}
          onSubmit={() => undefined}
          initialValues={getFormInitialValues(application.fieldHeadings)}
          onUpdate={(form) => onChangeFields(form, 'applications')}
        />
        )}
      </div>
    );
  }
}

export default ApplicationComp;
