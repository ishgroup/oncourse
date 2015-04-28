/*
	VERSION: Drop Shadow jQuery Plugin 1.6  12-13-2007
	With modifications by Ari Maniatis
*/

(function($){

	var dropShadowZindex = 6000;  //z-index counter

	$.fn.dropShadow = function(options)
	{
		// Default options
		var opt = $.extend({
			left: 4,
			top: 4,
			heightAdj: 0,
			blur: 2,
			opacity: .5,
			color: "black",
			swap: false
			}, options);
		var jShadows = $([]);  //empty jQuery collection
		
		// Loop through original elements
		this.not(".dropShadow").each(function()
		{
			var jthis = $(this);
			var shadows = [];
			var blur = (opt.blur <= 0) ? 0 : opt.blur;
			var opacity = (blur == 0) ? opt.opacity : opt.opacity / (blur * 8);
			var zOriginal = (opt.swap) ? dropShadowZindex : dropShadowZindex + 1;
			var zShadow = (opt.swap) ? dropShadowZindex + 1 : dropShadowZindex;
			//var heightAdj = opt.heightAdj;
			
			// Create ID for shadow
			var shadowId;
			if (this.id) {
				shadowId = this.id + "_dropShadow";
			}
			else {
				shadowId = "ds" + (1 + Math.floor(9999 * Math.random()));
			}

			// Modify original element
			$.data(this, "shadowId", shadowId); //store id in expando
			$.data(this, "shadowOptions", options); //store options in expando
			jthis
				.attr("shadowId", shadowId)
				.css("zIndex", zOriginal);
			if (jthis.css("position") != "absolute") {
				jthis.css({
					position: "absolute",
					zoom: 1 //for IE layout
				});
			}

			// Create first shadow layer
			bgColor = jthis.css("backgroundColor");
			if (bgColor == "rgba(0, 0, 0, 0)") bgColor = "transparent";  //Safari
			if (bgColor != "transparent" || jthis.css("backgroundImage") != "none" 
					|| this.nodeName == "SELECT" 
					|| this.nodeName == "INPUT"
					|| this.nodeName == "TEXTAREA") {		
				shadows[0] = $("<div></div>")
					.css("background", opt.color);								
			}
			else {
				shadows[0] = jthis
					.clone()
					.removeAttr("id")
					.removeAttr("name")
					.removeAttr("shadowId")
					.css("color", opt.color);
			}
			shadows[0]
				.addClass("dropShadow")
				.css({
					height: jthis.outerHeight() + opt.heightAdj,
					left: blur,
					opacity: opacity,
					position: "absolute",
					top: blur,
					width: jthis.outerWidth(),
					zIndex: zShadow
				});
				
			// Create other shadow layers
			var layers = (8 * blur) + 1;
			for (i = 1; i < layers; i++) {
				shadows[i] = shadows[0].clone();
			}

			// Position layers
			var i = 1;			
			var j = blur;
			while (j > 0) {
				shadows[i].css({left: j * 2, top: 0});           //top
				shadows[i + 1].css({left: j * 4, top: j * 2});   //right
				shadows[i + 2].css({left: j * 2, top: j * 4});   //bottom
				shadows[i + 3].css({left: 0, top: j * 2});       //left
				shadows[i + 4].css({left: j * 3, top: j});       //top-right
				shadows[i + 5].css({left: j * 3, top: j * 3});   //bottom-right
				shadows[i + 6].css({left: j, top: j * 3});       //bottom-left
				shadows[i + 7].css({left: j, top: j});           //top-left
				i += 8;
				j--;
			}

			// Create container
			var divShadow = $("<div></div>")
				.attr("id", shadowId) 
				.addClass("dropShadow")
				.css({
					left: jthis.position().left + opt.left - blur,
					marginTop: jthis.css("marginTop"),
					marginRight: jthis.css("marginRight"),
					marginBottom: jthis.css("marginBottom"),
					marginLeft: jthis.css("marginLeft"),
					position: "absolute",
					top: jthis.position().top + opt.top - blur,
					zIndex: zShadow
				});

			// Add layers to container	
			for (i = 0; i < layers; i++) {
				divShadow.append(shadows[i]);
			}
			
			// Add container to DOM
			jthis.after(divShadow);

			// Add shadow to return set
			jShadows = jShadows.add(divShadow);

			// Re-align shadow on window resize
			$(window).resize(function()
			{
				try {
					divShadow.css({
						left: jthis.position().left + opt.left - blur,
						top: jthis.position().top + opt.top - blur
					});
				}
				catch(e){}
			});
			
			// Increment z-index counter
			dropShadowZindex += 2;

		});  //end each
		
		return this.pushStack(jShadows);
	};


	$.fn.redrawShadow = function()
	{
		// Remove existing shadows
		this.removeShadow();
		
		// Draw new shadows
		return this.each(function()
		{
			var shadowOptions = $.data(this, "shadowOptions");
			$(this).dropShadow(shadowOptions);
		});
	};


	$.fn.removeShadow = function()
	{
		return this.each(function()
		{
			var shadowId = $(this).shadowId();
			$("div#" + shadowId).remove();
		});
	};


	$.fn.shadowId = function()
	{
		return $.data(this[0], "shadowId");
	};


	$(function()  
	{
		// Suppress printing of shadows
		var noPrint = "<style type='text/css' media='print'>";
		noPrint += ".dropShadow{visibility:hidden;}</style>";
		$("head").append(noPrint);
	});

})(jQuery);