import * as React from "react";
export declare type ButtonChildProps = Partial<React.DetailedHTMLProps<React.ButtonHTMLAttributes<HTMLButtonElement> & Record<string, any>, HTMLButtonElement>>;
export declare type TextAreaChildProps = Partial<React.DetailedHTMLProps<React.TextareaHTMLAttributes<HTMLTextAreaElement> & Record<string, any>, HTMLTextAreaElement>>;
export interface ChildProps {
    writeButton?: ButtonChildProps;
    previewButton?: ButtonChildProps;
    commandButtons?: ButtonChildProps;
    textArea?: TextAreaChildProps;
}
