import * as React from "react";
export interface ToolbarButtonProps {
    name: string;
    buttonComponentClass?: React.ComponentClass | string;
    buttonProps: any;
    buttonContent: React.ReactNode;
    onClick: React.MouseEventHandler<any>;
    readOnly: boolean;
}
export declare const ToolbarButton: React.FunctionComponent<ToolbarButtonProps>;
