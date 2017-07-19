import * as React from "react";
import classnames from "classnames";
import {Contact, CourseClass, Application} from "../../../../model";
import moment from "moment";
import {ClassHasCommenced} from "../Messages";
import {ItemWrapper} from "./ItemWrapper";


export interface Props {
  contact: Contact;
  application: Application;
  courseClass: CourseClass;
  onChange?: (item, contact) => void;
}

class ApplicationComp extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {application, courseClass, contact, onChange} = this.props;
    const divClass = classnames("row", "enrolmentItem", {disabled: !application.selected});
    const name = `application-${contact.id}-${application.classId}`;
    const title: string = `${courseClass.course.name}`;

    let warning = application.warnings && application.warnings.length ? this.props.application.warnings[0] : null;
    const error = application.warnings && application.errors.length ? this.props.application.errors[0] : null;
    if (!warning && courseClass.start && moment(courseClass.start).isBefore(moment())) {
      warning = ClassHasCommenced;
    }
    return (
      <div className={divClass}>
        <ItemWrapper title={title} name={name} error={error} warning={warning} selected={application.selected}
                     item={application} contact={contact}
                     onChange={onChange}>
          <span className="applicationOnly">(Application only)</span>
        </ItemWrapper>
      </div>
    );
  }
}

export default ApplicationComp;

