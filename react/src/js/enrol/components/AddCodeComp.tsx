import * as React from "react";
import {Promotion} from "../../model/web/Promotion";

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


  public render(): JSX.Element {
    const {onAdd,promotions} = this.props;
    return (
      <div className="code-info">
        <input className="form-control mb-2 mr-sm-2 mb-sm-0 code_input" name="code" type="text"
               onChange={e => this.handleChange(e)} value={this.state.value}/>
        <button disabled={!this.state.value} type="submit" className="btn btn-primary button" id="addCode"
                onClick={() => { 
                  onAdd(this.state.value);
                  this.setState({value: ''});
                }
                }>Add Code
        </button>
        <p>Promotional Code,Gift Certificate or Voucher</p>
        {promotions && promotions.map((promotion: Promotion) => 
          <p className="discountAddedMessage">Code "{promotion.code}" successfully added</p>)
        }
      </div>
    );
  }
}
export default AddCodeComp;

