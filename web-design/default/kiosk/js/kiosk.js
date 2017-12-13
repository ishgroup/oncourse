window.onload = function() {
  var classRows = document.querySelectorAll(".kiosk .courses-today .col-row");

  for (var i = 0; i < classRows.length; i++) {
    /**
     * @var serverDateTime
     *
     * Contains date and time of the server in normal format
     * i.e. YYYY-MM-DD HH:MM:SS Format
     * e.g. 2017-12-6 15:26:36
     */
    var serverDateTime = classRows[i].getElementsByClassName('current-time')[0].innerText;

    /**
     * @var serverTime
     *
     * Contains parsed current time format of the server in milliseconds
     * This is used for comparison with current class time
     */
    var serverTime = new Date(serverDateTime).getTime();

    /**
     * @var classTime
     *
     * Contains parsed class time
     */
    var classTimeEl = classRows[i].getElementsByClassName('class-time')[0];
    var classTime = classTimeEl.getAttribute('data-time');
    classTime = classTime.substring(0, classTime.indexOf('+')).replace('T', ' ');

    /**
     * @var classStartTime
     *
     * Contains start time of the class
     */
    var classStartDate = new Date(classTime);
    classStartTime = classStartDate.getTime();

    classTimeEl.innerText = Kiosk.formatAMPM(classStartDate);

    /**
     * Calculates the time left from commencing
     * and display appropriate formatted data
     */
    Kiosk.calculateTimeFromCommencing(serverTime, classRows[i], classTimeEl, classStartTime, i);

    /**
     * Pushes class row into array for intervals
     * and calculating time left from commencing
     */
    Kiosk.classRows.push({
      serverTime: serverTime,
      classRow: classRows[i],
      classTimeEl: classTimeEl,
      classStartTime: classStartTime,
      key: i
    });
  }

  Kiosk.setKioskInterval();
};

window.Kiosk = {
  /**
   * Contains list of all the class rows
   *
   */
  classRows: [],

  /**
   * Contains list of all the set interval
   * which will be used in clear interval
   */
  intervalRows: [],

  /**
   * Formats the time into AM/PM format for displaying
   * start time on initial load
   *
   * @param string date
   * @return string
   */
  formatAMPM: function(date) {
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    hours = hours ? hours : 12;
    minutes = minutes < 10 ? '0' + minutes : minutes;

    return (hours + ':' + minutes + ' ' + ampm);
  },

  /**
   * Calculates the time left from commencing class
   *
   * @param integer serverTime
   * @param htmlObject classRow
   * @param htmlObject classTimeEl
   * @param integer classStartTime
   * @param integer key
   * @return void
   */
  calculateTimeFromCommencing: function(serverTime, classRow, classTimeEl, classStartTime, key) {
    var distance = classStartTime - serverTime;
    var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));

    if ((hours == 0) && (minutes < 60)) {
      classTimeEl.innerText = 'in ' + minutes + ' minutes';
      classRow.classList.add('course-starting');
    }

    if (distance < 0) {
      classRow.classList.remove('course-starting');
      classRow.classList.add('course-commenced');
      classTimeEl.innerText = 'started';
      if (Kiosk.intervalRows['intervalRow_' + key]) {
        clearInterval(Kiosk.intervalRows['intervalRow_' + key]);
      }
    }
  },

  /**
   * Sets different interval for class rows
   *
   * @return void
   */
  setKioskInterval: function() {
    Kiosk.classRows.forEach(function(classRow) {
      Kiosk.intervalRows['intervalRow_' + classRow.key] = window.setInterval(function() {
        Kiosk.classRows[classRow.key].serverTime = Kiosk.classRows[classRow.key].serverTime + 1000;
        Kiosk.calculateTimeFromCommencing(
          Kiosk.classRows[classRow.key].serverTime,
          classRow.classRow,
          classRow.classTimeEl,
          classRow.classStartTime,
          classRow.key
        );
      }, 1000);
    });
  }
};
