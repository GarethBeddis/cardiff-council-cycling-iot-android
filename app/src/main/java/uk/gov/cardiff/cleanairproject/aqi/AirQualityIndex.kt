package uk.gov.cardiff.cleanairproject.aqi

class AirQualityIndex {
    fun getOverall(no2: Int, pm25: Int, pm100: Int):Bands {
        val highestIndex = maxOf(getNO2Index(no2), getPM25Index(pm25), getPM100Index(pm100))
        return when (highestIndex) {
            in 1..3 -> Bands.LOW
            in 4..6 -> Bands.MODERATE
            in 7..9 -> Bands.HIGH
            else -> Bands.VERY_HIGH
        }
    }

    private fun getNO2Index(no2: Int): Int {
        return when {
            no2 < 68 -> 1
            no2 in 68..134 -> 2
            no2 in 135..200 -> 3
            no2 in 201..267 -> 4
            no2 in 268..334 -> 5
            no2 in 335..400 -> 6
            no2 in 401..467 -> 7
            no2 in 468..534 -> 8
            no2 in 535..600 -> 9
            else -> 10
        }
    }

    private fun getPM25Index(pm25: Int): Int {
        return when {
            pm25 < 12 -> 1
            pm25 in 12..23 -> 2
            pm25 in 24..35 -> 3
            pm25 in 36..41 -> 4
            pm25 in 42..47 -> 5
            pm25 in 48..53 -> 6
            pm25 in 54..58 -> 7
            pm25 in 59..64 -> 8
            pm25 in 65..70 -> 9
            else -> 10
        }
    }

    private fun getPM100Index(pm100: Int): Int {
        return when {
            pm100 < 17 -> 1
            pm100 in 17..33 -> 2
            pm100 in 34..50 -> 3
            pm100 in 51..58 -> 4
            pm100 in 59..66 -> 5
            pm100 in 67..75 -> 6
            pm100 in 76..83 -> 7
            pm100 in 84..91 -> 8
            pm100 in 92..100 -> 9
            else -> 10
        }
    }
}