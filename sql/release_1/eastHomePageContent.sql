UPDATE WebContent set content = '<div id="image_breakout" class="rotating_banners"><div><a 
href="/courses?s=health"><img 
src="/s/img/header/homepage/sl_live4.png"/></a></div><div><a 
href="/courses/arts/drawing+&+painting"><img 
src="/s/img/header/homepage/sl_paint4.png"/></a> </div><div><a 
href="/AMEP"><img 
src="/s/img/header/homepage/sl_bus4.png"/></a></div></div>{block   
name:"Category navigation"}<div class="front-about"><h2>About 
Us</h2>{block name:"front_about"}</div><div class="front-blocks"><div 
class="front-block-search"><form name="search" action="/courses" 
method="get" class="searchbox-content"><div class="field-line"><input 
type="text" placeholder="Search for course" class="field-search" 
name="s" /></div><div class="field-line"><input name="day" 
value="weekday" type="checkbox" /><label>Weekdays</label><input 
name="day" type="checkbox" value="weekend" 
/><label>Weekends</label><input name="time" value="evening"   
type="checkbox" /><label>Evenings</label><input name="time" 
value="daytime" type="checkbox" /><label>Day</label></div><div 
class="field-line"><input default="postcode or suburb" 
class="suburb-autocomplete" autocomplete="off" size="18" name="near" 
type="text" placeholder="Location" /><input type="submit" value="" 
class="field-submit" /></div></form></div><div 
class="front-block-wr">{block name:"front_whatsnew"}{block 
name:"front_location"}{block name:"front_amep"}</div></div><div 
class="front-layout1"><div class="front-layout1-col1"><h2>City East 
for...</h2>{block name:"front_for"}</div><div 
class="front-layout1-col2"><h2>Testimonials</h2>{block 
name:"front_testimonials"}</div></div>

'
WHERE id = 2432;
UPDATE WebContent set content_textile = content where id = 2432;
