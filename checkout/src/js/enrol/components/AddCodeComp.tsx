import * as React from "react";
import {Promotion} from "../../model";

interface Props {
  onAdd: (code: string) => void;
  promotions: Promotion[];
}

interface State {
  value: string;
}


class AddCodeComp extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {value: ""};
  }

  handleChange(e) {
    this.setState({
      value: e.target.value,
    });
  }

  handleClick() {
    const {onAdd} = this.props;

    onAdd(this.state.value);
    this.setState({value: ""});
  }


  public render(): JSX.Element {
    const {promotions} = this.props;
    const {value} = this.state;

    return (
      <div className="code-info">
        <div className="code-input">
          <input
            className="form-control mb-2 mr-sm-2 mb-sm-0 code_input"
            name="code"
            type="text"
            onChange={e => this.handleChange(e)}
            value={value}
          />
          <button
            disabled={!value}
            type="submit"
            className="btn btn-primary button"
            id="addCode"
            onClick={() => this.handleClick()}
          >

            Add Code
          </button>
        </div>
        <p>Promotional Code, Gift Certificate or Voucher</p>
        {promotions && promotions.map((promotion: Promotion) => (
          <p key={promotion.id} className="discountAddedMessage">Code "{promotion.code}" successfully added</p>
        ))}
      </div>
    );
  }
}

export default AddCodeComp;

