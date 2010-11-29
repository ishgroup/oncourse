(function($){
	$.pageTypeEditor = function(blockIds) {
		jQuery(blockIds).sortable({ connectWith: ".connectedSortable", placeholder: "ui-state-highlight", 
			receive: function (event, ui) {
				$.post('/pt.sort', {id: $(ui.item).attr('id'), w:$(ui.item).index(), rg:$(ui.sender).attr('id')}, function(data) {});
			},
			update: function (event, ui) {
				 $.post('/pt.sort', {id: $(ui.item).attr('id'), w:$(ui.item).index()}, function(data) {});
				 
		}}).disableSelection();
	}
})(jQuery);