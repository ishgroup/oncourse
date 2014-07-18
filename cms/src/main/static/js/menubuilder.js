goog.provide('menubuilder');

goog.require("main");
goog.require("jquery.jeditable");
goog.require("jquery.livequery");
goog.require("jquery.livesearch");
goog.require("jquery.mjs.nestedSortable");

(function($){
    $.menubuilder = function(results) {
        $.editable.addInputType('autocomplete', {
            element: function(settings, original) {
                var input = jQuery('<input />');
                input.autocomplete({
                    source: results
                }).data( "autocomplete" )._renderItem = function( ul, item ) {
                    return jQuery( "<li></li>" )
                        .data( "item.autocomplete", item )
                        .append( "<a>" + item.label + "<br>" + item.title + "</a>" )
                        .appendTo( ul );
                };
                $(this).append(input);
                return (input);
            }
        });

        $('ul.cms_sortable').nestedSortable({
            disableNesting: 'no-nest',
            forcePlaceholderSize: true,
            handle: 'span',
            items: 'li',
            listType: 'ul',
            opacity: .6,
            placeholder: 'ui-state-highlight',
            tabSize: 25,
            tolerance: 'pointer',
            toleranceElement: '> span',
            update: function (event, ui) {
                var itemId = $(ui.item).attr('id').substring(2);
                var s = $('ul.cms_sortable').nestedSortable('toArray');
                var parent = null, index = 0;

                for (var i=0; i< s.length; i++) {
                    if(s[i].item_id == itemId) {
                        parent=s[i].parent_id;
                        break;
                    }
                }
                for (var i=0; i< s.length; i++) {
                    if(s[i].item_id == itemId) {
                        break;
                    }
                    if (parent == s[i].parent_id) {
                        index++;
                    }
                }

                $.ajax({
                    type: "POST",
                    data: "id="+itemId+"&pid="+parent+"&w="+index,
                    url: '/ma.sort',
                    complete:function (data) {
                        if(data.responseText=="{status: 'session timeout'}"){
                            window.location.reload();
                        }
                    }
                });

            },
            //check menuItem name on duplicate childMenuItem name in menuItem where we want to put it 
            isAllowed: function (item, parent) {
                //get current menuItem name, id and error element
                var itemTextElement = $(item).find('span.cms-first > span.editable').first();
                var itemId = itemTextElement.attr('id');
                var itemName = itemTextElement.text();
                var itemErrorElement = $(item).find('.error').first();
                
                //check for the existence of "origError"(duplicate name)
                var origError = itemErrorElement.data('origError');
                if (!origError)
                {
                    itemErrorElement.data('origError', itemErrorElement.text());
                    //bind handler on MouseUp with action "drop menuItem" and unbind this handler if it happened
                    var handler = function(){
                        itemErrorElement.text(itemErrorElement.data('origError'));
                        itemErrorElement.removeData('origError');
                        $(item).unbind('mouseup', handler);
                    };
                    $(item).bind('mouseup', handler);
                }

                //get children of menuItem where we want to put it
                var childTextElements = $(parent).children('ul').find('span.cms-first > span.editable');

                var result = true;
                //check all children on duplicate name with current menuItem;
                $(childTextElements).each(function (index, childTextElement) {
                    var id = $(childTextElement).attr('id');
                    var name = $(childTextElement).text();
                    if (id != itemId && name == itemName) {
                        result = false;
                        return;
                    }
                });
                //handle error result on UI
                if (result) 
                {   
                    itemErrorElement.text(itemErrorElement.data('origError'));
                    itemErrorElement.removeData('origError');
                }
                else
                {
                    itemErrorElement.text('You cannot drop the menu item to this menu. Menu item with such name already exists.');
                }
                return result;
            }
        });


        $("a.cms_add_menuitem").click(function() {
            $.post('/ma.newpage', function (data) {
                if(data.status=="session timeout"){
                    window.location.reload();
                }else{
                    var newitem = $('.cms_newmenuitem').clone(false);

                    newitem.attr('id', 'm_'+data.id);
                    error=newitem.find('.error');
                    error.attr('id', 'm_' + data.id+ '_error');
                    error.text(data.warning);
                    //pass the name of the pagemenu
                    newitem.find('.cms-menu-pages-n .editable').attr('id', 'n_' + data.id).text(data.value);
                    newitem.find('.cms-menu-pages-url .editable').attr('id', 'u_' + data.id);
                    newitem.find('.cms-menu-pages-dl a').attr('id', 'r_' + data.id);
                    //set attributes which respond to delete this item
                    newitem.find('.cms-menu-pages-dl a').attr('onclick','showDeleteMenuConfirmation('+data.id+')');
                    newitem.find('.cms-menu-pages-dl div').attr('id','deleteMenuModal'+data.id);
                    //added new item to the end of list
                    newitem.appendTo('.cms_navmenu_list').removeClass('hidden').removeClass('cms_newmenuitem');
                    newitem.show();
                    highlightMenuItem(newitem);
                    cms_attachEditable(newitem.children("span:first"));
                    newitem.children('.cms-menu-pages-url').trigger('click');

                    //if newitem is located higher then window.top and lower then window.top + window.height we need to scroll the item
                    var win = $(window);
                    if (newitem.offset().top < win.scrollTop() || newitem.offset().top > (win.scrollTop() + win.height()) )
                    {
                        window.scrollTo(0, newitem.offset().top);
                    }
                }
            }, 'json');
        });

        $('.cms_navmenu_list li > span').each(function() {
            cms_attachEditable($(this));
        });

        $('.cms_navmenu_list li > span .cms-ico-edit').click(function() {
            $(this).prev('.editable').click();
        });

        $('.cms_expander').live('click', function() {
            $(this).parent().filter(':first').siblings('ul').toggle();
        });

        $(".cms_navmenu_list li:has('ul > li')").addClass('hasChildren');//.children('ul').hide();
        //$( ".cms_navmenu_list li.hasChildren > span").prepend("<div class='cms_expander'><span class='ui-icon ui-icon-plusthick'></span></div>");

        function cms_attachEditable(newitem) {
            newitem.children('.cms-menu-pages-url').children('.editable').editable(editMenuItem, {
                indicator : "<img src='/s/img/indicator.gif'>",
                tooltip   : "",
                event     : "click",
                style  : "inherit",
                onblur: 'submit',
                type	  : 'autocomplete'
            });

            editable = newitem.children('.cms-menu-pages-n').children('.editable')
            editable.editable(editMenuItem, {
                indicator : "<img src='/s/img/indicator.gif'>",
                tooltip   : "",
                event     : "click",
                onblur: 'submit',
                style  : "inherit",
                data: unescape
            });
        }

        /**
         * we use the function as "data" parameter in jquery.jeditable to convert escaped text to user friendly text
         */
        function unescape(text)
        {
            return $("<div/>").html(text).text();
        }

        function editMenuItem(value, settings){
            value = encodeURIComponent(value);

            var returned = eval('('+$.ajax({
                type: "POST",
                data: "id="+this.id+"&value="+value,
                dataType: "json",
                async:false,
                url:'/ma.save'
            }).responseText+')');
            $("#m_"+ returned.id+ "_error").text(returned.warning);

            return returned.value;
        }

    };
})(jQuery);