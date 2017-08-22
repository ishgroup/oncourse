import * as React from "react";
import classnames from "classnames";

import moment from "moment";
import {Formats} from "../../../constants/Formats";
import {TimezoneService} from "../../../services/TImezoneService";

export default {
  render() {
    const {courseClass, pending} = this.props;

    const url:string = `/class/${courseClass.course.code}-${courseClass.code}`;
    const deleteClassName = classnames("deleteItem", {loading: pending});
    return (
      <li>
        <a href={url}>{courseClass.course.name}</a>
        <span className={deleteClassName} title="Remove item">
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
  },
};

