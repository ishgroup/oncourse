import * as React from "react";
import {FieldHeading} from "../../../../model/field/FieldHeading";
import FieldFactory from "../../../services/FieldFactory";


interface Prop {
  heading: FieldHeading;
}

export class HeadingComp extends React.Component<Prop, any> {
  private factory: FieldFactory = new FieldFactory();

  render() {
    const {heading} = this.props;
    const fields = heading.fields.map((f, i) => {
      return this.factory.getComponent(f);
    });
    return (
      <fieldset>
        <legend>{heading.name}</legend>
        <div className="message" dangerouslySetInnerHTML={{__html: heading.description}}/>
        {fields}
      </fieldset>
    );
  }
}

export default HeadingComp;
