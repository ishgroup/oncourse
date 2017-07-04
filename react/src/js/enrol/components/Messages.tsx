import * as React from "react";
import * as L from "lodash";
import {ValidationError} from "../../model/common/ValidationError";

const CLASS_ERRORS: string = "validation";
const CLASS_WARNINGS: string = "message";

/**
 * Shows validation errors
 */
export class Messages extends React.Component<Props, any> {
  private renderMessages = (messages: string[], className: string): any => {
    if (L.isNil(messages) || !messages.length) {
      return null;
    }
    return (
      <div className={className}>
        <ul>
          {messages.map((error, idx) => (
            <li key={idx} dangerouslySetInnerHTML={{__html: error}}/>
          ))}
        </ul>
      </div>
    );
  }

  render() {
    const {error} = this.props;
    if (L.isNil(error)) {
      return null;
    } else {
      let errors: string[] = [];

      if (!L.isEmpty(error.formErrors)) {
        errors = errors.concat(error.formErrors);
      }

      if (!L.isEmpty(error.fieldsErrors)) {
        const fes = error.fieldsErrors.map(e => e.error);
        errors = errors.concat(fes);
      }
      return (
        <div>
          {this.renderMessages(errors, CLASS_ERRORS)}
        </div>
      );
    }
  }
}

export interface Props {
  error: ValidationError;
}
