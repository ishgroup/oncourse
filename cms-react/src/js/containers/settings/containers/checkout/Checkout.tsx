import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {CheckoutSettings} from "../../../../model/settings/CheckoutSettings";
import {getCheckoutSettings, setCheckoutSettings} from "./actions";

interface Props {
  onInit: () => any;
  onSave: (settings) => any;
  checkout: CheckoutSettings;
}

export class Checkout extends React.Component<Props, any> {

  constructor(props) {
    super(props);

    this.state = {
      successUrl: props.successUrl,
      refundPolicy: props.refundPolicy,
    };
  }

  componentDidMount() {
    this.props.onInit();
  }

  onChange(event, key) {
    const value = event.target.type === 'checkbox' ? event.target.checked : event.target.value;
    this.setState({
      [key]: value,
    });
  }

  onSave() {
    const {onSave} = this.props;
    onSave(this.state);
  }


  render() {
    const {successUrl, refundPolicy} = this.state;

    return (
      <div>
        <Form>
          <FormGroup>
            <Label htmlFor="successUrl">After payment successful redirect user to URL</Label>
            <Row>
              <Col sm={3}>
                <Input
                  type="text"
                  name="successUrl"
                  id="successUrl"
                  value={successUrl}
                  onChange={e => this.onChange(e, 'successUrl')}
                />
              </Col>
            </Row>
          </FormGroup>

          <FormGroup>
            <Label htmlFor="refundPolicy">Refund policy page (eg. /student/terms)</Label>
            <Row>
              <Col sm={3}>
                <Input
                  type="text"
                  name="refundPolicy"
                  id="refundPolicy"
                  value={refundPolicy}
                  onChange={e => this.onChange(e, 'refundPolicy')}
                />
              </Col>
            </Row>

          </FormGroup>

          <Button color="primary" onClick={() => this.onSave()}>Save</Button>

        </Form>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  checkout: state.settings.checkoutSettings,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getCheckoutSettings()),
    onSave: settings => dispatch(setCheckoutSettings(settings)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Checkout);
