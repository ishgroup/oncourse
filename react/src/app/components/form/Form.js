import render from './formTpl';

class Form extends React.Component {

    constructor() {
        super();
        this.components = {};
        this.state = {
            data: {},
            errors: {},
            invalid: false,
            submitting: false
        };
    }

    static childContextTypes = {
        register: React.PropTypes.func.isRequired,
        unregister: React.PropTypes.func.isRequired,
        setFieldState: React.PropTypes.func.isRequired
    };

    getChildContext() {
        return {
            register: this.register,
            unregister: this.unregister,
            setFieldState: this.setFieldState
        };
    }

    register = (component) => {
        this.components[component.props.name] = component;
        this.setFieldState(component.props.name, component.state.value, component.state.invalid);
    };

    unregister = (component) => {
        let errors = {...this.state.errors};

        delete this.components[component.props.name];
        delete errors[component.props.name];

        this.setState({ errors });
    };

    setFieldState = (name, value, invalid) => {
        let invalidForm = false,
            errors = {
                ...this.state.errors,
                [name]: invalid
            };

        for(let error in errors) {
            invalidForm = !!errors[error];
            if(invalidForm) {
                break;
            }
        }

        this.setState({
            data: {
                ...this.state.data,
                [name]: value
            },
            errors,
            invalid: invalidForm
        });
    };

    reset() {
        let components = this.components;

        this.refs.form && this.refs.form.reset();

        this.setState({
            data: {},
            errors: {},
            invalid: false
        });

        for(let cmp in components) {
            components[cmp].reset();
        }
    }

    render() {
        return render.apply({
            ...this.props
        });
    }
}

export default Form;
