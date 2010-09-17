<?php

	$college = $_REQUEST['s'];

?><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
<head>
	<title><?php print $college; ?></title>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<link rel="stylesheet" href="/s/css/styles.css" type="text/css">
	<link rel="stylesheet" href="/s/css/site.css" type="text/css">
	<link rel="icon" href="/s/img/favicon.png" type="image/png">
</head>
<body>
<div id="popup-header">
	<h1><span><?php print $college; ?></span></h1>
</div>
<div id="popup-content">
	<h2><?php print $college; ?> copyright notice</h2>
	
	<p>The course descriptions, images and other information on this site are created and maintained by <?php print $college; ?>. This site is designed, programmed and maintained by ish group pty ltd (ish). The processes, designs, images and text may be protected by patent, copyright or other intellectual property rights held by ish and/or <?php print $college; ?>. Permission must be obtained for any use of the material or processes used on this site.</p>
	
	<p class="winclose"><a href="#" onclick="window.close();">close this window</a></p>
</div>
</body>
</html>
