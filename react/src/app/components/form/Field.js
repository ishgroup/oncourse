import render from './fieldTpl';

class Field extends React.Component {

    static contextTypes = {
        register: React.PropTypes.func.isRequired,
        unregister: React.PropTypes.func.isRequired,
        setFieldState: React.PropTypes.func.isRequired
    };

    constructor(props, context) {
        super(props, context);
        this.state = {
            dirty: false,
            touched: false,
            invalid: this.validate(),
            value: null
        };
    }

    componentWillMount() {
        this.context.register(this);
    }

    componentWillUnmount() {
        this.context.unregister(this);
    }

    reset() {
        this.setState({
            dirty: false,
            touched: false,
            invalid: this.validate(),
            value: null
        });
        this.context.setFieldState(this.props.name, this.state.value, this.state.invalid);
    }

    validate(value) {
        let validators = this.props.validators,
            error = null;

        if(!validators) {
            return null;
        }

        if(!(validators instanceof Array)) {
            validators = [validators];
        }

        validators.every((validator) => {
            error = validator(value);
            return !error;
        });

        return error;
    }

    onChange = (e) => {
        let value = e.target.value,
            invalid = this.validate(value),
            onChange = this.props.onChange;

        this.setState({
            dirty: true,
            value: e.target.value,
            invalid: this.validate(value)
        }, () => {
            this.context.setFieldState(this.props.name, value, invalid);
            typeof onChange === 'function' ? onChange(e) : null;
        });
    };

    onBlur = (e) => {
        let onBlur = this.props.onBlur;
        this.setState({ touched: true });
        typeof onBlur === 'function' ? onBlur(e) : null;
    };

    render() {
        let renderField = this.props.render;

        if(typeof renderField === 'function') {
            return renderField({
                ...this.props,
                onChange: this.onChange,
                onBlur: this.onBlur,
                value: this.state.value
            }, {
                ...this.state
            });
        } else {
            return render.apply({
                props: this.props,
                onChange: this.onChange,
                onBlur: this.onBlur,
                value: this.state.value
            });
        }
    }
}

export default Field;
