/**
	Tags new classes an 'Not Confirmed'
	
	Trigger: 
		Entity Event	
		CourseClass : Create or Update
*/



def run (args) {

	def cc = args.entity

	if (!cc.hasTag("Programs/Not Confirmed") || !cc.hasTag("Programs/Confirmed") || !cc.hasTag("Programs/Cancelled", true)) {
		cc.addTag("Programs/Not Confirmed")
	}
	args.context.commitChanges()
}