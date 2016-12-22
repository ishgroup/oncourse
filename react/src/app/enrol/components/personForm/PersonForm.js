import Form from 'app/common/components/form/Form';
import Field from 'app/common/components/form/Field';
import render from './personFormTpl';
import 'styles/enrol/components/personForm.css';

let validators = {
    required: (value) => {
        return !value ? 'required' : null;
    },

    email: (value) => {
        return /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(value) ?
            null : 'email';
    }
};

class PersonForm extends Form {

    submit = (e) => {
        e.preventDefault();

        let { data } = this.state,
            commonError = this.props.validate(data);

        if(commonError) {
            this.setState({
                error: commonError
            });
            return;
        }

        this.setState({
            submitting: true
        });

        this.props.submit(data).then(() => {
            this.reset();
        }, (error) => {
            let errorMsg = '';

            if(error.response) {
                errorMsg = error.response.data;
            } else {
                errorMsg = error.message;
            }

            this.setState({
                error: errorMsg
            });
        }).then(() => {
            this.setState({
                submitting: false
            });
        });
    };

    render() {
        return render.apply({
            submit: this.submit,
            validators: validators,
            submitting: this.state.submitting,
            invalid: this.state.invalid,
            error: this.state.error,
            Field
        });
    }
}

export default PersonForm;
