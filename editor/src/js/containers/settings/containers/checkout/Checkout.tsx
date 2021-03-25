import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import classnames from "classnames";
import {getCheckoutSettings, setCheckoutSettings} from "./actions";
import {Checkbox} from "../../../../common/components/Checkbox";
import {State} from "../../../../reducers/state";
import {CheckoutSettingsState} from "./reducers/State";
import {CheckoutSettings} from "../../../../model";
import {toPositive} from "../../../../common/utils/NumberUtils";

interface Props {
  fetching?: boolean;
  onInit: () => any;
  onSave: (settings: CheckoutSettings) => any;
  checkout: CheckoutSettingsState;
}

export class Checkout extends React.Component<Props, any> {

  constructor(props) {
    super(props);

    const checkout = props.checkout;

    this.state = {
      allowCreateContactOnEnrol: checkout.allowCreateContactOnEnrol,
      allowCreateContactOnWaitingList:  checkout.allowCreateContactOnWaitingList,
      allowCreateContactOnMailingList: checkout.allowCreateContactOnMailingList,
      collectParentDetails: checkout.collectParentDetails,
      contactAgeWhenNeedParent: checkout.contactAgeWhenNeedParent,
      enrolmentMinAge: checkout.enrolmentMinAge,
    };
  }

  componentDidMount() {
    this.props.onInit();
  }

  componentWillReceiveProps(props: Props) {
    if (props.checkout.refreshSettings) {
      const checkout = props.checkout;

      this.setState({
        allowCreateContactOnEnrol: checkout.allowCreateContactOnEnrol,
        allowCreateContactOnWaitingList:  checkout.allowCreateContactOnWaitingList,
        allowCreateContactOnMailingList: checkout.allowCreateContactOnMailingList,
        collectParentDetails: checkout.collectParentDetails,
        contactAgeWhenNeedParent: checkout.contactAgeWhenNeedParent,
        enrolmentMinAge: checkout.enrolmentMinAge,
      });
    }
  }

  onChange(value, key) {

    this.setState({
      [key]: value,
    });

  }

  onSave() {
    const {onSave} = this.props;
    onSave(this.state);
  }

  render() {
    const {allowCreateContactOnEnrol, allowCreateContactOnWaitingList, allowCreateContactOnMailingList,
      collectParentDetails, enrolmentMinAge, contactAgeWhenNeedParent} = this.state;
    const {fetching} = this.props;

    return (
      <div className={classnames({fetching})}>
        <Form>

          <FormGroup>
            <Checkbox
              label="Allow create new student on enrol."
              name="enableSocialMedia"
              checked={allowCreateContactOnEnrol}
              onChange={e => {this.onChange(e.target.checked, 'allowCreateContactOnEnrol');}}
            />
          </FormGroup>

          <FormGroup>
            <Checkbox
              label="Allow create new student on waiting list."
              name="allowCreateContactOnWaitingList"
              checked={allowCreateContactOnWaitingList}
              onChange={e => {this.onChange(e.target.checked, 'allowCreateContactOnWaitingList');}}
            />
          </FormGroup>

          <FormGroup>
            <Checkbox
              label="Allow create new student on mailing list"
              name="allowCreateContactOnMailingList"
              checked={allowCreateContactOnMailingList}
              onChange={e => {this.onChange(e.target.checked, 'allowCreateContactOnMailingList');}}
            />
          </FormGroup>

          <FormGroup>
            <div className="form-inline">

              <Checkbox
                label="Collect parent or guardian details for students under"
                className="inline"
                name="collectParentDetails"
                checked={collectParentDetails}
                onChange={e => {this.onChange(e.target.checked, 'collectParentDetails');}}
              />

              <Input
                type="number"
                name="contactAgeWhenNeedParent"
                className="xs"
                value={contactAgeWhenNeedParent}
                onChange={e => {this.onChange(e.target.value, 'contactAgeWhenNeedParent');}}
                onBlur={e => {this.onChange(toPositive(e.target.value), 'contactAgeWhenNeedParent');}}
              />

            </div>
          </FormGroup>

          <FormGroup>
            <Label htmlFor="addThisId">Enrolment min age</Label>
            <Row>
              <Col sm={2}>
                <Input
                  type="number"
                  name="enrolmentMinAge"
                  id="enrolmentMinAge"
                  value={enrolmentMinAge}
                  onChange={e => this.onChange(e.target.value, 'enrolmentMinAge')}
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


const mapStateToProps = (state: State) => ({
  checkout: state.settings.checkoutSettings,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getCheckoutSettings()),
    onSave: settings => dispatch(setCheckoutSettings(settings)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Checkout as any);
