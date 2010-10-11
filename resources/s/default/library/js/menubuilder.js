(function($){  
	$.menubuilder = function() {
		$( ".cms_sortable" ).sortable({
			revert: true,
			helper: "clone",
			revert: "invalid",
			handle: ".cms_draghandle"
		});
		
		$("button.cms_add_menuitem").click(function() {
			var newitem = $('.cms_newmenuitem').clone(false);
			newitem.prependTo('.cms_navmenu_list').removeClass('hidden').removeClass('cms_newmenuitem');
			cms_attachEditable(newitem);
			newitem.children('.cms_refurl').trigger('click');
		});
		
		$('.cms_navmenu_list li').each(function() {
			cms_attachEditable($(this))
		});
		
		$('.cms_expander').live('click', function() {
			$(this).siblings('ul').toggle();
		});
		
		$(".cms_navmenu_list li:has('ul')" ).addClass('hasChildren').children('ul').hide();
		$( ".cms_navmenu_list li.hasChildren" ).prepend("<div class='cms_expander'><span class='ui-icon ui-icon-plusthick'></span></div>");
		
		function cms_attachEditable(newitem) {
			newitem.children('.cms_pagename').editable("/submitchange", { 
				indicator : "<img src='img/indicator.gif'>",
				tooltip   : "",
				event     : "click",
				style  : "inherit"
			});
			
			newitem.children('.cms_refurl').editable("/submitchange", { 
				indicator : "<img src='img/indicator.gif'>",
				tooltip   : "",
				event     : "click",
				style  : "inherit",
				type	  : 'autocomplete'
			});
		}
		
		function add_menu_item() {
			alert("!!!");
		}
	}
	
	$.editable.addInputType('autocomplete', {
		  element: function(settings, original) {
		      var input = jQuery('<input />');

				// these are all the URL aliases + pages in the site
				var results = [
					{
						value: "44",
						label: "/page/44",
						title: "About us",
					},
					{
						value: "45",
						label: "/page/46",
						title: "Contact us",
					},
					{
						value: "46",
						label: "/page/46",
						title: "Policies",
					},
					{
						value: "44",
						label: "/about",
						title: "About us",
					},
					{
						value: "46",
						label: "/about/contactus",
						title: "Contact us",
					},
					{
						value: "47",
						label: "/policies",
						title: "Policies",
					}
				];
				
		      input.autocomplete({
		      	source: results, 
		      	change: function(event, ui) {
		      		// the following code does not actually work
		      		if (ui.item.pagename == "New menu item") {
		      			ui.item.pagename.val(getPageTitle);
		      		}
		      	}
				}).data( "autocomplete" )._renderItem = function( ul, item ) {
					return $( "<li></li>" )
						.data( "item.autocomplete", item )
						.append( "<a>" + item.label + "<br>" + item.title + "</a>" )
						.appendTo( ul );
		      		};      
		      $(this).append(input);
		      return (input);
		  }
	});
})(jQuery);

	

	
	
	
	
	
	






