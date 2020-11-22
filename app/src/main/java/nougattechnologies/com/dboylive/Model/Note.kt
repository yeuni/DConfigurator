package nougattechnologies.com.dboylive.Model

class Note {
    //	query = "CREATE TABLE users ( userId INTEGER PRIMARY KEY,
    // userName TEXT, userNote TEXT, userTime TEXT, userColor TEXT, udpateStatus TEXT)";
    // udpateStatus TEXT
    var id = 0
    var title: String? = null
    var note: String? = null
    var timestamp: String? = null
    var udpateStatus: String? = null
    var color = 0

    //(no such column: Note.COLUMN_UPDATESTATUS (code 1): , while compiling: SELECT  * FROM notes where Note.COLUMN_UPDATESTATUS = 'no')
    constructor() {}
    constructor(id: Int, title: String?, note: String?, timestamp: String?, color: Int, udpateStatus: String?) {
        this.id = id
        this.title = title
        this.note = note
        this.timestamp = timestamp
        this.color = color
        this.udpateStatus = udpateStatus
    }

    companion object {
        const val TABLE_NAME = "notes"
        const val COLUMN_ID = "noteId"
        const val COLUMN_TITLE = "noteTitle"
        const val COLUMN_NOTE = "noteContent"
        const val COLUMN_TIMESTAMP = "noteTimestamp"
        const val COLUMN_COLOR = "noteColor"
        const val COLUMN_UPDATESTATUS = "udpateStatus"

        // Create table SQL query
        const val CREATE_TABLE = ("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_NOTE + " TEXT,"
                + COLUMN_TIMESTAMP + " TEXT,"
                + COLUMN_COLOR + " INT,"
                + COLUMN_UPDATESTATUS + " TEXT"
                + ")")
    }
}