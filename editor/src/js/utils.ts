import {DefaultConfig} from "./constants/Config";

export const createRootComponent = () => {
  const cmsId = DefaultConfig.CONTAINER_ID;

  if (document.getElementById(cmsId)) return;

  const rootDiv = document.createElement('div');
  rootDiv.id = cmsId;
  rootDiv.className = 'cms-scope';
  document.body.appendChild(rootDiv);
};

export const loadCmsCss = path => {
  const cmsCssId = "cms-css";
  if (document.getElementById(cmsCssId)) return;

  const head  = document.getElementsByTagName('head')[0];
  const link  = document.createElement('link');
  link.id    = cmsCssId;
  link.rel   = 'stylesheet';
  link.type  = 'text/css';
  link.href  = path;
  link.media = 'all';
  head.appendChild(link);
};

export class DOM {
  static findPage(title) {
    // The same rule with backend behavior
    const domTitle = title.replace(/ /g,'');

    const contentNode = document.getElementById('content');
    const pageNode = contentNode && contentNode.getElementsByClassName(`block-${domTitle}`)[0];

    if (pageNode) {
      return pageNode;
    }

    return null;
  }

  static findBlocks() {
    const blocks = document.querySelectorAll<HTMLElement>(".editor-block-class");

    return blocks.length ? blocks : null;
  }

  static findBlock(id) {
    const DOMBlock = document.getElementById(id);
    return DOMBlock;
  }
}

export class Browser {
  static isSafari() {
    // Safari 3.0+ "[object HTMLElementConstructor]"
    // @ts-ignore
    return /constructor/i.test(window['HTMLElement']) ||
      (function (p) { return p.toString() === "[object SafariRemoteNotification]"; })(!window['safari'] ||
        (typeof window['safari'] !== 'undefined' && window['safari'].pushNotification));
  }

  static isIE() {
    // Internet Explorer 6-11
    return /*@cc_on!@*/false || !!document['documentMode'];
  }

  static isFirefox() {
    // Firefox 1.0+
    return typeof window['InstallTrigger'] !== 'undefined';
  }

  static isOpera() {
    // Opera 8.0+
    return (!!window['opr'] && window['opr'].addons) || !!window['opera'] || navigator.userAgent.indexOf(' OPR/') >= 0;
  }

  static isChrome() {
    // Chrome 1+
    return !!window['chrome'] && !!window['chrome'].webstore;
  }

  static unsupported() {
    return this.isIE() || this.isSafari();
  }

}

export const getPort = () => window.location.protocol.includes("https") ? 443 : 80;