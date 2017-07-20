import * as React from "react";
import {FieldHeading} from "../../../../model";
import FieldFactory from "../../../../components/form/FieldFactory";


interface Prop {
  heading: FieldHeading;
  touch?: (field) => void;
}

export class HeadingComp extends React.Component<Prop, any> {

  render() {
    const {heading, touch} = this.props;

    return (
      <fieldset>
        <legend>{heading.name}</legend>
        <div className="message" dangerouslySetInnerHTML={{__html: heading.description}}/>
        {heading.fields.map((field, i) => <FieldFactory key={i} field={field} onBlurSelect={touch} />)}
      </fieldset>
    );
  }
}

export default HeadingComp;
