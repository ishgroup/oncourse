/**
 *  enrol.js
 *
 *  Enrolment engine functionality.
 *
 *
 */
 
// calculates the total price when you change the enrolment checkboxes
function totalPrice() {
	$j('form#enrolmentform input:not(:checked)').each(function(i) {
		var id = $j(this).parent().attr('id').substring(1);
		$j('td#p' + id).addClass('price-crossed');
	});
	var total = 0;
	var discountTotal = 0;
	$j('form#enrolmentform input:checked').each(function(i) {
		var id = $j(this).parent().attr('id').substring(1);
		var price = $j('td#p' + id).removeClass('price-crossed');
		discountTotal += parseFloat($j('#d' + id).text());
		total += parseFloat(price.children('.fee-discounted').text().substring(1));
	});
	$j('#discounttotal').text(makeMoney(discountTotal));
	$j('#total').text(makeMoney(total));
	$j('#cardtotalstring').text(makeMoney(total));
}

function makeMoney(money) {
	var value = money.toString().split('.');
	var cent  = value.length==1 ? '00' : (value[1].length == 1 ? value[1]+'0' : value[1].substring(0,2));
	return '$' + value[0] + '.' + cent;
}

jQuery.fn.clearForm = function() {
	return this.each(function() {
		var type = this.type, tag = this.tagName.toLowerCase();
		if (tag == 'form')
			return $j(':input',this).clearForm();
		if (type == 'text' || type == 'password' || tag == 'textarea')
			this.value = '';
		else if (type == 'checkbox' || type == 'radio')
			this.checked = false;
		else if (tag == 'select')
			this.selectedIndex = -1;
	});
};

$j(document).ready(function() {

});