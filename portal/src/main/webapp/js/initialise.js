function set_is_company(ch){
	if(ch) {
		jQuery(".company-details").hide("blind",{},400);
		jQuery(".user-details").show("blind",{},400);
	} else {
		jQuery(".company-details").show();
		jQuery(".user-details").hide("blind",{},400);
	}
}



