<?php

	$error = '';
	$sent = 0;
	
	$course = $_REQUEST['t'];
	$classid = $_REQUEST['c'];
	$college = $_REQUEST['s'];
	$siteurl = $_SERVER['HTTP_HOST'];

	if (isset($_REQUEST['submit'])) {
		
		if (preg_match(' /[\r\n,;\'"]/ ', $sendername)) exit('Invalid');
		if (preg_match(' /[\r\n,;\'"]/ ', $senderemail)) exit('Invalid');
		if (preg_match(' /[\r\n,;\'"]/ ', $friendname)) exit('Invalid');
		if (preg_match(' /[\r\n,;\'"]/ ', $friendemail)) exit('Invalid');
		
		if ($sendername == '') 
			$error .= "Sender Name is required. ";
			
		if ($senderemail != "" && !ereg('^[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\.([a-zA-Z]{2,4})+([.a-zA-Z]{0,5})$', $senderemail))
			$error .= "Sender Email is not valid. ";
			
		if ($friendname == '') 
			$error .= "Friend Name is required. ";
			
		if ($friendemail != "" && !ereg('^[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\.([a-zA-Z]{2,4})+([.a-zA-Z]{0,5})$', $friendemail))
			$error .= "Friend Email is not valid.";
	
			
		if ($error == '') {

			$to = $friendname." <".$friendemail.">";
			$subject = $college." Course: ".$course;
			$body = "Dear ".$friendname.",\r\n";
			$body.= $sendername." has sent you this link from the ".$college." web site. We respect your privacy. Your details have not been added to any mailing list.\r\n\r\n";
			$body.= $college."\r\n\r\n";
			$body.= "Course: ".$course."\r\n\r\n";
			$body.= "http://".$siteurl."/class/".$classid."\r\n\r\n";
			$body.= stripslashes($message);
			$headers = "From: ".$sendername." <".$senderemail.">\r\n".
				"X-Mailer: phpmail";
			if (mail($to, $subject, $body, $headers)) {
				$sent = 1;
			}
		
		}
	}

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
	<h2>Email to a friend</h2>
	<h3><?php print $course; ?></h3>
	
	<?php if ($error != '') print "<div style=\"margin:1em 0;padding:5px 10px;color:#900;background:#FFF5EE;border:#900;\"><strong>Error:</strong> ".$error."</div>"; ?>
	<?php if ($sent != 0) print "<div style=\"margin:1em 0;padding:5px 10px;color:#06A;background:#EEF6FF;border:#06A;\"><strong>Message Sent!</strong></div>"; ?>
	
	<form action="" method="post" id="feedbackform">
	<input type="hidden" name="t" value="<?php print $course; ?>">
	<input type="hidden" name="c" value="<?php print $classid; ?>">
	<dl>
		<dt><label for="sendername">Your Name</label> <em title="This field is required">*</em></dt>
		<dd><input type="text" name="sendername" id="sendername" class="input-fixed" value="<?php print $_REQUEST['sendername']; ?>" class="textfield"></dd>
		
		<dt><label for="senderemail">Your Email</label> <em title="This field is required">*</em></dt>
		<dd><input type="text" name="senderemail" id="senderemail" class="input-fixed" value="<?php print $_REQUEST['senderemail']; ?>" class="textfield"></dd>
		
		<dt><label for="friendname">Friend&#8217;s Name</label> <em title="This field is required">*</em></dt>
		<dd><input type="text" name="friendname" id="friendname" class="input-fixed" value="<?php print $_REQUEST['friendname']; ?>" class="textfield"></dd>
		
		<dt><label for="friendemail">Friend&#8217;s Email</label> <em title="This field is required">*</em></dt>
		<dd><input type="text" name="friendemail" id="friendemail" class="input-fixed" value="<?php print $_REQUEST['friendemail']; ?>" class="textfield"></dd>
		
		<dt><label for="message">Message</label></dt>
		<dd><textarea name="message" id="message" class="textarea-fixed"><?php print stripslashes($_REQUEST['message']); ?></textarea></dd>
	</dl>
	<div><input type="submit" name="submit" class="btn" value="Send"></div>
	</form>
	<p class="winclose"><a href="#" onclick="window.close();">close this window</a></p>
</div>
</body>
</html>
