var slideShowShortlist = function() {
//console.log("showing shortlist");
	$j("div#shortlist ul").css("display","none").css("height","auto");
	       	
	shortListHeight = $j("div#shortlist ul").height();
	
	$j('div#shortlist div#shortlist_classes').css("height","auto");

	$j("div#shortlist ul")
		.css("height","10px")
		.css("display","block")
    	.animate({ height: shortListHeight + 10 }) 
    	.animate({ height: shortListHeight }, "fast"); 
    $j("a.toggle_shortlist").toggle();
};

/*function showHideNav(event) {
	var $target = $(event.target);
	if( $target.not("#nav > ul > li") ) {
		$target.children().slideToggle();
	}
}*/

function Linkify(inputText) {
    //URLs starting with http://, https://, or ftp://
    var replacePattern1 = /(\b(https?|ftp):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/gim;
    var replacedText = inputText.replace(replacePattern1, '<a href="$1" target="_blank">$1</a>');

    //URLs starting with www. (without // before it, or it'd re-link the ones done above)
    var replacePattern2 = /(^|[^\/])(www\.[\S]+(\b|$))/gim;
    var replacedText = replacedText.replace(replacePattern2, '$1<a href="http://$2" target="_blank">$2</a>');

    //Change email addresses to mailto:: links
    var replacePattern3 = /(\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,6})/gim;
    var replacedText = replacedText.replace(replacePattern3, '<a href="mailto:$1">$1</a>');

    return replacedText;
};


$j(document).ready(function() { 

	/* Advanced search panel */
	$j("div#search_box .show-advanced-search-slide").click(function() { 
		$j("#search_panel_advanced").slideToggle('fast');
		$j("#shortlist").slideToggle('slow');
		$j("div#search_box .show-advanced-search-slide").toggle();
	});
		
	//expandable subject heirarchy
	if ($j('div#block322').length) {
		$j( "div#block322 > .taggroup > ul > li:has(div)" ).addClass('hasChildren').not('.parent_tag').children('div').hide();
		$j( "div#block322 div.taggroup ul li h2 a" ).bind( "click", function expandoList() {
		
				$j(this).parent().parent().children('div').slideToggle();
				return false; //stop normal href from working
			} );
	}
	$j( "div#nav > ul > li > a" ).bind("click", function () {
		return false; //stop normal href from working
	} );	

	$j('#toggle_shortlist_show').live('click', slideShowShortlist);			
    $j("div#shortlist a#toggle_shortlist_hide").live('click', 
    	function(){
    		//var shortListHeight = $j("div#shortlist ul").height();
    		
	    	$j("div#shortlist ul")
	    		.animate({ height: "10px" }, "fast");
	    	$j("a.toggle_shortlist").toggle();
        }
	);	  

	$j("select#subject").linkselect();
	$j("#search_panel_advanced select").linkselect();
	
	$j.Juitter.start({
		searchType:"fromUser", // needed, you can use "searchWord", "fromUser", "toUser"
		searchObject:"SydneyCollege", // needed, you can insert a username here or a word to be searched for, if you wish multiple search, separate the words by comma.

		// The values below will overwrite the ones on the Juitter default configuration. 
		// They are optional here.
		// I'm changing here as a example only
		lang:"en", // restricts the search by the given language
		live:"live-15", // the number after "live-" indicates the time in seconds to wait before request the Twitter API for updates.
		placeHolder:"newsfeed", // Set a place holder DIV which will receive the list of tweets example <div id="juitterContainer"></div>
		loadMSG: "Loading messages...", // Loading message, if you want to show an image, fill it with "image/gif" and go to the next variable to set which image you want to use on 
		imgName: "loader.gif", // Loading image, to enable it, go to the loadMSG var above and change it to "image/gif"
		total: 3, // number of tweets to be show - max 100
		readMore: "", // read more message to be show after the tweet content
		nameUser:"text", // insert "image" to show avatar of "text" to show the name of the user that sent the tweet 
		openExternalLinks:"sameWindow", // here you can choose how to open link to external websites, "newWindow" or "sameWindow"
                filter:"sex->***,porn->****,fuck->****,shit->****"  // insert the words you want to hide from the tweets followed by what you want to show instead example: "sex->censured" or "porn->BLOCKED WORD" you can define as many as you want, if you don't want to replace the word, simply remove it, just add the words you want separated like this "porn,sex,fuck"... Be aware that the tweets will still be showed, only the bad words will be removed
	});

	$j('body.main-page div.image_banners div').cycle({ fx:'fade', speed:1000, pause:0, sync:true, random: 1 });		
	
	/* Testing for IE compatibility */
	if (!jQuery.support.htmlSerialize) { 
	
		//$j("div#block315 div.taggroup ul li h2 a").css("border","1px solid red");
		//DD_roundies.addRule('div#nav.dropdown > ul > li', '0, 0, 6px, 6px');
		//DD_roundies.addRule('div#nav.dropdown > ul > li:hover', '0 0 6px 6px');
		
		//DD_roundies.addRule('div#block315 > div.taggroup > ul > li > h2', '4px, 4px, 4px, 12px');
		//DD_roundies.addRule('#category_navigation ul li', '4px, 4px, 4px, 12px');
		//DD_roundies.addRule('div.left_panel div.panel_heading', '16px, 16px, 16px, 53px');
		//DD_roundies.addRule('div.right_panel div.panel_heading', '16px, 16px, 53px, 16px');
		//DD_roundies.addRule('#subcategory_nav > .taggroup > ul > li > h2', '0, 0, 8px, 8px');
		
		//DD_roundies.addRule('.side_panel', '0, 40px, 0, 0');
	 }
}); 