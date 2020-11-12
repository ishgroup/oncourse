import { ParseTree } from 'antlr4ts/tree/ParseTree';
export declare class DuplicateSymbolError extends Error {
}
export declare enum MemberVisibility {
    Invalid = -1,
    Public = 0,
    Protected = 1,
    Private = 2,
    Library = 3
}
export declare enum TypeKind {
    Integer = 0,
    Float = 1,
    String = 2,
    Boolean = 3,
    Date = 4,
    Class = 5,
    Array = 6,
    Alias = 7
}
export declare enum ReferenceKind {
    Irrelevant = 0,
    Pointer = 1,
    Reference = 2,
    Instance = 3
}
export interface Type {
    name: string;
    baseTypes: Type[];
    kind: TypeKind;
    reference: ReferenceKind;
}
export interface SymbolTableOptions {
    allowDuplicateSymbols?: boolean;
}
export declare class FundamentalType implements Type {
    name: string;
    readonly baseTypes: Type[];
    readonly kind: TypeKind;
    readonly reference: ReferenceKind;
    static readonly integerType: FundamentalType;
    static readonly floatType: FundamentalType;
    static readonly stringType: FundamentalType;
    static readonly boolType: FundamentalType;
    static readonly dateType: FundamentalType;
    constructor(name: string, typeKind: TypeKind, referenceKind: ReferenceKind);
    private typeKind;
    private referenceKind;
}
export declare class Symbol {
    name: string;
    context: ParseTree | undefined;
    constructor(name?: string);
    setParent(parent: Symbol | undefined): void;
    readonly parent: Symbol | undefined;
    readonly firstSibling: Symbol;
    readonly previousSibling: Symbol | undefined;
    readonly nextSibling: Symbol | undefined;
    readonly lastSibling: Symbol;
    readonly next: Symbol | undefined;
    removeFromParent(): void;
    resolve(name: string, localOnly?: boolean): Symbol | undefined;
    readonly root: Symbol | undefined;
    readonly symbolTable: SymbolTable | undefined;
    getParentOfType<T extends Symbol>(t: new (...args: any[]) => T): T | undefined;
    readonly symbolPath: Symbol[];
    qualifiedName(separator?: string, full?: boolean, includeAnonymous?: boolean): string;
    protected _parent: Symbol | undefined;
}
export declare class TypedSymbol extends Symbol {
    type: Type | undefined;
    constructor(name: string, type?: Type);
}
export declare class TypeAlias extends Symbol implements Type {
    readonly baseTypes: Type[];
    readonly kind: TypeKind;
    readonly reference: ReferenceKind;
    constructor(name: string, target: Type);
    private targetType;
}
export declare class ScopedSymbol extends Symbol {
    constructor(name?: string);
    readonly children: Symbol[];
    clear(): void;
    addSymbol(symbol: Symbol): void;
    removeSymbol(symbol: Symbol): void;
    getNestedSymbolsOfType<T extends Symbol>(t: new (...args: any[]) => T): T[];
    getAllNestedSymbols(name?: string): Symbol[];
    getSymbolsOfType<T extends Symbol>(t: new (...args: any[]) => T): T[];
    getAllSymbols<T extends Symbol>(t: new (...args: any[]) => T, localOnly?: boolean): Set<Symbol>;
    resolve(name: string, localOnly?: boolean): Symbol | undefined;
    getTypedSymbols(localOnly?: boolean): TypedSymbol[];
    getTypedSymbolNames(localOnly?: boolean): string[];
    readonly directScopes: ScopedSymbol[];
    symbolFromPath(path: string, separator?: string): Symbol | undefined;
    indexOfChild(child: Symbol): number;
    nextSiblingOf(child: Symbol): Symbol | undefined;
    previousSiblingOf(child: Symbol): Symbol | undefined;
    readonly firstChild: Symbol | undefined;
    readonly lastChild: Symbol | undefined;
    nextOf(child: Symbol): Symbol | undefined;
    private _children;
}
export declare class NamespaceSymbol extends ScopedSymbol {
}
export declare class BlockSymbol extends ScopedSymbol {
}
export declare class VariableSymbol extends TypedSymbol {
    constructor(name: string, value: any, type?: Type);
    value: any;
}
export declare class LiteralSymbol extends TypedSymbol {
    constructor(name: string, value: any, type?: Type);
    readonly value: any;
}
export declare class ParameterSymbol extends VariableSymbol {
}
export declare class RoutineSymbol extends ScopedSymbol {
    returnType: Type | undefined;
    constructor(name: string, returnType: Type);
    getVariables(localOnly?: boolean): VariableSymbol[];
    getParameters(localOnly?: boolean): ParameterSymbol[];
}
export declare enum MethodFlags {
    None = 0,
    Virtual = 1,
    Const = 2,
    Overwritten = 4,
    SetterOrGetter = 8,
    Explicit = 16
}
export declare class MethodSymbol extends RoutineSymbol {
    methodFlags: MethodFlags;
    visibility: MemberVisibility;
    constructor(name: string, returnType: Type);
}
export declare class FieldSymbol extends VariableSymbol {
    visibility: MemberVisibility;
    setter: MethodSymbol | undefined;
    getter: MethodSymbol | undefined;
    constructor(name: string, type: Type);
}
export declare class ClassSymbol extends ScopedSymbol implements Type {
    readonly baseTypes: Type[];
    readonly kind: TypeKind;
    readonly reference: ReferenceKind;
    isStruct: boolean;
    readonly superClasses: ClassSymbol[];
    constructor(name: string, referenceKind: ReferenceKind, ...superClass: ClassSymbol[]);
    getMethods(includeInherited?: boolean): MethodSymbol[];
    getFields(includeInherited?: boolean): FieldSymbol[];
    private referenceKind;
}
export declare class ArrayType extends Symbol implements Type {
    readonly baseTypes: Type[];
    readonly kind: TypeKind;
    readonly reference: ReferenceKind;
    readonly elementType: Type;
    readonly size: number;
    constructor(name: string, referenceKind: ReferenceKind, elemType: Type, size?: number);
    private referenceKind;
}
export declare class SymbolTable extends ScopedSymbol {
    readonly options: SymbolTableOptions;
    constructor(name: string, options: SymbolTableOptions);
    clear(): void;
    addDependencies(...tables: SymbolTable[]): void;
    removeDependency(table: SymbolTable): void;
    readonly info: {
        dependencyCount: number;
        symbolCount: number;
    };
    addNewSymbolOfType<T extends Symbol>(t: new (...args: any[]) => T, parent: ScopedSymbol | undefined, ...args: any[]): T;
    addNewNamespaceFromPath(parent: ScopedSymbol | undefined, path: string, delimiter?: string): NamespaceSymbol;
    getAllSymbols<T extends Symbol>(t?: new (...args: any[]) => T, localOnly?: boolean): Set<Symbol>;
    symbolWithContext(context: ParseTree): Symbol | undefined;
    resolve(name: string, localOnly?: boolean): Symbol | undefined;
    protected dependencies: Set<SymbolTable>;
}
