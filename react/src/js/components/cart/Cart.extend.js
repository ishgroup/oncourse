export default {
    render() {
        let count = this.props.courses.length,
            { CartOrderItem } = this.components;

        return (
            <div className="top-cart" onClick={this.methods.toggleShortList}>
                <aside id="headerToolbar" className="header-toolbar">
                    <div className="short-list" id="shortlist">
                        <div id="info" className="shortlistInfo clearfix">
                            <span>{count}</span>
                        </div>
                        {this.props.showedShortList && !!count && <div className="shortlistChoices dialogContainer">
                            <ul className="shortListOrder shortlistChoices" style={{display: 'block'}}>
                                {this.props.courses.map((course) => {
                                    return <CartOrderItem key={course.id} course={course} onRemove={this.methods.onRemoveCourse}/>;
                                })}
                                <li className="shortListOrderEnrol">
                                    <a className="shortlistLinkEnrol abtn gamma" href="/enrol/">Enrol</a>
                                </li>
                            </ul>
                            <div className="closeButton">X</div>
                        </div>}
                    </div>
                </aside>
            </div>
        );
    }
};