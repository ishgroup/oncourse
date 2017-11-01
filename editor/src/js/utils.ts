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
  static findPage(id) {
    const pageNode = document.querySelector('.cms-edit-area');

    if (pageNode && pageNode.getAttribute('data-page') == id) {
      return pageNode;
    }

    return null;
  }
}
