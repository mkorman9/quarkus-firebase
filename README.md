# quarkus-firebase

Example of Quarkus HTTP API with Bearer-Token authorization based on Firebase Auth.

## Start locally with Firebase emulator

- Make sure that the project id in `emulator/.firebaserc` is set to `emulator-project`
- Start emulator
```sh
cd emulator
firebase emulators:start
```
- Run the app via IDE

## Start locally without emulator

- Navigate to [Firebase Console](https://console.firebase.google.com) and create a project
- Go to Project settings -> Service accounts -> Generate new private key, save it as `firebase-credentials.json` 
in the project root directory
- Comment out `%dev.firebase.emulator.enabled=true` in `application.properties` file

## Setup application container

On production `firebase.credentials-path` property should be set up and point to mounted credentials file

```properties
# application.properties

firebase.credentials-path=/config/firebase-credentials.json
```

```yaml
# compose.yml

services:
  quarkus-website:
    image: quarkus-firebase:latest
    restart: always
    ports:
      - "8080:8080"
    volumes:
      - "${PWD}/application.properties:/config/application.properties:ro"
      - "${PWD}/firebase-credentials.json:/config/firebase-credentials.json:ro"
```
