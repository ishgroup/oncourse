import * as React from 'react';
import {CourseClassCart} from "../../services/IshState";
import moment from "moment";
import {Formats} from "../../constants/Formats";
import {TimezoneService} from "../../services/TImezoneService";

export default {
  render() {
    let courseClass: CourseClassCart = this.props.courseClass;

    return (
      <li>
        <a href={`/class/${courseClass.id}`}>{courseClass.course.name}</a>
        <span className={this.utils.classnames('deleteItem', {
          'loading': this.props.pending
        })} title="Remove item">
                    <a onClick={this.methods.remove}>X</a>
                </span>
        {courseClass.start && courseClass.end &&
        <div className="shortListOrderClasses">
          <abbr className="dtstart" title="">
            {moment(courseClass.start).add(moment().utcOffset(), "m").format(Formats.DATE_TIME_FORMAT)}
          </abbr>
          {" - "}
          <abbr className="dtend" title="">
            {moment(courseClass.end).add(moment().utcOffset(), "m").format(Formats.DATE_TIME_FORMAT)}
          </abbr>
          {` (UTC ${TimezoneService.getTimezone()}) `}
        </div>}
      </li>
    );
  }
};



