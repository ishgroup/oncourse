/*

   onCourse javascript configuration


   Edit this file to control the behaviour of the browser application parts of onCourse.
   In particular, the checkout application drives the shopping basket, enrol buttons,
   places available, discounts and the whole checkout and ecommerce engine.

   The editor is the CMS tool which allows you to edit pages in your site.

*/

//
// Checkout configuration options
//
checkout_config = {

  // The checkout application can be relocated to any path in your site
  // You must create a CMS page at that location with the correct html
  checkoutPath: "/enrol/",

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

// Load the javascript for the checkout. This file is called dynamic.js
window.loadjs("/assets/dynamic.js");



//
// CMS editor configuration options
//
cms_config = {

  // The editor has its own styling, which you can adjust if you like
  cssPath: "/s/oncourse/editor/cms.css"
}

// The editor javascript should only be loaded when you are trying to login
// or have already logged into the CMS
if (document.location.hash === "#editor" || document.cookie.indexOf("editor=") != -1) {
  window.loadjs("/s/oncourse/editor/cms.js");
}