
/*$j(document).ready(function() {
    $j("div.advanced-search-button-slide").click(
    	function(){ $j("div#advanced_search_container_slide").animate({ height: "150px" }); $j("div.search").toggle()
        	;}); $j("div#hide_button").click(
            	function(){
        		$j("div#advanced_search_container_slide").animate({
            		height: "0px"
        });
   });       
});*/


var slideShowShortlist = function() {
//console.log("showing shortlist");
	$j("div#shortlist ul").css("display","none").css("height","auto");
	       	
	shortListHeight = $j("div#shortlist ul").height();

	$j("div#shortlist ul")
		.css("height","0")
		.css("display","block")
    	.animate({ height: shortListHeight + 10 }) 
    	.animate({ height: shortListHeight }, "fast"); 
    $j("a.toggle_shortlist").toggle();
};

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
	//expandable subject heirarchy
	$j( "div#block216 .taggroup > ul > li:has(div)" ).addClass('hasChildren').not('.parent_tag').not('.active_tag').children('div').hide();
	
	// Add a toggler span
	$j( "div#block216 .hasChildren > h2" ).append("<span class=\"expander\"></span>");
	$j( "div#block216 .hasChildren:has(div)" ).append("<span class=\"expander\"></span>");
	
	$j( "div#block216 .hasChildren span.expander" ).bind( "click", function expandoList() {
		//alert("!!!");
		$j(this).parent().children('div').slideToggle("fast");
		return false; //stop normal href from working
	});

	$j('#toggle_shortlist_show').live('click', slideShowShortlist);
		
    $j("div#shortlist a#toggle_shortlist_hide").live('click', 
    	function(){
    		//var shortListHeight = $j("div#shortlist ul").height();
    		
	    	$j("div#shortlist ul")
	    		.animate({ height: "0px" }, "fast");
	    	$j("a.toggle_shortlist").toggle();
        }
	);	        	

    $j("div.advanced-search-button-slide").click(
        function(){ $j("div#advanced_search_container_slide").animate({ height: "150px" }) .animate({
        height: "140px" }, "fast"); $j("div.advanced-search-button-slide").toggle();
            }); $j("div#advanced-search-button-slide").click(function(){
        $j("div#advanced_search_container_slide").animate({
            height: "0px"}, "fast"); 
      });       
      
    $j("a#cancel-search").click(
    	function(){
    		$j("div#advanced_search_container_slide").hide();
		}
	);
	
	$j("div.advanced-search-button-slide-cce").click(function () {
		$j("div#advanced_search_container_slide").toggle();
	});
	
	$j("#shortlist_classes .menu #enrolNowLink").text("Enrol");
	
	// Front page slider 
	$j("#frontpage_slider").cycle({ 
	    fx:     'slideX', 
	    speed:  'slow', 
	    timeout: 10000, 
	    pager:  '#frontpage_slider_pager',
		random: 1
	});
		
	//sitewise advertising block - set random: 1 to randomise, 0 to display in defined order
	$j('div#block213').cycle({ fx:'fade', speed:1000, pause:0, sync:true, random: 1 });

	// Twitter feed
	/*$j.Juitter.start({
		searchType:"fromUser", // needed, you can use "searchWord", "fromUser", "toUser"
		searchObject:"ccesydney", // needed, you can insert a username here or a word to be searched for, if you wish multiple search, separate the words by comma.

		// The values below will overwrite the ones on the Juitter default configuration. 
		// They are optional here.
		// I'm changing here as a example only
		lang:"en", // restricts the search by the given language
		live:"live-15", // the number after "live-" indicates the time in seconds to wait before request the Twitter API for updates.
		placeHolder:"newsfeed", // Set a place holder DIV which will receive the list of tweets example <div id="juitterContainer"></div>
		loadMSG: "Loading messages...", // Loading message, if you want to show an image, fill it with "image/gif" and go to the next variable to set which image you want to use on 
		imgName: "loader.gif", // Loading image, to enable it, go to the loadMSG var above and change it to "image/gif"
		total: 3, // number of tweets to be show - max 100
		readMore: "More...", // read more message to be show after the tweet content
		nameUser:"text", // insert "image" to show avatar of "text" to show the name of the user that sent the tweet 
		openExternalLinks:"sameWindow", // here you can choose how to open link to external websites, "newWindow" or "sameWindow"
                filter:"sex->***,porn->****,fuck->****,shit->****"  // insert the words you want to hide from the tweets followed by what you want to show instead example: "sex->censured" or "porn->BLOCKED WORD" you can define as many as you want, if you don't want to replace the word, simply remove it, just add the words you want separated like this "porn,sex,fuck"... Be aware that the tweets will still be showed, only the bad words will be removed
	});*/
			
});

