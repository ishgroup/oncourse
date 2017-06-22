import React from "react";

export class PayerAdd extends React.Component<any, any> {
  private listEl;

  constructor() {
    super();

    this.state = {
      showList: false,
    };
  }

  handleBlur(e) {
    if (this.listEl.contains(e.relatedTarget)) {
      return;
    }
    this.toggleList(false);
  }

  toggleList(show) {
    this.setState({showList: show});
  }

  render() {
    const {showList} = this.state;
    const {onAddPayer, onAddCompany} = this.props;

    return (
      <div className="payer-selection">
        <a
          className="button"
          href="#"
          onClick={() => this.toggleList(!showList)}
          onBlur={this.handleBlur.bind(this)}
        > Choose a different payer
        </a>
        <ul
          className="new-payer-option"
          ref={ref => this.listEl = ref}
          style={{display: showList ? 'block' : 'none'}}
        >
          <li id="new-person">
            <a href="#" onClick={() => onAddPayer()}>a person</a>
          </li>
          <li id="new-company">
            <a href="#" onClick={() => onAddCompany()}>a business</a>
          </li>
        </ul>
      </div>
    );
  }
}
