/*!
  Copyright (c) 2018 Jed Watson.
  Licensed under the MIT License (MIT), see
  http://jedwatson.github.io/classnames
*/
export interface ClassArray extends Array<ClassValue> {
}
export interface ClassDictionary {
    [id: string]: string | boolean;
}
export declare type ClassValue = string | ClassDictionary | ClassArray | undefined | null;
export declare function classNames(...classValues: ClassValue[]): string;
