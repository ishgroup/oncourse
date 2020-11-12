import { Parser, ParserRuleContext } from 'antlr4ts';
export declare type TokenList = number[];
export declare type RuleList = number[];
export declare class CandidatesCollection {
    tokens: Map<number, TokenList>;
    rules: Map<number, RuleList>;
}
export declare class CodeCompletionCore {
    showResult: boolean;
    showDebugOutput: boolean;
    debugOutputWithTransitions: boolean;
    showRuleStack: boolean;
    ignoredTokens: Set<number>;
    preferredRules: Set<number>;
    private parser;
    private atn;
    private vocabulary;
    private ruleNames;
    private tokens;
    private precedenceStack;
    private tokenStartIndex;
    private statesProcessed;
    private shortcutMap;
    private candidates;
    private static followSetsByATN;
    constructor(parser: Parser);
    collectCandidates(caretTokenIndex: number, context?: ParserRuleContext): CandidatesCollection;
    private checkPredicate;
    private translateToRuleIndex;
    private getFollowingTokens;
    private determineFollowSets;
    private collectFollowSets;
    private processRule;
    private atnStateTypeMap;
    private generateBaseDescription;
    private printDescription;
    private printRuleState;
}
