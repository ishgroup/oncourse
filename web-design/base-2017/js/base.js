/*
  This is a core javascript file which will be built into your website.

  All the require statements below will import further javascript, even though
  they look like comments.

  The resulting javascript will be merged, minified and zipped automatically into
  all.js and all.js.gz for serving to users. Don't try to edit those files since
  they will automatically be overridden.

  Google Closure compiler is used for minification.

*/


/*

  Another javascript bundle named dynamic.js will be served to all users before all.js is loaded.
  It contains:

  classnames 2.2.5
  react 0.14.8
  react-dom 0.14.8
  react-redux 5.0.1
  redux 3.6.0
  redux-thunk 2.1.0
  jquery 3.1.1

  It also contains onCourse code for driving the shopping basket and enrolment application.

  Do not include those libraries here and do not include anything incompatible with them
  such as other versions of the same libraries.
*/


/*
  Put your own javascript here
*/

//= require main.js

//= require vendor/assets.min.js

/*
  Additional vendor libraries which might be useful in your site
*/

//= require deprecated/jquery-migrate-3.0.0.js
//= require deprecated/jquery.browser.js
//= require deprecated/jquery-ui-1.9.1.min.js
//= require deprecated/jquery.customSelect.min.js


/*
  These files are due to be removed once the CMS application is rebuilt in 2017
*/

/*//= require deprecated/jquery.utils.js*/
//= require deprecated/jquery.validate.min.js
//= require deprecated/additional-methods.min.js


/*
  These are part of the onCourse application
  Don't remove them or the application may break
*/

//= require application.js
//= require gmap.js
//= require search.js
//= require shortlist.js
//= require forms.js
//= require socialMedia.js
//= require CoursesFilter.js