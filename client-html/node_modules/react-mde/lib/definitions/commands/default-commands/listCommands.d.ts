import { Command } from "../../types";
import { TextApi, TextState } from "../../index";
export declare type AlterLineFunction = (line: string, index: number) => string;
/**
 * Inserts insertionString before each line
 */
export declare function insertBeforeEachLine(selectedText: string, insertBefore: string | AlterLineFunction): {
    modifiedText: string;
    insertionLength: number;
};
export declare const makeList: (state0: TextState, api: TextApi, insertBefore: string | AlterLineFunction) => void;
export declare const unorderedListCommand: Command;
export declare const orderedListCommand: Command;
export declare const checkedListCommand: Command;
