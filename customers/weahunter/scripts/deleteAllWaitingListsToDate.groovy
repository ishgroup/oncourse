def run(args) {
    def context = args.context

    def date = new Date().copyWith(
            //***ATTENTION*** please, enter your date in the next string
            year: 2017,
            month: Calendar.JULY,
            dayOfMonth: 1,

            hourOfDay: 0,
            minute: 0,
            second: 0
    )

    List<WaitingList> waitingLists = ObjectSelect.query(WaitingList)
            .prefetch(WaitingList.WAITING_LIST_SITES.joint())
            .where(WaitingList.CREATED_ON.lt(date))
            .select(context)

    int partSize = 50
    for (int i = 0; i < waitingLists.size(); i++) {
        context.deleteObjects(waitingLists.get(i))
        if (i % partSize == 0){
             context.commitChanges()
        }
    }

    if (context.hasChanges()){
        context.commitChanges()
    }
}