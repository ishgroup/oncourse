import * as React from "react";
import classnames from "classnames";
import moment from "moment";

import {Formats} from "../../../../constants/Formats";
import * as FormatUtils from "../../../../common/utils/FormatUtils";
import {Enrolment, Contact, CourseClass, CourseClassPrice} from "../../../../model";
import {ClassHasCommenced} from "../Messages";
import {ItemWrapper} from "./ItemWrapper";


export interface Props {
  contact: Contact;
  enrolment: Enrolment;
  courseClass: CourseClass;
  onChange?: (item, contact) => void;
}

class EnrolmentComp extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {enrolment, courseClass, contact, onChange} = this.props;
    const divClass = classnames("row", "enrolmentItem", {disabled: !enrolment.selected});
    const name = `enrolment-${contact.id}-${enrolment.classId}`;
    const title: string = `${courseClass.course.name}`;

    let warning = enrolment.warnings && enrolment.warnings.length ? this.props.enrolment.warnings[0] : null;
    const error = enrolment.warnings && enrolment.errors.length ? this.props.enrolment.errors[0] : null;
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
          selected={enrolment.selected}
          item={enrolment}
          contact={contact}
          onChange={onChange}
        >
          <ClassDetails courseClass={courseClass}/>
        </ItemWrapper>
        {enrolment.selected && courseClass.price && <ClassPrice enrolment={enrolment}/>}
      </div>
    );
  }
}


const ClassPrice = (props): any => {
  const enrolment: Enrolment = props.enrolment;
  const price: CourseClassPrice = enrolment.price || {};

  const fee = price.feeOverriden ? price.feeOverriden : price.fee;
  const discountedFee = price.appliedDiscount ? price.appliedDiscount.discountedFee : null;
  const discount = price.appliedDiscount ? price.appliedDiscount.discountValue : null;
  const feeStyle = price.appliedDiscount ? {textDecoration: "line-through"} : null;

  return (
    <div className="col-xs-8 col-md-7 alignright">
      {fee &&
      <div className="row text-right">
        <span className="fee-full fullPrice" style={feeStyle}>${Number(fee).toFixed(2)}</span>
      </div>
      }

      {discountedFee &&
      <div className="row text-right">
        <span className="fee-discounted discountedPrice">${Number(discountedFee).toFixed(2)}</span>
      </div>
      }

      {discount &&
      <span style={{display: "none"}} className="discount">${Number(discount).toFixed(2)}</span>
      }
    </div>
  );
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
  );
};


export default EnrolmentComp;
