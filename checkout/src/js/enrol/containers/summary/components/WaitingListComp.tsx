import * as React from "react";
import classnames from "classnames";
import {Contact, Course, WaitingList} from "../../../../model";
import {ItemWrapper} from "./ItemWrapper";
import {toFormKey} from "../../../../components/form/FieldFactory";
import EnrolmentFieldsForm from "./EnrolmentFieldsForm";
import {TextField} from "../../../../components/form/TextField";
import {TextAreaField} from "../../../../components/form/TextAreaField";

export interface Props {
  contact: Contact;
  waitingList: WaitingList;
  product: Course;
  onChange?: (item, contact) => void;
  onChangeFields?: (form, type) => any;
  onUpdate: (prop) => void;
}

class WaitingListComp extends React.Component<Props, any> {

  constructor(props) {
    super(props);

    this.state = {
      studentsCount: props.waitingList.studentsCount,
      details: props.waitingList.detail,
      errors: {
        studentsCount: false,
        details: false,
      },
    };
  }

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

  onChangeField(e, key) {
    this.setState({errors: {...this.state.errors, [key]: false}});
    const value = e.target.value;

    if (key === 'studentsCount' && (value > 0 && value <= 30 || value === '')) {
      this.setState({
        [key]: value,
      });
    } else {
      this.setState({
        [key]: value,
      });
    }
  }
  onBlurField(key) {
    const {onUpdate} = this.props;

    if (key === 'studentsCount' && !(this.state[key] > 0 && this.state[key] <= 30)) {
      this.setState({
        [key]: this.props.waitingList.studentsCount,
        errors: {...this.state.errors, [key]: true},
      });

      return;
    }

    this.setState({
      errors: {...this.state.errors, [key]: false},
    });

    onUpdate({[key]: this.state[key]});
  }

  public render(): JSX.Element {
    const {waitingList, product, contact, onChange, onChangeFields} = this.props;
    const {studentsCount, detail, errors} = this.state;

    const divClass = classnames("row waitingList", {disabled: !waitingList.selected});
    const name = `application-${contact.id}-${waitingList.courseId}`;
    const title = <span><span className="checkout-course-type">Waiting List for</span> {product.name}</span>;

    const warning = waitingList.warnings && waitingList.warnings.length ? this.props.waitingList.warnings[0] : null;
    const error = waitingList.warnings && waitingList.errors.length ? this.props.waitingList.errors[0] : null;

    return (
      <div className={divClass}>
        <ItemWrapper
          title={title}
          name={name}
          error={error}
          warning={warning}
          selected={waitingList.selected}
          item={waitingList}
          contact={contact}
          onChange={onChange}
          fullWidth={true}
        >
          {waitingList.selected &&
            <fieldset>
              <TextField
                input={{
                  value: studentsCount,
                  name: `${waitingList.contactId}-${waitingList.courseId}-studentsCount`,
                  onChange: e => this.onChangeField(e, 'studentsCount'),
                  onBlur: () => this.onBlurField('studentsCount'),
                }}
                meta={{
                  invalid: errors.studentsCount,
                  touched: true,
                  error: "You should enter numbers from 1 to 30. If you have larger groups please add the details in the notes.",
                }}
                required={true}
                label="Students count"
                type="number"
              />
              <TextAreaField
                input={{
                  value: detail,
                  name: `${waitingList.contactId}-${waitingList.courseId}-detail`,
                  onChange: e => this.onChangeField(e, 'detail'),
                  onBlur: () => this.onBlurField('detail'),
                }}
                required={false}
                label="Detail"
                type="text"
              />
            </fieldset>
          }

        </ItemWrapper>

        <EnrolmentFieldsForm
          headings={waitingList.fieldHeadings}
          classId={waitingList.courseId}
          selected={waitingList.selected}
          form={`${waitingList.contactId}-${waitingList.courseId}`}
          onSubmit={() => undefined}
          initialValues={this.getFieldInitialValues(waitingList.fieldHeadings)}
          onUpdate={form => onChangeFields(form, 'waitingLists')}
        />
      </div>
    );
  }
}

export default WaitingListComp;
