export default function() {
    if(!this.students.length) {
        return null;
    }

    return (
        <div className="enrol-list">
            <div className="enrol-list__header">Student List</div>
            {this.students.map((item) => {
                return <div key={item.id} className="enrol-list__row">{item.first_name} {item.last_name} ({item.email})</div>;
            })}
        </div>
    );
}
