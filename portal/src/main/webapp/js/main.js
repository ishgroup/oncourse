/**
 * Project.
 * Version:
 */


var myScroll;
 function loaded() {
 myScroll = new iScroll('wrapper');
 }

 document.addEventListener('touchmove', function (e) {
 e.preventDefault();
 }, false);
 document.addEventListener('DOMContentLoaded', loaded, false);



/*
function loaded() {
	if(document.getElementById('footer')) {
		var body = window.innerHeight;
		var height = document.getElementById('footer').scrollHeight + 1;
		var size = body - height;
		document.getElementById('wrapper').style.height = size + 'px';
	}
}

document.addEventListener('DOMContentLoaded', loaded, false);*/
