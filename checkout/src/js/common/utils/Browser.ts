export default class BrowserDetector {
  private browser;
  readonly unsupportedBrowsers;

  constructor() {
    this.browser = {};
    this.unsupportedBrowsers = {
      IE: 10
    };

    this._detectBrowser();
  }

  // tslint:disable-next-line:function-name
  private _detectBrowser() {
    this.browser = (function () {
      const ua = navigator.userAgent;
      let tem;
      let  M =
          ua.match(
            /(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*(\d+)/i
          ) || [];

      if (/trident/i.test(M[1])) {
        tem = /\brv[ :]+(\d+)/g.exec(ua) || [];
        return {name: "IE", version: tem[1] || ""};
      }

      if (M[1] === "Chrome") {
        tem = ua.match(/\b(OPR|Edge)\/(\d+)/);
        if (tem != null) {
          return {name: tem[1].replace("OPR", "Opera"), version: tem[2]};
        }
      }

      M = M[2] ? [M[1], M[2]] : [navigator.appName, navigator.appVersion, "-?"];

      if ((tem = ua.match(/version\/(\d+)/i)) != null) {
        M.splice(1, 1, tem[1]);
      }

      return {name: M[0], version: M[1]};
    })();
  }

  get isIE() {
    return this.browser.name === 'IE';
  }

  get isEdge() {
    return this.browser.name === 'Edge';
  }

  get isMicrosoft() {
    return this.isIE || this.isEdge;
  }

  get isFirefox() {
    return this.browser.name === 'Firefox';
  }

  get isChrome() {
    return this.browser.name === 'Chrome';
  }

  get isSafari() {
    return this.browser.name === 'Safari';
  }

  get isAndroid() {
    return /Android/i.test(navigator.userAgent);
  }

  get isBlackBerry() {
    return /BlackBerry/i.test(navigator.userAgent);
  }

  get isWindowsMobile() {
    return /IEMobile/i.test(navigator.userAgent);
  }

  get isIOS() {
    return /iPhone|iPad|iPod/i.test(navigator.userAgent);
  }

  get isMobile() {
    return (
      this.isAndroid || this.isBlackBerry || this.isWindowsMobile || this.isIOS
    );
  }

  isSupported() {
    if (this.unsupportedBrowsers.hasOwnProperty(this.browser.name)) {
      return +this.browser.version > this.unsupportedBrowsers[this.browser.name];
    }
    return true;
  }
}
