import * as React from "react";
import {isNil} from "lodash";
import classnames from "classnames";
import {Contact} from "../../../../model/web/Contact";
import {CourseClass} from "../../../../model/web/CourseClass";
import moment from "moment";
import {ClassHasCommenced} from "../Messages";
import {Application} from "../../../../model/checkout/Application";


export interface Props {
  contact: Contact;
  application: Application;
  courseClass: CourseClass;
  onChange?: (item, contact) => void;
}

class ApplicationComp extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {application, courseClass, contact, onChange} = this.props;
    const divClass = classnames("row", "enrolmentItem", {"disabled": !application.selected});
    const checkBoxName = `application-${contact.id}-${application.classId}`;
    const title: string = `${courseClass.course.name}`;

    let warning = application.warnings && application.warnings.length ? this.props.application.warnings[0] : null;
    const error = application.warnings && application.errors.length ? this.props.application.errors[0] : null;
    if (!warning && courseClass.start && moment(courseClass.start).isBefore(moment())) {
      warning = ClassHasCommenced;
    }
    return (
      <div className={divClass}>
        <div className="col-xs-16 col-md-17 enrolmentInfo">
          <label>
            <input className="applicationSelect"
                   type="checkbox"
                   name={checkBoxName}
                   onChange={ onChange.bind(this, application, contact) }
                   checked={application.selected } disabled={!isNil(error)}/>
            { title }
          </label>
          {warning && (<span dangerouslySetInnerHTML={{__html: warning}}/>)}
          {error && <span className="disabled" dangerouslySetInnerHTML={{__html: error}}/>}
          <br/>
          <span className="applicationOnly">(Application only)</span>
        </div>
      </div>
    );
  }
}

export default ApplicationComp;