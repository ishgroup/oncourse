(function($){ 
	$.calendar = function(timetableMonthUrl) { 
		var monthYear = undefined;
		var monthYearChanged = false;
		var monthTimetable = {};

		jQuery("#timetable").datepicker({
			
			onNotifyUpdate: function (inst) { 
				
				var drawTimetable = function () {
					
					if(jQuery(".ui-datepicker-today .relative").size() == 0) {
						jQuery(".ui-datepicker-today").append('<div class="relative"></div>');
					}
					
					jQuery(".ui-datepicker-today .relative").append('<div class="today">Today</div>');
					
					jQuery(".center .ui-datepicker-calendar td a").each(function() {
						var day = jQuery(this).text();
						if (monthTimetable[day] != undefined) {
							var lessons = '';
							for (var i = 0; i != monthTimetable[day].sessions.length; i++) {
								lessons += '<span></span>';
							}
							jQuery(this).parent("td:first").append('<div class="relative"><div class="lesson">' + lessons + '</div></div>');
						}
		    		});
				}
				
				if (monthYear == undefined) {
					monthYear = jQuery.datepicker.formatDate('m-yy', jQuery("#timetable").datepicker("getDate"));
					monthYearChanged = !monthYearChanged;
				}
				
				if (monthYearChanged) {
					
					jQuery.getJSON(timetableMonthUrl + '/' + monthYear, function(data) {
						monthTimetable = data;
						drawTimetable();
						monthYearChanged = !monthYearChanged;
					});
				}
				else {
					drawTimetable();
				}
			},
			onSelect: function(dateText, inst) {
				var day = jQuery.datepicker.formatDate('d', jQuery.datepicker.parseDate('mm/d/yy', dateText));
				if (monthTimetable[day] != undefined) {
					jQuery("#dayInfo").html("");
					jQuery("#dayInfo").append('<div class="date-day1"><span>' + day + '</span>' + monthTimetable[day].month + '</div>');
					jQuery.each(monthTimetable[day].sessions, function(index) {
						var html = '<div class="class1 ' + ((index != monthTimetable[day].sessions.length -1) ? 'bord-r' : '') + '">';
						html += this.time + '<br/>';
						html += '<a href="' + this.href +  '">' + this.name + '</a><br />';
						
						if (this.room != undefined) {
							html += '<div class="class-place">' + this.room.siteName + '</div>';	
						}
						
						jQuery.each (this.tutors, function () {
							html += '<div class="class-man">' + this.tutorFullName + '</div>';
						}); 
						html += '</div>';
						
						jQuery("#dayInfo").append(html);		
					});
					jQuery("#dayInfo").append('<div class="cl mb20"></div>');
				}
			},
			onChangeMonthYear: function(y, m, inst) {
				monthYear = m + '-' + y;
				jQuery("#dayInfo").html("");
				monthYearChanged = !monthYearChanged;
			},
			showOtherMonths:true
		});
	};
})(jQuery);


