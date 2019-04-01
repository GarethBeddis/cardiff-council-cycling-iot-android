package uk.gov.cardiff.cleanairproject.data.sql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import uk.gov.cardiff.cleanairproject.data.model.Journey
import uk.gov.cardiff.cleanairproject.data.model.Reading

// Ref: https://github.com/Android-Tutorials-Hub/login-register-sqlite-tutorial-Kotlin/blob/master/app/src/main/java/com/androidtutorialshub/loginregisterkotlin/sql/DatabaseHelper.kt
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Create table sql query
    private val CREATE_JOURNEY_TABLE = ("CREATE TABLE " + TABLE_JOURNEY + "(" +
            COLUMN_JOURNEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_REMOTE_ID + " INTEGER," +
            COLUMN_JOURNEY_SYNCED + " INTEGER" + ")")
    private val CREATE_READING_TABLE = ("CREATE TABLE " + TABLE_READING + "(" +
            COLUMN_READING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_READING_JOURNEY_ID + " INTEGER," +
            COLUMN_NOISE_READING + " REAL," +
            COLUMN_NO2_READING + " REAL," +
            COLUMN_PM10_READING + " REAL," +
            COLUMN_PM25_READING + " REAL," +
            COLUMN_TIME_TAKEN + " REAL," +
            COLUMN_LONGITUDE + " INTEGER," +
            COLUMN_LATITUDE + " INTEGER)")

    // Drop table sql query
    private val DROP_JOURNEY_TABLE = "DROP TABLE IF EXISTS $TABLE_JOURNEY"
    private val DROP_READING_TABLE = "DROP TABLE IF EXISTS $TABLE_READING"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_READING_TABLE)
        db.execSQL(CREATE_JOURNEY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //Drop User Table if exist
        db.execSQL(DROP_JOURNEY_TABLE)
        db.execSQL(DROP_READING_TABLE)
        // Create tables again
        onCreate(db)
    }

    // Journeys
    fun addJourney(journey: Journey):Journey {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_REMOTE_ID, journey.RemoteId)
        values.put(COLUMN_JOURNEY_SYNCED, journey.Synced)
        // Inserting Row
        journey.id = db.insert(TABLE_JOURNEY, null, values)
        db.close()
        // Return the journey with the ID
        return journey
    }
    fun deleteJourney(journey: Journey) {
        deleteJourney(journey.id.toInt())
    }
    fun deleteJourney(journeyID: Int) {
        val db = writableDatabase
        db.delete(
            TABLE_JOURNEY, "$COLUMN_JOURNEY_ID = ?",
            arrayOf(journeyID.toString()))
        db.close()
    }
    fun getJourneyReadingsCount(journey: Journey): Int {
        return getJourneyReadingsCount(journey.id.toInt())
    }
    fun getJourneyReadingsCount(journeyID: Int): Int {
        val db = readableDatabase
        val columns = arrayOf(COLUMN_READING_ID)
        val selectionCriteria = "$COLUMN_READING_JOURNEY_ID = ?"
        val selectionArgs = arrayOf(journeyID.toString())
        val cursor = db.query(TABLE_READING, columns, selectionCriteria, selectionArgs, null,  //group the rows
            null, null)
        val cursorCount = cursor.count
        cursor.close()
        db.close()
        return cursorCount
    }
    fun getUnsyncedJourneys(): List<Journey> {
        val db = readableDatabase
        // Get journeys where Synced = false
        val columns = arrayOf(
            COLUMN_JOURNEY_ID,
            COLUMN_REMOTE_ID,
            COLUMN_JOURNEY_SYNCED)
        val selectionCriteria = "$COLUMN_JOURNEY_SYNCED = ?"
        val selectionArgs = arrayOf("0")
        val cursor = db.query(
            TABLE_JOURNEY, columns, selectionCriteria, selectionArgs,null,null, null)
        // Prepare a list to hold the journeys
        val readings = mutableListOf<Journey>()
        // Get the journeys from the results
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                // Get the journey
                val journey = Journey(
                    id = cursor.getLong(0),
                    RemoteId = cursor.getLong(1),
                    Synced = (cursor.getInt(2) > 0))
                // If the journey has readings, add it to the list, otherwise delete it
                if (getJourneyReadingsCount(cursor.getInt(0)) > 0) {
                    readings.add(journey)
                } else {
                    deleteJourney(journey)
                }
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return readings
    }
    fun updateJourneys(journeys: List<Journey>) {
        val db = writableDatabase
        for (journey in journeys) {
            val updatedFields = ContentValues()
            updatedFields.put(COLUMN_REMOTE_ID, journey.RemoteId)
            updatedFields.put(COLUMN_JOURNEY_SYNCED, journey.Synced)
            db.update(TABLE_JOURNEY, updatedFields,
                "$COLUMN_JOURNEY_ID = ?", arrayOf(journey.id.toString()))
        }
        db.close()
    }

    // Readings
    fun addReading(reading: Reading) {
        val db = writableDatabase
        val values = ContentValues()
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
    fun getUnsyncedReadings(): List<Reading> {
        val db = readableDatabase
        // Get journeys where Synced = false
        val tables = "$TABLE_READING reading INNER JOIN $TABLE_JOURNEY journey " +
                "on reading.$COLUMN_READING_JOURNEY_ID = journey.$COLUMN_JOURNEY_ID"
        val columns = arrayOf(
            COLUMN_READING_ID,
            COLUMN_READING_JOURNEY_ID,
            COLUMN_NOISE_READING,
            COLUMN_NO2_READING,
            COLUMN_PM10_READING,
            COLUMN_PM25_READING,
            COLUMN_TIME_TAKEN,
            COLUMN_LONGITUDE,
            COLUMN_LATITUDE,
            COLUMN_REMOTE_ID)
        val cursor = db.query(tables, columns, null, null,null,null, null)
        // Prepare a list to hold the journey ID's
        val readings = mutableListOf<Reading>()
        // Get the journeys from the results
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                // Put row into an object
                readings.add(
                    Reading(
                        id = cursor.getLong(0),
                        JourneyId = cursor.getLong(1),
                        JourneyRemoteId = cursor.getLong(9),
                        NoiseReading = cursor.getDouble(2),
                        No2Reading = cursor.getDouble(3),
                        PM10Reading = cursor.getDouble(4),
                        PM25Reading = cursor.getDouble(5),
                        TimeTaken = cursor.getLong(6),
                        Longitude = cursor.getDouble(7),
                        Latitude = cursor.getDouble(8)))
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return readings
    }
    fun updateReadings(readingIDs: List<Int>) {
        val db = writableDatabase
        for (id in readingIDs) {
            db.delete(TABLE_READING,"$COLUMN_READING_ID = ?", arrayOf(id.toString()))
        }
        db.close()
    }

    companion object {
        // Database Version
        private val DATABASE_VERSION = 1

        // Database Name
        private val DATABASE_NAME = "LocalStorage.db"

        // Table Names
        private val TABLE_JOURNEY = "journey"
        private val TABLE_READING = "reading"

        // Journey Table Column names
        private val COLUMN_JOURNEY_ID = "journey_id"
        private val COLUMN_REMOTE_ID = "journey_RemoteId"
        private val COLUMN_JOURNEY_SYNCED = "journey_Synced"

        // Reading Table Column names
        private val COLUMN_READING_ID = "reading_id"
        private val COLUMN_READING_JOURNEY_ID = "reading_journey_id"
        private val COLUMN_NOISE_READING = "reading_noiseReading"
        private val COLUMN_NO2_READING = "reading_No2Reading"
        private val COLUMN_PM10_READING = "reading_PM10Reading"
        private val COLUMN_PM25_READING = "reading_PM25Reading"
        private val COLUMN_TIME_TAKEN = "reading_TimeTaken"
        private val COLUMN_LONGITUDE = "reading_longitude"
        private val COLUMN_LATITUDE = "reading_latitude"
    }
}