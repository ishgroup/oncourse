import * as React from "react";
import {FieldHeading} from "../../model";
import FieldFactory from "../../components/form/FieldFactory";
import {replaceWithNl} from "../../common/utils/HtmlUtils";
import { Dispatch } from 'redux';


interface Prop {
  heading: FieldHeading;
  touch?: (field) => void;
  onChangeSuburb?: (item) => void;
  form?: string;
  dispatch?: Dispatch;
}

export class HeadingComp extends React.Component<Prop, any> {

  render() {
    const {heading, touch, onChangeSuburb, form, dispatch} = this.props;

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
          dispatch={dispatch}
        />)}
      </fieldset>
    );
  }
}

export default HeadingComp;
