/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

window.onload = function () {
  choose(window.location.href.toString());
};

var selectionImage;

function choose(url) {
  var f = url.split("/").slice(-1)[0].split("?")[0];
  if (f.match(/#/g) && f.match(/#/g).length > 0) {
    f = f.split("#")[0];
  }
  if (f !== "") {
    $('div.non-sidebar').empty();
    $('div.non-sidebar').load("operations/" + f + ".html", function () {
      goToAnchor();
    });
  }
}

function goToAnchor() {
  var doARead = $($('a')[0]).offset();
  var anchorArr = window.location.href.toString().split("#");
  if (anchorArr.length > 2) {
    var anchor = anchorArr[anchorArr.length - 1];
    window.scrollTo(0, $('a[name=' + anchor + ']').offset().top - 80);
  }
}

$(function () {
  var url = window.location.href.toString();
  $(window).bind('hashchange', function () {
    url = window.location.href.toString();
    choose(url);
  });

  $(document.body).on("click", ".model a", function (e) {
    e.preventDefault();
    var model = $(this).parent().attr("data-model");
    var parentOffset = $(this).parent().offset();
    var encodedWord = encodeURI(model);

    $.get("models/" + encodedWord + ".html", function (data) {
      if (!selectionImage) {
        selectionImage = $('<div>').attr({
          title: 'Model detail',
          target: '_blank',
          class: 'model-detail-popup'
        });
        $('#modal-container .modal-body').append(selectionImage);
      }

      selectionImage.html(data);
      selectionImage.attr('href', url.replace('{term}', encodeURI(model)));

      $('#modal-container').addClass('show').fadeIn();
      $('body').addClass('modal-open');
    })
  });

  $(document).on("click", "*", function (e) {
    var target = $(e.target);
    if (target.parents(".modal").length === 0) {
      if (document.querySelector('.modal')) {
        $('#modal-container').removeClass('show').fadeOut();
        $('body').removeClass('modal-open');
      }
    }
  });
});
