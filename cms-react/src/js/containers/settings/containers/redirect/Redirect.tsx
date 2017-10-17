import React from 'react';
import update from 'react/lib/update';
import {connect, Dispatch} from "react-redux";
import classnames from 'classnames';
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {getRedirectSettings, setRedirectSettings} from "./actions";
import {RedirectSettingsState} from "./reducers/State";
import {State} from "../../../../reducers/state";

interface Props {
  onInit: () => any;
  onSave: (settings) => any;
  redirect: RedirectSettingsState;
}

export class Redirect extends React.Component<Props, any> {

  constructor(props) {
    super(props);

    this.state = {
      rules: props.redirect.rules,
    };
  }

  componentDidMount() {
    this.props.onInit();
  }

  onChange(e, index, key) {
    this.setState(update(this.state, {
      rules: {[index]: {[key]: {$set: e.target.value}}},
    }));
  }

  onAddNew() {
    this.setState(update(this.state, {
      rules: {
        $push: [{from: '', to: ''}],
      },
    }));
  }

  onSave() {
    const {onSave} = this.props;

    this.validate();

    const state = this.state;
    state.rules = state.rules
      .filter(rule => rule.from || rule.to)
      .map(rule => ({from: rule.from, to: rule.to}));


    if (this.state.rules.filter(rule => rule.error).length) return;

    onSave(state);
  }

  validate() {
    this.setState({
      rules: this.state.rules
        .map(rule => ({...rule, error: rule.from && !rule.to && 'to' || !rule.from && rule.to && 'from' || null})),
    });
  }

  onRemove(index) {
    this.setState(update(this.state, {
      rules: {
        $splice: [
          [index, 1],
        ],
      },
    }));
  }

  render() {
    const {rules} = this.state;

    console.log(this.state);

    return (
      <div>
        <p>
          Add 301 redirects to your website by entering the local path on the left (starting with '/')
          and the destination on the right (either starting with '/' for another local page or starting with
          http/https for redirecting to another website).
        </p>

        <FormGroup>
          <Button onClick={() => this.onAddNew()} color="primary">
            <span className="icon icon-add_circle"/> Add new
          </Button>
        </FormGroup>

        {rules && rules.map((rule, index) =>
          <FormGroup key={index}>
            <div className="form-inline rules">

              <Label>From: </Label>
              <Input
                className={classnames({invalid: rule.error && rule.error.from})}
                type="text"
                name={`from-${index}`}
                id={`from-${index}`}
                value={rule.from}
                onChange={e => this.onChange(e, index, 'from')}
              />
              <Label>To: </Label>
              <Input
                type="text"
                name={`to-${index}`}
                id={`to-${index}`}
                value={rule.to}
                onChange={e => this.onChange(e, index, 'to')}
              />

              <Button color="danger" onClick={() => this.onRemove(index)}>Remove</Button>
            </div>
          </FormGroup>,
        )}

        <Button onClick={() => this.onSave()} color="primary">
          Save
        </Button>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  redirect: state.settings.redirectSettings,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getRedirectSettings()),
    onSave: settings => dispatch(setRedirectSettings(settings)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Redirect);
