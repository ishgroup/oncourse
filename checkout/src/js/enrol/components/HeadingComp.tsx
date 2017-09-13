import * as React from "react";
import {FieldHeading} from "../../model";
import FieldFactory from "../../components/form/FieldFactory";


interface Prop {
  heading: FieldHeading;
  touch?: (field) => void;
  onChangeSuburb?: (item) => void;
}

export class HeadingComp extends React.Component<Prop, any> {

  render() {
    const {heading, touch, onChangeSuburb} = this.props;

    return (
      <fieldset>
        {heading.name &&
          <legend>{heading.name}</legend>
        }
        {heading.description &&
          <div className="message" dangerouslySetInnerHTML={{__html: heading.description}}/>
        }
        {heading.fields.map((field, i) => <FieldFactory
          key={i}
          field={field}
          onBlurSelect={touch}
          onChangeSuburb={onChangeSuburb}
        />)}
      </fieldset>
    );
  }
}

export default HeadingComp;
