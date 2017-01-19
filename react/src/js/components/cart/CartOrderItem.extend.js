export default {
    render() {
        let course = this.props.course;

        return (
            <li>
                <a href="/class/bushwalking.sydney-1">{course.name}</a>
                <span title="Remove item" className="deleteItem">
                    <a onClick={this.methods.remove}>X</a>
                </span>
                <div className="shortListOrderClasses">
                    <abbr title="" className="dtstart">{course.date_start}</abbr>
                    - <abbr title="" className="dtend">{course.date_end}</abbr>
                </div>
            </li>
        );
    }
};