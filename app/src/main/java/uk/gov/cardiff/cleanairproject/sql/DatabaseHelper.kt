package uk.gov.cardiff.cleanairproject.sql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import uk.gov.cardiff.cleanairproject.model.Journey
import uk.gov.cardiff.cleanairproject.model.Reading
import uk.gov.cardiff.cleanairproject.model.User

// Ref: https://github.com/Android-Tutorials-Hub/login-register-sqlite-tutorial-Kotlin/blob/master/app/src/main/java/com/androidtutorialshub/loginregisterkotlin/sql/DatabaseHelper.kt
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Create table sql query
    private val CREATE_USER_TABLE = ("CREATE TABLE " + TABLE_USER + "(" +
            COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USER_EMAIL + " TEXT," +
            COLUMN_USER_PASSWORD + " TEXT" + ")")

    private val CREATE_JOURNEY_TABLE = ("CREATE TABLE " + TABLE_JOURNEY + "(" +
            COLUMN_JOURNEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_REMOTE_ID + " INTEGER," +
            COLUMN_JOURNEY_SYNCED + " INTEGER" + ")")

    private val CREATE_READING_TABLE = ("CREATE TABLE " + TABLE_READING + "(" +
            COLUMN_READING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_READING_REMOTE_ID + " INTEGER," +
            COLUMN_READING_JOURNEY_ID + " INTEGER," +
            COLUMN_NOISE_READING + " REAL," +
            COLUMN_NO2_READING + " REAL," +
            COLUMN_PM10_READING + " REAL," +
            COLUMN_PM25_READING + " REAL," +
            COLUMN_TIME_TAKEN + " REAL," +
            COLUMN_LONGITUDE + " INTEGER," +
            COLUMN_LATITUDE + " INTEGER," +
            COLUMN_READING_SYNCED + " INTEGER" + ")")

    // Drop table sql query
    private val DROP_USER_TABLE = "DROP TABLE IF EXISTS $TABLE_USER"

    private val DROP_JOURNEY_TABLE = "DROP TABLE IF EXISTS $TABLE_JOURNEY"

    private val DROP_READING_TABLE = "DROP TABLE IF EXISTS $TABLE_READING"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_USER_TABLE)
        db.execSQL(CREATE_READING_TABLE)
        db.execSQL(CREATE_JOURNEY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE)
        db.execSQL(DROP_JOURNEY_TABLE)
        db.execSQL(DROP_READING_TABLE)
        // Create tables again
        onCreate(db)
    }

    // Add a user record
    fun addUser(user: User) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_USER_EMAIL, user.email)
        values.put(COLUMN_USER_PASSWORD, user.password)

        // Inserting Row
        db.insert(TABLE_USER, null, values)
        db.close()
    }

    fun addJourney(journey: Journey):Journey {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_REMOTE_ID, journey.RemoteId)
        values.put(COLUMN_JOURNEY_SYNCED, journey.Synced)

        // Inserting Row
        journey.id = db.insert(TABLE_JOURNEY, null, values)
        db.close()

        // Return the journey with the ID
        return journey
    }

    fun addReading(reading: Reading) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_READING_REMOTE_ID, reading.RemoteId)
        values.put(COLUMN_READING_JOURNEY_ID, reading.JourneyId)
        values.put(COLUMN_NOISE_READING, reading.NoiseReading)
        values.put(COLUMN_NO2_READING, reading.No2Reading)
        values.put(COLUMN_PM10_READING, reading.PM10Reading)
        values.put(COLUMN_PM25_READING, reading.PM25Reading)
        values.put(COLUMN_TIME_TAKEN, reading.TimeTaken)
        values.put(COLUMN_LONGITUDE, reading.Longitude)
        values.put(COLUMN_LATITUDE, reading.Latitude)

        // Inserting Row
        db.insert(TABLE_READING, null, values)
        db.close()
    }

    // Update a user record
    fun updateUser(user: User) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_USER_EMAIL, user.email)
        values.put(COLUMN_USER_PASSWORD, user.password)

        // updating row
        db.update(TABLE_USER, values, "$COLUMN_USER_ID = ?",
            arrayOf(user.id.toString()))
        db.close()
    }

    // Delete a user record
    fun deleteUser(user: User) {
        val db = this.writableDatabase
        db.delete(TABLE_USER, "$COLUMN_USER_ID = ?",
            arrayOf(user.id.toString()))
        db.close()
    }

    // Delete a journey
    fun deleteJourney(journey: Journey) {
        val db = this.writableDatabase
        db.delete(
            TABLE_JOURNEY, "$COLUMN_JOURNEY_ID = ?",
            arrayOf(journey.id.toString()))
        db.close()
    }

    // Get readings count for a journey
    fun getReadingsCount(journey: Journey): Int {
        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_READING_ID)
        val selectionCriteria = "$COLUMN_READING_JOURNEY_ID = ?"
        val selectionArgs = arrayOf(journey.id.toString())
        val cursor = db.query(TABLE_READING, //Table to query
            columns,        //columns to return
            selectionCriteria,      //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,  //group the rows
            null,   //filter by row groups
            null)
        val cursorCount = cursor.count
        cursor.close()
        db.close()
        return cursorCount
    }

    // Checks if a user exists by email
    fun checkUser(email: String): Boolean {

        // Array of columns to fetch
        val columns = arrayOf(COLUMN_USER_ID)
        val db = this.readableDatabase

        // Selection criteria
        val selection = "$COLUMN_USER_EMAIL = ?"

        // Selection argument
        val selectionArgs = arrayOf(email)

        // Query user table with condition
        val cursor = db.query(TABLE_USER, //Table to query
            columns,        //columns to return
            selection,      //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,  //group the rows
            null,   //filter by row groups
            null)  //The sort order

        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0) {
            return true
        }

        return false
    }

    fun checkUser(email: String, password: String): Boolean {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_USER_ID)

        val db = this.readableDatabase

        // selection criteria
        val selection = "$COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?"

        // selection arguments
        val selectionArgs = arrayOf(email, password)

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        val cursor = db.query(TABLE_USER, //Table to query
            columns, //columns to return
            selection, //columns for the WHERE clause
            selectionArgs, //The values for the WHERE clause
            null,  //group the rows
            null, //filter by row groups
            null) //The sort order

        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0)
            return true

        return false

    }

    companion object {

        // Database Version
        private val DATABASE_VERSION = 1

        // Database Name
        private val DATABASE_NAME = "UserManager.db"

        // User table name
        private val TABLE_USER = "user"
        private val TABLE_JOURNEY = "journey"
        private val TABLE_READING = "reading"

        // User Table Columns names
        private val COLUMN_USER_ID = "user_id"
        private val COLUMN_USER_EMAIL = "user_email"
        private val COLUMN_USER_PASSWORD = "user_password"

        // Journey Table Column names

        private val COLUMN_JOURNEY_ID = "journey_id"
        private val COLUMN_REMOTE_ID = "journey_RemoteId"
        private val COLUMN_JOURNEY_SYNCED = "journey_Synced"

        // Reading Table Column names

        private val COLUMN_READING_ID = "reading_id"
        private val COLUMN_READING_REMOTE_ID = "reading_remote_id"
        private val COLUMN_READING_JOURNEY_ID = "reading_journey_id"
        private val COLUMN_NOISE_READING = "reading_noiseReading"
        private val COLUMN_NO2_READING = "reading_No2Reading"
        private val COLUMN_PM10_READING = "reading_PM10Reading"
        private val COLUMN_PM25_READING = "reading_PM25Reading"
        private val COLUMN_TIME_TAKEN = "reading_TimeTaken"
        private val COLUMN_LONGITUDE = "reading_longitude"
        private val COLUMN_LATITUDE = "reading_latitude"
        private val COLUMN_READING_SYNCED = "reading_Synced"

    }
}