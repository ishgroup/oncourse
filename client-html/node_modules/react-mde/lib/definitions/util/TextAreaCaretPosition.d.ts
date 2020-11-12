export interface CaretCoordinates {
    top: number;
    left: number;
    lineHeight: number;
}
export declare function getCaretCoordinates(element: HTMLTextAreaElement, append?: string): CaretCoordinates;
