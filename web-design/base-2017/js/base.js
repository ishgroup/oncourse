/*
  A dynamic library which contains pre-packaged
  functionality for driving dyanamic parts of the website.
  It includes:

  classnames 2.2.5
  react 0.14.8
  react-dom 0.14.8
  react-redux 5.0.1
  redux 3.6.0
  redux-thunk 2.1.0
  jquery 3.1.1
*/

//= require vendor/dynamic.js
//= require main.js

/*
  Additional vendor libraries which might be useful in your site

  jquery-migrate-3.0.0.js - we should keep the library in our stack until we get rid of jquery-ui-1.9.1
  or will upgrade it to latest jquery-ui
*/

//= require vendor/jquery-migrate-3.0.0.js
//= require vendor/jquery.browser.js
//= require vendor/jquery-ui-1.9.1.min.js
//= require vendor/vex-3.0.0.js
//= require vendor/vex.dialog-3.0.0.js
//= require vendor/assets.min.js


/*
  These files are due to be removed
*/

//= require deprecated/jquery.nyroModal-2.0.0.min.js
//= require deprecated/jquery.utils.js
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