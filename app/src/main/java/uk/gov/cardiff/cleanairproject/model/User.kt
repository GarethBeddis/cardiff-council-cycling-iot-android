package uk.gov.cardiff.cleanairproject.model


// Ref: https://github.com/Android-Tutorials-Hub/login-register-sqlite-tutorial-Kotlin/blob/master/app/src/main/java/com/androidtutorialshub/loginregisterkotlin/model/User.kt
data class User(val id: Int = -1, val email: String, val password: String)