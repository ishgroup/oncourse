/**
 * Functions to manipulate with DOM
 */


/**
 * Scroll to top of the document
 */
export const scrollToTop = () => {
  window.scrollTo(0, 0);
};


/**
 * Scroll to block with validation messages
 */
export const scrollToValidation = () => {
  const el = document.querySelector('#oncourse-checkout .validation') as HTMLElement;
  window.scrollTo(0, offset(el).top - 5);
};


/**
 * Get element offsetTop
 */
const offset = (el: HTMLElement): {top: number} => {
  if (!el) return {top: 0};

  const rect = el.getBoundingClientRect();
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  return {top: rect.top + scrollTop};
};
