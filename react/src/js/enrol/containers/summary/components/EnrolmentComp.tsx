import * as React from "react";
import {isNil} from "lodash";
import {Formats} from "../../../../constants/Formats";
import * as FormatUtils from "../../../../common/utils/FormatUtils";

import classnames from "classnames";
import {Enrolment} from "../../../../model/checkout/Enrolment";
import {Contact} from "../../../../model/web/Contact";
import {CourseClass} from "../../../../model/web/CourseClass";
import {CourseClassPrice} from "../../../../model/web/CourseClassPrice";
import moment from "moment";
import {ClassHasCommenced} from "../Messages";


export interface Props {
  contact: Contact;
  enrolment: Enrolment;
  courseClass: CourseClass;
  selected: boolean;
  onSelect: (item: Enrolment, selected: boolean) => void;
}

class EnrolmentComp extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {enrolment, courseClass, selected, contact, onSelect} = this.props;
    const divClass = classnames("row", "enrolmentItem", {"disabled": !selected});
    const checkBoxName = `enrolment-${contact.id}-${enrolment.classId}`;
    const onChange = () => onSelect(enrolment, !selected);
    const title: string = `${courseClass.course.name}`;

    let warning =  enrolment.warnings && enrolment.warnings.length ? this.props.enrolment.warnings[0] : null;
    const error = enrolment.warnings && enrolment.errors.length ? this.props.enrolment.errors[0] : null;
    if (!warning && courseClass.start && moment(courseClass.start).isBefore(moment())) {
      warning = ClassHasCommenced;
    }
    return (
      <div className={divClass}>
        <div className="col-xs-16 enrolmentInfo">
          <label>
            <input className="enrolmentSelect"
                   type="checkbox"
                   name={checkBoxName}
                   onChange={ onChange }
                   checked={selected } disabled={!isNil(error)}/>
            { title }
          </label>
          {warning && (<span dangerouslySetInnerHTML={{__html: warning}}/>)}
          {error && <span className="disabled" dangerouslySetInnerHTML={{__html: error}}/>}
          <br/>
          <ClassDetails courseClass={courseClass}/>
        </div>
        {selected && courseClass.price && <ClassPrice enrolment={enrolment}/>}
      </div>
    );
  }
}


const ClassPrice = (props): any => {
  const enrolment: Enrolment = props.enrolment;
  const price: CourseClassPrice = enrolment.price;

  const fee = price.feeOverriden ? price.feeOverriden : price.fee;
  const discountedFee = price.appliedDiscount ? price.appliedDiscount.discountedFee : null;
  const discount = price.appliedDiscount ? price.appliedDiscount.discountValue : null;
  const feeStyle = price.appliedDiscount ? {textDecoration: "line-through"} : null;
  return (
    <div className="col-xs-8 alignright">
      <div className="row">
        { fee && <span className="col-xs-24 col-md-12 fee-full fullPrice text-right" style={ feeStyle }>${fee}</span> }
        { discountedFee &&
        <span className="col-xs-24 col-md-12 fee-discounted discountedPrice text-right">${ discountedFee }</span> }
        { discount && <span style={{display: "none"}} className="discount">${discount}</span> }
      </div>
    </div>
  )
};

const ClassDetails = (props): any => {
  const {courseClass} = props;
  const start: string = FormatUtils.formatDate(courseClass.start, Formats.ClassDateFormat);
  const end: string = FormatUtils.formatDate(courseClass.end, Formats.ClassDateFormat);
  return (
    <em>
      {courseClass.room && <span>{`${courseClass.room.site.name} » `}</span>}
      {courseClass.distantLearning && <span className="started">Self paced</span>}
      {start && end &&
      <span><span className="started">{start}</span>&nbsp;-&nbsp;<span className="ended">{end}</span></span>}
    </em>
  )
};


export default EnrolmentComp;