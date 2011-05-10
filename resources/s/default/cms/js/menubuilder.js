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
		
		$('ul.cms_sortable').nestedSortable({
			disableNesting: 'no-nest',
			forcePlaceholderSize: true,
			handle: 'span',
			items: 'li',
			listType: 'ul',
			opacity: .6,
			placeholder: 'ui-state-highlight',
			tabSize: 25,
			tolerance: 'pointer',
			toleranceElement: '> span',
			update: function (event, ui) {
				var itemId = $(ui.item).attr('id').substring(2);
				var s = $('ul.cms_sortable').nestedSortable('toArray');
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
				
				$.post('/ma.sort', {id: itemId, pid: parent, w:index});
			}
			
		});
		
		$(".cms-menu-pages-dl a").live('click', function() {
			
			var elemID = this.id;
			
			var data = $.ajax({
				type: "POST",
				data: "id="+elemID.substring(2),
				  url: '/ma.remove',
				  async: false
				 }).responseText;
			if(data=="{status: 'OK'}"){
				var id = elemID.replace('r_', 'm_');
				$(".cms_navmenu_list #" + id).remove();
			}else{
				alert("The node with children cannot be removed.");
			}

		});
		
		$("a.cms_add_menuitem").click(function() {
			$.post('/ma.newpage', function (data) {
				var newitem = $('.cms_newmenuitem').clone(false);
				
				newitem.attr('id', 'm_'+data.id);
				newitem.find('.cms-menu-pages-n .editable').attr('id', 'n_' + data.id);
				newitem.find('.cms-menu-pages-url .editable').attr('id', 'u_' + data.id);
				newitem.find('.cms-menu-pages-dl a').attr('id', 'r_' + data.id);
				
				newitem.prependTo('.cms_navmenu_list').removeClass('hidden').removeClass('cms_newmenuitem');
				newitem.show();
				highlightMenuItem(newitem);
				cms_attachEditable(newitem.children("span:first"));
				newitem.children('.cms-menu-pages-url').trigger('click');
				
			}, 'json');
		});
		
		$('.cms_navmenu_list li > span').each(function() {
			cms_attachEditable($(this));
		});
		
		$('.cms_navmenu_list li > span .cms-ico-edit').click(function() {
			$(this).prev('.editable').click();
		});
		
		$('.cms_expander').live('click', function() {
			$(this).parent().filter(':first').siblings('ul').toggle();
		});
		
		$(".cms_navmenu_list li:has('ul > li')").addClass('hasChildren');//.children('ul').hide();
		//$( ".cms_navmenu_list li.hasChildren > span").prepend("<div class='cms_expander'><span class='ui-icon ui-icon-plusthick'></span></div>");
		
		function cms_attachEditable(newitem) {
			newitem.children('.cms-menu-pages-url').children('.editable').editable("/ma.save", { 
				indicator : "<img src='/s/img/indicator.gif'>",
				tooltip   : "",
				event     : "click",
				style  : "inherit",
				type	  : 'autocomplete'
			});
			
			newitem.children('.cms-menu-pages-n').children('.editable').editable("/ma.save", { 
				indicator : "<img src='/s/img/indicator.gif'>",
				tooltip   : "",
				event     : "click",
				style  : "inherit"
			});
		}
		
	};
})(jQuery);