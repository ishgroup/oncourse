export default {
    render() {
        let isAdded = this.props.isAdded;

        return (
            <a title="Enrol in this class."
               className={this.utils.classnames('enrolAction abtn alpha', { 'enrol-added-class': isAdded })}
               onClick={isAdded ? null : this.methods.add}>{isAdded ? 'Added' : 'Enrol In This Class'}</a>
        );
    }
};