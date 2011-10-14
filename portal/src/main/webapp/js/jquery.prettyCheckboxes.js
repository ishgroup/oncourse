/* ------------------------------------------------------------------------
	prettyCheckboxes
	
	Developped By: Stephane Caron (http://www.no-margin-for-errors.com)
	Inspired By: All the non user friendly custom checkboxes solutions ;)
	Version: 1.1
	
	Copyright: Feel free to redistribute the script/modify it, as
			   long as you leave my infos at the top.
------------------------------------------------------------------------- */
	
	jQuery.fn.prettyCheckboxes = function(settings) {
		settings = jQuery.extend({
					checkboxWidth: 14,
					checkboxHeight: 14,
					className : 'prettyCheckbox',
					display: 'list'
				}, settings);

		jQuery(this).each(function(){
			// Find the label
			$label = jQuery('label[for="'+jQuery(this).attr('id')+'"]');

			// Add the checkbox holder to the label
			$label.prepend("<span class='holderWrap'><span class='holder'></span></span>");

			// If the checkbox is checked, display it as checked
			if(jQuery(this).is(':checked')) { $label.addClass('checked'); };

			// Assign the class on the label
			$label.addClass(settings.className).addClass(jQuery(this).attr('type')).addClass(settings.display);

			// Assign the dimensions to the checkbox display
			$label.find('span.holderWrap').width(settings.checkboxWidth).height(settings.checkboxHeight);
			$label.find('span.holder').width(settings.checkboxWidth);

			// Hide the checkbox
			jQuery(this).addClass('hiddenCheckbox');

			// Associate the click event
			$label.bind('click',function(){
				jQuery('input#' + jQuery(this).attr('for')).trigger('click');
				
				if(jQuery('input#' + jQuery(this).attr('for')).is(':checkbox')){
					jQuery(this).toggleClass('checked');
					
					jQuery(this).find('span.holder').css('top',0);
				}else{
					$toCheck = jQuery('input#' + jQuery(this).attr('for'));

					// Uncheck all radio
					jQuery('input[name="'+$toCheck.attr('name')+'"]').each(function(){
						jQuery('label[for="' + jQuery(this).attr('id')+'"]').removeClass('checked');	
					});

					jQuery(this).addClass('checked');
				};
				return false;
			});
			
			jQuery('input#' + $label.attr('for')).bind('keypress',function(e){
				if(e.keyCode == 32){
					if(jQuery.browser.msie){
						jQuery('label[for="'+jQuery(this).attr('id')+'"]').toggleClass("checked");
					}else{
						jQuery(this).trigger('click');
					}
					return false;
				};
			});
		});
	};
	
	checkAllPrettyCheckboxes = function(caller, container){
		if(jQuery(caller).is(':checked')){
			// Find the label corresponding to each checkbox and click it
			jQuery(container).find('input[type=checkbox]:not(:checked)').each(function(){
				jQuery('label[for="'+jQuery(this).attr('id')+'"]').trigger('click');
				if(jQuery.browser.msie){
					jQuery(this).attr('checked','checked');
				}else{
					jQuery(this).trigger('click');
				};
			});
		}else{
			jQuery(container).find('input[type=checkbox]:checked').each(function(){
				jQuery('label[for="'+jQuery(this).attr('id')+'"]').trigger('click');
				if(jQuery.browser.msie){
					jQuery(this).attr('checked','');
				}else{
					jQuery(this).trigger('click');
				};
			});
		};
	};