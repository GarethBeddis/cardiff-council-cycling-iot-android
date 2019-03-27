package uk.gov.cardiff.cleanairproject.sql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import uk.gov.cardiff.cleanairproject.model.Journey
import uk.gov.cardiff.cleanairproject.model.Readings
import uk.gov.cardiff.cleanairproject.model.User
import java.util.*

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
            COLUMN_START_TIME + " REAL," +
            COLUMN_END_TIME + " REAL," +
            COLUMN_SYNCED + " INTEGER" + ")")

    private val CREATE_READINGS_TABLE = ("CREATE TABLE " + TABLE_READINGS + "(" +
            COLUMN_READINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_REMOTE_ID + " INTEGER," +
            COLUMN_JOURNEY_ID + " INTEGER FOREIGN KEY" +
            COLUMN_JOURNEY_REMOTE_ID + " INTEGER," +
            COLUMN_NOISE_READING + " REAL," +
            COLUMN_NO2_READING + " REAL," +
            COLUMN_PM10_READING + " REAL," +
            COLUMN_PM25_READING + " REAL," +
            COLUMN_TIME_TAKEN + " REAL," +
            COLUMN_LONGITUDE + " INTEGER," +
            COLUMN_LATITUDE + " INTEGER," +
            COLUMN_SYNCED + " INTEGER" + ")")

    // Drop table sql query
    private val DROP_USER_TABLE = "DROP TABLE IF EXISTS $TABLE_USER"

    private val DROP_JOURNEY_TABLE = "DROP TABLE IF EXISTS $TABLE_JOURNEY"

    private val DROP_READINGS_TABLE = "DROP TABLE IF EXISTS $TABLE_READINGS"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_USER_TABLE)
        db.execSQL(CREATE_READINGS_TABLE)
        db.execSQL(CREATE_JOURNEY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE)
        db.execSQL(DROP_JOURNEY_TABLE)
        db.execSQL(DROP_READINGS_TABLE)
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
        values.put(COLUMN_JOURNEY_ID, journey.id)
        values.put(COLUMN_JOURNEY_REMOTE_ID, journey.RemoteId)
        values.put(COLUMN_START_TIME, journey.StartTime)
        values.put(COLUMN_END_TIME, journey.EndTime)
        values.put(COLUMN_NOISE_READING, journey.Synced)

        // Inserting Row
        journey.id = db.insert(TABLE_JOURNEY, null, values)
        db.close()

        // Return the journey with the ID
        return journey
    }

    fun addReading(readings: Readings) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_READINGS_ID, readings.id)
        values.put(COLUMN_JOURNEY_ID, readings.JourneyId)
        values.put(COLUMN_NOISE_READING, readings.NoiseReading)
        values.put(COLUMN_NO2_READING, readings.No2Reading)
        values.put(COLUMN_PM10_READING, readings.PM10Reading)
        values.put(COLUMN_PM25_READING, readings.PM25Reading)
        values.put(COLUMN_TIME_TAKEN, readings.TimeTaken)
        values.put(COLUMN_LONGITUDE, readings.Longitude)
        values.put(COLUMN_LATITUDE, readings.Latitude)

        // Inserting Row
        db.insert(TABLE_READINGS, null, values)
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
        // delete user record by id
        db.delete(TABLE_USER, "$COLUMN_USER_ID = ?",
            arrayOf(user.id.toString()))
        db.close()
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
        private val TABLE_READINGS = "readings"

        // User Table Columns names
        private val COLUMN_USER_ID = "user_id"
        private val COLUMN_USER_EMAIL = "user_email"
        private val COLUMN_USER_PASSWORD = "user_password"

        // Journey Table Column names

        private val COLUMN_JOURNEY_ID = "journey_id"
        private val COLUMN_REMOTE_ID = "journey_RemoteId"
        private val COLUMN_START_TIME = "journey_StartTime"
        private val COLUMN_END_TIME = "journey_EndTime"
        private val COLUMN_SYNCED = "journey_Synced"

        // Readings Table Column names

        private val COLUMN_READINGS_ID = "readings_id"
        private val COLUMN_JOURNEY_REMOTE_ID = "readings_Journey_RemoteID"
        private val COLUMN_NOISE_READING = "readings_noiseReading"
        private val COLUMN_NO2_READING = "readings_No2Reading"
        private val COLUMN_PM10_READING = "readings_PM10Reading"
        private val COLUMN_PM25_READING = "readings_PM25Reading"
        private val COLUMN_TIME_TAKEN = "readings_TimeTaken"
        private val COLUMN_LONGITUDE = "readings_longitude"
        private val COLUMN_LATITUDE = "readings_latitude"

    }
}