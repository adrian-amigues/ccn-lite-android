## Synopsis

ccn-lite-android is a student project application that uses ccn-lite as a primary networking protocol.
The application is not made to work with anything else than the rest of the project that are in separated repositories.

Images of the application:

<img src="https://github.com/Aranor28/ccn-lite-android/blob/master/img/ccn-app1.png" height="550px" /> <img src="https://github.com/Aranor28/ccn-lite-android/blob/master/img/ccn-app2.png" height="550px" />

## Installation

The application can be installed by building and running the project in android studio.
There is also an apk available in app/build/outputs/apk that should be the most recent built version of the application.

The application needs a ccn-relay running on the phone the send out interests. This relay is part of the whole project and is in the listed repos.
It can bypass it in the options by choosing to use a direct IP, where you can then specify that IP. 

## Behaviour

The application is entirely depending on the project´s relay repo.
On start the application will contact the SDS to recieve information about the available sensors and their locations.
After that the application will request the data from all the sensors it knows.

The auto-updating of the sensor data can be toggles in the options, it will request the sensors data at a fixed interval, but will not request info from the SDS automatically.

The SDS can be requested manually with the refresh button on the app bar.

## Motivation

Uppsala University - Project CS autumn 2016
