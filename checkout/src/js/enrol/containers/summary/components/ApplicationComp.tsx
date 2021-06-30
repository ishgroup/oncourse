import * as React from "react";
import classnames from "classnames";
import {Contact, CourseClass, Application} from "../../../../model";
import moment from "moment";
import {ClassHasCommenced} from "../Messages";
import {ItemWrapper} from "./ItemWrapper";
import {toFormKey} from "../../../../components/form/FieldFactory";
import CustomFieldsForm from "./CustomFieldsForm";

export interface Props {
  contact: Contact;
  application: Application;
  courseClass: CourseClass;
  onChange?: (item, contact) => void;
  onChangeFields?: (form, type) => any;
  readonly?: boolean;
}

class ApplicationComp extends React.Component<Props, any> {
  getFieldInitialValues(headings) {
    const initialValues = {};

    if (headings && headings.length) {
      headings
        .map((h,i) => h.fields
          .filter(f => f.defaultValue)
          .map(f => (initialValues[toFormKey(f.key,i)] = f.defaultValue)),
        );

      return initialValues;
    }

    return null;
  }

  public render(): JSX.Element {
    const {application, courseClass, contact, onChange, onChangeFields, readonly} = this.props;
    const divClass = classnames("row", "enrolmentItem", {disabled: !application.selected});
    const name = `application-${contact.id}-${application.classId}`;
    const title = <span><span className="checkout-course-type">Application for</span> {courseClass.course.name}</span>;

    let warning = application.warnings && application.warnings.length ? this.props.application.warnings[0] : null;
    const error = application.warnings && application.errors.length ? this.props.application.errors[0] : null;
    if (!warning && courseClass.start && moment(courseClass.start).isBefore(moment())) {
      warning = ClassHasCommenced;
    }
    return (
      <div className={divClass}>
        <ItemWrapper title={title} name={name} error={error} warning={warning} selected={application.selected}
                     item={application} contact={contact} readonly={readonly}
                     onChange={onChange}>
        </ItemWrapper>

        {!readonly && <CustomFieldsForm
          headings={application.fieldHeadings}
          classId={application.classId}
          selected={application.selected}
          form={`${application.contactId}-${application.classId}`}
          onSubmit={() => undefined}
          initialValues={this.getFieldInitialValues(application.fieldHeadings)}
          onUpdate={form => onChangeFields(form, 'applications')}
        />}
      </div>
    );
  }
}

export default ApplicationComp;
