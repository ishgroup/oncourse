goog.provide('themeEditor');

goog.require('tinyscrollbar');


function editThemes() {
    var list = jQuery('#cmsThemeList');
    var key = jQuery('#cmsThemeKey');
    var themeId = jQuery('#cmsThemeKeyId');

    themeId.val(list.children('.selected').attr('rel'));
    list.children('.selected').clone().appendTo(key);
    key.toggle(function() {
        list.addClass('show');
    }, function() {
        list.removeClass('show');
    });
    list.children('span').click(
        function() {
            key.html('');
            jQuery(this).clone().appendTo(key);
            jQuery(this).siblings().removeClass('selected').end().addClass(
                'selected');
            key.click();
            themeId.val(jQuery(this).attr('rel'));
        });

    // Auto resize modules in table cell
    /*var themeLayout = function() {
     jQuery('.cms-theme-layout').find('.cms-theme-module').each(function(){
     var module = jQuery(this);
     var modH = module.parent('td').innerHeight() / (module.siblings('.cms-theme-module').size() + 1) - 5;

     module.css({'height':modH});
     });
     };
     themeLayout();*/

}

function sortThemes() {
    jQuery("#b_header, #b_left, #b_content, #b_right, #b_footer, #b_unassigned").sortable({
        connectWith: ".sortableThemes",
        stop: function(event, ui) {
            var itemId = jQuery(ui.item).attr('id').substring(3);
            var parent = jQuery(ui.item).parent();
            var s = parent.sortable('toArray');
            var index = 0;
            var parSubstr = parent.attr('id').substring(2);

            for (var i = 0; i < s.length; i++) {

                if (s[i].substring(3) == itemId) {
                    break;
                }

                index++;
            }

            jQuery.ajax({
                async: false,
                cache: false,
                type: "POST",
                data: "id="+itemId+"&region="+parSubstr+"&w="+index,
                url: '/cms/ui/internal/page.pagestructure.cmsnavigation.pagetypes.pagetypeedit.sort?t:cp=ui/internal/page',
                complete: function(data){
                    if(data.responseText=="{status: 'session timeout'}"){
                        window.location.reload();
                    } else {
                        jQuery("#scrollTheme").data().plugin_tinyscrollbar.update();
                    }
                }
            });

        }

    });
}