(function($){  
	$.menubuilder = function(results) {
		
		$.editable.addInputType('autocomplete', {
			  element: function(settings, original) {
			      var input = jQuery('<input />');
			      input.autocomplete({
			      	source: results, 
			      	change: function(event, ui) {
			      		// the following code does not actually work
			      		if (ui.item.pagename == "New menu item") {
			      			ui.item.pagename.val(getPageTitle);
			      		}
			      	}
					}).data( "autocomplete" )._renderItem = function( ul, item ) {
						return jQuery( "<li></li>" )
							.data( "item.autocomplete", item )
							.append( "<a>" + item.label + "<br>" + item.title + "</a>" )
							.appendTo( ul );
			      		};      
			      $(this).append(input);
			      return (input);
			  }
		});

		$('ol.cms_sortable').nestedSortable({
			disableNesting: 'no-nest',
			forcePlaceholderSize: true,
			handle: 'div',
			items: 'li',
			opacity: .6,
			placeholder: 'ui-state-highlight',
			tabSize: 25,
			tolerance: 'pointer',
			toleranceElement: '> div'
		});
		
		$(".cms_navmenu_list .cms_delete_icon a").click(function() {
			var id = this.id.replace('r_', 'm_');
			$(".cms_navmenu_list #" + id).remove();
		});
		
		$("button.cms_add_menuitem").click(function() {
			var newitem = $('.cms_newmenuitem').clone(false);
			newitem.prependTo('.cms_navmenu_list').removeClass('hidden').removeClass('cms_newmenuitem');
			cms_attachEditable(newitem);
			newitem.children('.cms_refurl').trigger('click');
		});
		
		$('.cms_navmenu_list li > div').each(function() {
			cms_attachEditable($(this));
		});
		
		$('.cms_expander').live('click', function() {
			$(this).parent().filter(':first').siblings('ol').toggle();
		});
		
		$(".cms_navmenu_list li:has('ol > li')").addClass('hasChildren').children('ol').hide();
		$( ".cms_navmenu_list li.hasChildren > div").prepend("<div class='cms_expander'><span class='ui-icon ui-icon-plusthick'></span></div>");
		
		function cms_attachEditable(newitem) {
			newitem.children('.cms_pagename').editable("/ma.save", { 
				indicator : "<img src='img/indicator.gif'>",
				tooltip   : "",
				event     : "click",
				style  : "inherit"
			});
			
			newitem.children('.cms_refurl').editable("/ma.save", { 
				indicator : "<img src='img/indicator.gif'>",
				tooltip   : "",
				event     : "click",
				style  : "inherit",
				type	  : 'autocomplete'
			});
		}
	};
})(jQuery);



	

	
	
	
	
	
	






