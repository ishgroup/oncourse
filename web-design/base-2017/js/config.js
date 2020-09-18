/*
  License: copyright ish group
  Purpose:
   onCourse javascript configuration


   Edit this file to control the behaviour of the browser application parts of onCourse.
   In particular, the checkout application drives the shopping basket, enrol buttons,
   places available, discounts and the whole checkout and ecommerce engine.

   The editor is the CMS tool which allows you to edit pages in your site.

*/

//
// Checkout configuration options
//
var checkout_config = {

  // The checkout application can be relocated to any path in your site
  // You must create a CMS page at that location with the correct html
  checkoutPath: "/checkout",

  // Can the user create new contacts in the checkout. If false, they can only use
  // an existing contact already in onCourse
  canCreateContact: {
    enrol: true,
    waitingList: true,
    mailingList: true
  },

  // Require guardian/parent details if the student is under this age
  guardianRequiredAge: 16,


  // Check the user ticks the 'accept terms' checkbox, the following link is available to them to see those terms
  // It can be a relative or absolute URL
  termsAndConditions: "/termsAndConditions",
  featureEnrolmentDisclosure: "/policies/enrolment",

  // After the user is shown the payment successful page, they can be redirected to another URL of your choosing
  // It can be a relative or absolute URL
  paymentSuccessURL: ""

}

//
// CMS editor configuration options
//
var cms_config = {
  "cssPath": "/s/oncourse-releases/editor/stable/editor.css?v=" + ciVersion
}

/*
 ******
 Loader
 ******

 The application lives inside /s/oncourse-release which isn't visible in
 your webDAV login. You can however put a file in the same path to have it
 picked up instead of the default.

 You really really need to know what you are doing if you choose to do this.

 /s/oncourse-releases/checkout/stable <-- the latest stable and tested release
 /s/oncourse-releases/checkout/beta <-- the current beta release which might be fully tested
*/


// Load the javascript for the checkout
window.loadjs("/s/oncourse-releases/checkout/stable/dynamic.js?v=" + ciVersion);


// The editor javascript should only be loaded when you are trying to login
// or have already logged into the CMS

if (document.location.hash === "#editor" || document.cookie.indexOf("editor=") != -1) {
  window.loadjs("/s/oncourse-releases/editor/stable/editor.js?v=" + ciVersion);
}
