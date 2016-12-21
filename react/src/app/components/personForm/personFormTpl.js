const ERRORS = {
    required: 'Please, fill this field',
    email: 'Invalid email'
};

function renderField(field, meta) {
    return (
        <div className="person-form__field">
            <label>{field.label}</label>
            <div>
                <input className="person-form__control" {...field}/>
                {meta.touched && <div className="person-form__error">{ERRORS[meta.invalid]}</div>}
            </div>
        </div>
    );
}

export default function render() {
    const Field = this.Field;

    return (
        <form className="person-form" onSubmit={this.submit} ref="form">
            <div>
                <Field name="first_name" label="First Name" placeholder="First Name" validators={this.validators.required} render={renderField}/>
                <Field name="last_name" label="Last Name" placeholder="Last Name" validators={this.validators.required} render={renderField}/>
                <Field name="email" label="Email" placeholder="Email"  validators={[this.validators.required, this.validators.email]} render={renderField}/>
            </div>
            <div className="person-form__footer">
                {this.submitting && <div className="person-form__msg">Request...</div>}
                {this.error && <div className="person-form__msg person-form__error">{this.error}</div>}
                <input type="submit" value="Submit" disabled={!!this.invalid}/>
            </div>
        </form>
    );
}
