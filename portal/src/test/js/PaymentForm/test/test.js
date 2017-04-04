let moment = require("./moment.min");

/**
 3/18
 03/18
 03/2018
 3/2018
 3-18
 3\18
 Mar-18
 March-18
 Mar-2018
 Mar 2018
 */

let testData = ["3/18", "03/18", "03/2018", "3/2018", "3-18","03-18","March-18", "Mar-2018", "Mar 2018", "3\\18", "03\\18"];
var DATE_FORMATS = ["M/YY", "M-YY", "M/YYYY", "M-YYYY",
    "MM/YY", "MM-YY", "MM/YYYY", "MM-YYYY",
    "MMM-YY","MMM YY", "MMM-YYYY", "MMM YYYY",
    "MMMM-YY", "MMMM YY", "MMMM-YYYY", "MMMM YYYY",
    "M\\YY", "MM\\YY", "MM\\YYYY", "M\\YYYY",
];

testData.forEach((s) => {
    let m = moment(s, DATE_FORMATS, true);
    console.log("Test:" + s + "-" + (m.isValid() ? "PASS":"FAIL"));
    console.log(m.format("MM/YY"));
});



