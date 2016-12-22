export default function() {
    return <input {...this.props} onChange={this.onChange} onBlur={this.onBlur} value={this.value}/>;
};
