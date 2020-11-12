export interface GravatarOptions {
    size?: number;
    default?: '404' | 'mp' | 'identicon' | 'monsterid' | 'wavatar' | 'retro' | 'robohash' | 'blank';
    defaultUrl?: string;
    forcedefault?: 'y';
    rating?: 'g' | 'pg' | 'r' | 'x';
}
export declare const getGravatarUrl: (email: string, options?: GravatarOptions) => string;
