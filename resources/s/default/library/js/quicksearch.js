jQuery.fn.quickSearch = function(url, settings) {
	return this.each( function() {
		var minInput = 3;
		var textInput = $j(this);
		var divContainer = $j('.quicksearch-wrap');
		var thisObject = this;
		var selectedIndex = -1;
		
		// function triggered after contents is updated
		updatedFunction = function() {
			show();
			
			// observe checkboxes
			$j('.suburb-choice').each(function() {
				$j(this).bind("click", updateFunction);
			});
		};
		
		loadFunction = function(url, params, callbackFunction) {
			selectedIndex = -1;
			divContainer.load(url, params, callbackFunction);
		};
		
		// function for updating the list via site selections
		updateFunction = function withToggle(event) {
			var srcElement = null;
			if (event && (srcElement = event['srcElement'])) {
				
				// build array of query params
				var params = [];
				$j('.suburb-choice:checked').each(function() {
					var me = $j(this);
					var val = me.attr('value');
					params.push(val);
				});
				textInput.addClass('throbber');
				var terms = textInput.val();
				loadFunction(url, {text: terms, suburb: params}, updatedFunction);
			}
		};
		
		// take terms and query the server-side
		matchesFunction = function getMatches(terms) {
			if (!terms || terms.length < minInput) {
				hide();
				return;
			}
			textInput.addClass('throbber');
			loadFunction(url, {text: terms}, updatedFunction);
		};
		
		function show() {
			textInput.removeClass('throbber');
			$j('div.advanced-search-button').hide();
			hideAdvancedSearch();
			divContainer.removeShadow();
			divContainer.slideDown('slow', function() {
				divContainer.dropShadow();
			});
			
			// install watcher to detect clicks on the background
			$j(document).bind('mousedown.quicksearch', function(click) {
				// hide quicksearch if click was not inside quicksearch area
				if ($j(click.target).parents('div.quicksearch-wrap').length == 0 && click.target != thisObject) {
					hide();
				}
			});
		}
		
		function hide() {
			selectedIndex = -1;
			allItems = null;
			textInput.removeClass('throbber');
			divContainer.removeShadow();
			$j(document).unbind('mousedown.quicksearch');
			divContainer.slideUp(400);
			$j('div.advanced-search-button').show();
		}
		
		textInput.keyup(function(key) {
			if (key.which == 27) { // escape
				hide();
				return false;
			}

			// copy the text of the quick search field into the advanced search field
			$j("#adv_keyword").val($j(this).attr('value'));

			oldIndex = selectedIndex;
			allItems = divContainer.children('div').children('ul').children('li').not('.title');
			selectedItem = null;
			if (allItems.size() > 0) {
				if (selectedIndex >= 0) {
					selectedItem = allItems.eq(selectedIndex);
				}
				
				if (key.which == 13) { // return key
					if (selectedItem == null) {
						$j('form#search').submit();
						return true;
					} else {
						window.location = selectedItem.children('a').attr('href');
						key.preventDefault();
						return false;
					}
				}
				

				if (allItems.size() == 1 && (key.which == 40 || key.which == 9 || key.which == 38)) {
					selectedIndex = 0;
				}
				else
				{
					if (key.which == 40 || key.which == 9) { // down key
						if (selectedIndex >= (allItems.size() - 1)) {
							selectedIndex = 0;
						} else {
							selectedIndex++;
						}
					}
					
					if (key.which == 38) { // up key
						if (selectedIndex <= 0) {
							selectedIndex = allItems.size() - 1;
						} else {
							selectedIndex--;
						}
					}
					// update if necessary
					if (selectedIndex != oldIndex) {
						if (selectedItem != null) {
							selectedItem.removeClass('selected');
						}
						
						allItems.eq(selectedIndex).addClass('selected');
					}
				}
				
				return false;
			}
			return true;
		});
		
		// observe quicksearch field delay == 1.0seconds
		// depends on jquery-util/query.utils[.min].js
		textInput.delayedObserver(function() {
			matchesFunction(textInput.val());
		}, 1.0);
	});
};

jQuery.fn.hideQuickSearch = function(url, settings) {
	return this.each( function() {
		this.hide();
	})
};
