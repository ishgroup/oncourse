records.each { Document document ->
    csv << [
            "id"            : document.id,
            "createdOn"     : document.createdOn?.format("d-M-y HH:mm:ss"),
            "modifiedOn"    : document.modifiedOn?.format("d-M-y HH:mm:ss"),
            "File name"     : document.currentVersion?.fileName,
            "File type"     : document.currentVersion?.mimeType,
            "Security level": document.webVisibility,
            "Description"   : document.description,
            "isShared"      : document.isShared,
            "isRemoved"     : document.isRemoved,
            "Added"         : document.added,
            "FileUUID"      : document.fileUUID,
            "Attached student" : document.attachedForFirstEnrolmentStudentInfo
    ]
}
