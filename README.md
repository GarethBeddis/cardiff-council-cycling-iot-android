# Cardiff Council Cycling IOT Android

This repository contains the code for the Android application for the Cardiff Council Cycling IOT project.

## Quick Start

### Prerequisites

To build and run this application, you will need Android Studio with the Android 9.0 Pie SDK (API Level 28) installed, and a physical or virtual Android device running Android 6 Marshmallow or higher.

[Download Android Studio](https://developer.android.com/studio)

### Install and Run

Clone the repo
```
git clone git@gitlab.cs.cf.ac.uk:c1630757/cardiff-council-cycling-iot-android.git
```

Launch Android Studio and import the project
```
Import Project -> Select Project Folder -> Click OK
```

Configure the web server location for the app by modifying the API_URL buildConfigField in the app build.gradle file
```
...

android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "uk.gov.cardiff.cleanairproject"
        minSdkVersion 23
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        buildConfigField 'String', 'API_URL', '"http:// ADD YOUR SERVER IP OR DOMAIN HERE "'
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

...
```

Click the Run button in the top right of Android Studio to build and run the apps