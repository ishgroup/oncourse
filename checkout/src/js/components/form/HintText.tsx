import * as React from "react";
import {WrappedMouseHoverProps} from "./MouseHover";
import {WrappedFieldProps} from "redux-form";

export const HintText = (props: HintTextProps) => {
    const {mouseInside, hint} = props;

    return (
        <span className="validate-text">
            {mouseInside && hintText(hint)}
        </span>
    );
};

function hintText(hint) {
    if (!hint) {
        return null;
    }

    return (
        <span className="hint">
        {hint}
        <span className="hint-pointer"/>
    </span>
    );
}

interface HintTextProps extends Partial<WrappedFieldProps>, WrappedMouseHoverProps {
    hint?: string;
}

