import {Field, SubmissionError} from 'redux-form';
import classnames from 'classnames';
import 'css/components/personForm.css';

const required = (value) => {
    return value ? false : 'Required';
};

const email = value => /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(value) ? false : 'Invalid email';

class PersonForm extends React.Component {

    onSubmit = (values) => {
        return new Promise((resolve) => {
            resolve();
        }).then(() => {
            if (typeof this.props.onValidate === 'function' && !this.props.onValidate(values)) {
                throw new SubmissionError({_error: 'User is already in the list'});
            }

            return this.props.onSubmit(values)
                .then(() => {
                    this.props.reset();
                }).catch((err) => {
                    throw new SubmissionError({_error: err.error});
                });
        });
    };

    renderField = ({input, label, type, meta: {error, touched}}) => {
        let showError = touched && error;
        return (
            <div className="person-form__field">
                <label>{label}</label>
                <div>
                    <input className={classnames('person-form__control', {
                        'person-form__error': showError
                    })} {...input} placeholder={label} type={type}/>
                    {showError && <div className="person-form__error">{error}</div>}
                </div>
            </div>
        );
    };

    render() {
        const {handleSubmit, error} = this.props;

        return (
            <form className="person-form" onSubmit={handleSubmit(this.onSubmit)}>
                <div>
                    <Field component={this.renderField} name="first_name" type="text" placeholder="First Name" label="First Name"
                           validate={required}/>
                    <Field component={this.renderField} name="last_name" type="text" placeholder="Last Name" label="Last Name"
                           validate={required}/>
                    <Field component={this.renderField} name="email" type="text" placeholder="Email" label="Email"
                           validate={[required, email]}/>
                </div>
                <div className="person-form__footer">
                    {!this.props.submitting && error && <div className="person-form__msg person-form__error">{error}</div>}
                    {this.props.submitting && <div className="person-form__msg">Request...</div>}
                    <input type="submit" value="Submit"/>
                </div>
            </form>
        );
    }
}

export default PersonForm;
