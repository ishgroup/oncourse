export default {
    render() {
        let courseClass = this.props.courseClass;

        return (
            <li>
                <a href={`/class/${courseClass.course_id}`}>{courseClass.name}</a>
                <span className="deleteItem" title="Remove item">
                    <a onClick={this.methods.remove}>X</a>
                </span>
                {courseClass.start_date && courseClass.end_date &&
                    <div className="shortListOrderClasses">
                        <abbr className="dtstart" title="">{courseClass.start_date}</abbr>
                         - <abbr className="dtend" title="">{courseClass.end_date}</abbr>
                    </div>}
            </li>
        );
    }
};