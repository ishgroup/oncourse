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
			toleranceElement: '> div',
			update: function (event, ui) {
				var itemId = $(ui.item).attr('id').substring(2);
				var s = $('ol.cms_sortable').nestedSortable('toArray');
				var parent = null, index = 0;
				
				for (var i=0; i< s.length; i++) {
					if (parent != s[i].parent_id) {
						parent = s[i].parent_id;
						index = 0;
					} 
					
					if(s[i].item_id == itemId) {
						break;
					} 
					
					index++;
				}
				
				$.post('/ma.sort', {id: itemId, pid: parent, w:index}, function(data) {});
			}
			
		});
		
		$(".cms_navmenu_list .cms_delete_icon a").live('click', function() {
			var elemID = this.id;
			response=$.post('/ma.remove', {id: elemID.substring(2)});
			if(response.responseText=="{status: 'OK'}"){
				var id = elemID.replace('r_', 'm_');
				$(".cms_navmenu_list #" + id).remove();
			}else{
				alert("this node cannot be deleted.");
			}
			
		});
		
		$("button.cms_add_menuitem").click(function() {
			$.post('/ma.newpage', function (data) {
				var newitem = $('.cms_newmenuitem').clone(false);
				
				newitem.attr('id', 'm_'+data.id);
				newitem.find('.cms_pagename').attr('id', 'n_' + data.id);
				newitem.find('.cms_refurl').attr('id', 'u_' + data.id);
				newitem.find('.cms_delete_icon a').attr('id', 'r_' + data.id);
				
				newitem.prependTo('.cms_navmenu_list').removeClass('hidden').removeClass('cms_newmenuitem');
				cms_attachEditable(newitem.children("div:first"));
				newitem.children('.cms_refurl').trigger('click');
			}, 'json');
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
			newitem.children('.cms_refurl').editable("/ma.save", { 
				indicator : "<img src='img/indicator.gif'>",
				tooltip   : "",
				event     : "click",
				style  : "inherit",
				type	  : 'autocomplete'
			});
			
			newitem.children('.cms_pagename').editable("/ma.save", { 
				indicator : "<img src='img/indicator.gif'>",
				tooltip   : "",
				event     : "click",
				style  : "inherit"
			});
		}
	};
})(jQuery);