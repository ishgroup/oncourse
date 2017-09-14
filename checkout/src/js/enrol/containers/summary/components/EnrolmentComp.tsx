import * as React from "react";
import classnames from "classnames";
import moment from "moment";

import {Formats} from "../../../../constants/Formats";
import * as FormatUtils from "../../../../common/utils/FormatUtils";
import {Enrolment, Contact, CourseClass, CourseClassPrice} from "../../../../model";
import {ClassHasCommenced} from "../Messages";
import {ItemWrapper} from "./ItemWrapper";
import {toFormKey} from "../../../../components/form/FieldFactory";
import EnrolmentFieldsForm from "./EnrolmentFieldsForm";


export interface Props {
  contact: Contact;
  enrolment: Enrolment;
  courseClass: CourseClass;
  onChange?: (item, contact) => void;
  onChangeFields?: (form) => any;
}

class EnrolmentComp extends React.Component<Props, any> {
  getFieldInitialValues(headings) {
    const initialValues = {};

    if (headings && headings.length) {
      headings
        .map(h => h.fields
          .filter(f => f.defaultValue)
          .map(f => (initialValues[toFormKey(f.key)] = f.defaultValue)),
        );

      return initialValues;
    }

    return null;
  }

  public render(): JSX.Element {
    const {enrolment, courseClass, contact, onChange, onChangeFields} = this.props;
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

        <EnrolmentFieldsForm
          headings={enrolment.fieldHeadings}
          classId={enrolment.classId}
          selected={enrolment.selected}
          form={`${enrolment.contactId}-${enrolment.classId}`}
          onSubmit={() => undefined}
          initialValues={this.getFieldInitialValues(enrolment.fieldHeadings)}
          onUpdate={form => onChangeFields(form)}
        />
      </div>
    );
  }
}

const ClassPrice = (props): any => {
  const enrolment: Enrolment = props.enrolment;
  const price: CourseClassPrice = enrolment.price || {};

  const fee = price.feeOverriden ? Number(price.feeOverriden).toFixed(2) : Number(price.fee).toFixed(2);
  const discountedFee = price.appliedDiscount ? Number(price.appliedDiscount.discountedFee).toFixed(2) : null;
  const discount = price.appliedDiscount ? Number(price.appliedDiscount.discountValue).toFixed(2) : null;

  const feeStyle = price.appliedDiscount ? {textDecoration: "line-through"} : null;

  return (
    <div className="col-xs-8 col-md-7 alignright">
      {(fee || Number(fee) === 0) &&
      <div className="text-right">
        <span className="fee-full fullPrice" style={feeStyle}>${fee}</span>
      </div>
      }

      {discountedFee !== null &&
      <div className="text-right">
        <span className="fee-discounted discountedPrice">${discountedFee}</span>
      </div>
      }

      {discount !== null &&
      <span style={{display: "none"}} className="discount">${discount}</span>
      }
    </div>
  );
};

const ClassDetails = (props): any => {
  const {courseClass} = props;
  const start: string = FormatUtils.formatDate(courseClass.start, Formats.ClassDateFormat);
  const end: string = FormatUtils.formatDate(courseClass.end, Formats.ClassDateFormat);
  return (
    <div>
      <em>
        {courseClass.room && <span>{`${courseClass.room.site.name} » `}</span>}
        {courseClass.distantLearning && <span className="started">Self paced</span>}
        {start && end &&
        <span><span className="started">{start}</span>&nbsp;-&nbsp;<span className="ended">{end}</span></span>}
      </em>
    </div>
  );
};


export default EnrolmentComp;
