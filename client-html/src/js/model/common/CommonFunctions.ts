/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export type StringArgFunction = (arg: string) => void;

export type BooleanArgFunction = (arg: boolean) => void;

export type NumberArgFunction = (arg: number) => void;

export type HTMLTagArgFunction = (arg: HTMLElement) => void;

export type DateArgFunction = (arg: Date) => void;

export type AnyArgFunction<R = void, A = any> = (...args: A[]) => R;

export type NoArgFunction = () => void;

export type PromiseReturnFunction = (...args: any[]) => Promise<any>;
