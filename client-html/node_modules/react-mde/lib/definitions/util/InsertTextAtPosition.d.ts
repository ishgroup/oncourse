/*!
 * The MIT License
   Copyright (c) 2018 Dmitriy Kubyshkin
   Copied from https://github.com/grassator/insert-text-at-cursor
 */
/**
 * Inserts the given text at the cursor. If the element contains a selection, the selection
 * will be replaced by the text.
 */
export declare function insertText(input: HTMLTextAreaElement | HTMLInputElement, text: string): void;
