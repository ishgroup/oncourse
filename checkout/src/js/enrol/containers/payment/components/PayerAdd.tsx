import * as React from "react";
import classnames from "classnames";

export interface Props {
  onAddPayer: () => void;
  onAddCompany: () => void;
  disabled?: boolean;
}

export class PayerAdd extends React.Component<Props, any> {
  private listEl;

  constructor(props) {
    super(props);

    this.state = {
      showList: false,
    };
  }

  handleBlur(e) {
    const related = e.relatedTarget || document.activeElement;

    if (this.listEl.contains(related)) {
      return;
    }
    this.toggleList(false);
  }

  toggleList(show) {
    const {disabled} = this.props;
    if (disabled) return;

    this.setState({showList: show});
  }

  render() {
    const {showList} = this.state;
    const {onAddPayer, onAddCompany, disabled} = this.props;

    return (
      <div className="payer-selection">
        <a
          className={classnames('button', {disabled})}
          href="#"
          onClick={e => {
            e.preventDefault();
            this.toggleList(!showList);
          }}
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
