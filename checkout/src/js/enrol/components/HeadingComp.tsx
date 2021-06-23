import * as React from "react";
import {FieldHeading} from "../../model";
import FieldFactory from "../../components/form/FieldFactory";
import {replaceWithNl} from "../../common/utils/HtmlUtils";


interface Prop {
  heading: FieldHeading;
  touch?: (field) => void;
  onChangeSuburb?: (item) => void;
  form?: string;
}

export class HeadingComp extends React.Component<Prop, any> {

  render() {
    const {heading, touch, onChangeSuburb, form} = this.props;

    return (
      <fieldset>
        {heading.name &&
          <legend
            style={{whiteSpace: "pre-line"}}
          >
            {replaceWithNl(heading.name)}
          </legend>
        }
        {heading.description &&
          <div
            className="message"
            dangerouslySetInnerHTML={{__html: replaceWithNl(heading.description)}}
            style={{whiteSpace: "pre-line"}}
          />
        }
        {heading.fields.map((field, i) => <FieldFactory
          key={i}
          field={field}
          index={i}
          onBlurSelect={touch}
          onChangeSuburb={onChangeSuburb}
          form={form}
        />)}
      </fieldset>
    );
  }
}

export default HeadingComp;
