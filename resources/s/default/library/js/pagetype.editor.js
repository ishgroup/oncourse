(function($){
	$.pageTypeEditor = function(blockIds) {
		jQuery(blockIds).sortable({ connectWith: ".connectedSortable", placeholder: "ui-state-highlight", 
			update: function (event, ui) {
				 
				 var items = $(blockIds).sortable('toArray');
				 var itemId = $(ui.item).attr('id');
				 
				 var weight = 0;
				 for (; weight < items.length; weight++) {
					 if (items[weight] == itemId) {
						 break;
					 }
				 }
				 
				 var params = {id: itemId, w:weight};
				 
				 if (ui.sender) {
                	 params['rg'] = $(ui.item).closest('ul').attr('id').substring(2); 
                 }
				 
				 $.post('/pt.sort', params, function(data) {});
		}}).disableSelection();
	}
})(jQuery);