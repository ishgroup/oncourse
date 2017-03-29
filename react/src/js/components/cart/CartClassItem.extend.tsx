import * as React from 'react';
import {CourseClassCart} from "../../services/IshState";

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
                        {/* TODO: Fix api, and than delete JSON.stringify */}
                        <abbr className="dtstart" title="">{JSON.stringify(courseClass.start)}</abbr>
                         - <abbr className="dtend" title="">{JSON.stringify(courseClass.end)}</abbr>
                    </div>}
            </li>
        );
    }
};
