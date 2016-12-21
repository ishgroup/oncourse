export default function() {
    const { PersonForm, StudentList } = this;

    return (
        <div className="enrol">
            <PersonForm submit={this.submit} validate={this.validate}/>
            <StudentList students={this.students}/>
        </div>
    );
};
