/**
 * Project.
 * Version:
 */

// Load All
jQuery(document).ready(function() {

    jQuery('.internal-page #sidebarRight .courses-list-sub li a').toggle(function() {
        jQuery('#sidebarRight .courses-list-sub li a.active').click();
        jQuery(this).addClass('active').siblings('ul').slideDown('slow');
        return false;
    }, function(){
        jQuery(this).removeClass('active').siblings('ul').slideUp('slow');
        return false;
    });

    jQuery('.content div.image_banners div').cycle({ fx:'fade', speed:1000, pause:0, sync:true, random: 1 });
});
