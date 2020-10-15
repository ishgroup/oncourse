  def membership = args.entity
  println membership.status
  println membership.confirmationStatus
  if (membership.status == ProductStatus.ACTIVE && membership.confirmationStatus == ConfirmationStatus.DO_NOT_SEND) {

    email {
      template "Send Membership SCC"
      bindings membership: membership
      to membership.contact
    }
    
    membership.setConfirmationStatus(ConfirmationStatus.SENT)
    args.context.commitChanges()

  }
