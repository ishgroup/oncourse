import * as React from 'react';

export default {
    render: function() {
        let { isAlreadyAdded, name, date } = this.props;

        let commonText, classDescription = [];

        if(isAlreadyAdded) {
            commonText = 'You\'ve already added this class to your shortlist. Do you want to proceed to checkout?';
        } else {
            commonText = 'Thanks for adding:';
            classDescription = [
                <p key="className" className="className">{name}</p>,
                <p key="classDate" className="classDate">{date}</p>
            ];
        }

        return (
            <div className="confirmOrderDialog dialogContainer" onClick={this.utils.stopPropagation}>
                <div className="confirm-message">
                    <strong className="confirm-txt">{commonText}</strong>
                    {classDescription}
                </div>
                <p className="confirm-proseed">
                    <a href="/enrol/" className="button">Proceed to Checkout</a>
                </p>
                <p className="confirm-close-wrapper">
                    <a className="button closeButton" onClick={this.methods.close}>Continue browsing</a>
                </p>
                <div className="closeButton" onClick={this.methods.close}>X</div>
            </div>
        );
    }
};
