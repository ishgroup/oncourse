import React from "react";

export class PayerAdd extends React.Component<any, any> {
  render() {
    return (
      <div className="payer-selection">
        <a className="button" href="#">Choose a different payer</a>
        <ul className="new-payer-option">
          <li id="new-person">
            <a href="#">a person</a>
          </li>
          <li id="new-company">
            <a href="#">a business</a>
          </li>
        </ul>
      </div>
    )
  }
}