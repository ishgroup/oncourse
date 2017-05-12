import * as React from "react";
import * as Lodash from "lodash";
import {ValidationError} from "../../model/common/ValidationError";

const CLASS_ERRORS: string = "validation";
const CLASS_WARNINGS: string = "message";

/**
 * Shows validation errors
 */
class Messages extends React.Component<any, Props> {
  private renderMessages = (messages: string[], className: string): any => {
    if (Lodash.isNil(messages) || !messages.length) {
      return null
    }
    return (
      <div className={className}>
        <ul>
          {messages.map((error, idx) => (
            <li key={idx}>{error}</li>
          ))}
        </ul>
      </div>
    );
  };

  render() {
    const {error} = this.props;
    if (Lodash.isNil(error)) {
      return null
    } else {
      return (
        <div>
          {this.renderMessages(error.formErrors, CLASS_ERRORS)}
        </div>
      );
    }
  }
}

export interface Props {
  error: ValidationError
}

export default Messages;