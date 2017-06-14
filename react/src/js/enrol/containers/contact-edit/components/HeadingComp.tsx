import * as React from "react";
import {FieldHeading} from "../../../../model/field/FieldHeading";
import FieldFactory from "../../../../components/form/FieldFactory";


interface Prop {
  heading: FieldHeading;
}

export class HeadingComp extends React.Component<Prop, any> {

  render() {
    const {heading} = this.props;

    return (
      <fieldset>
        <legend>{heading.name}</legend>
        <div className="message" dangerouslySetInnerHTML={{__html: heading.description}}/>
        {heading.fields.map((field, i) => <FieldFactory key={i} field={field} />)}
      </fieldset>
    );
  }
}

export default HeadingComp;
