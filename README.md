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
- Tokens for testing can be generated with
```sh
cd generate_token
npm ci
npm run-script generate-for-emulator
```

## Start locally without emulator

- Navigate to [Firebase Console](https://console.firebase.google.com) and create a project
- Go to Project settings -> Service accounts -> Generate new private key, save it as `firebase-credentials.json` 
in the project root directory
- Comment out `%dev.firebase.emulator.enabled=true` in `application.properties` file
- Add `firebase.credentials.type=FILE` to `application.properties` file
- Run the app via IDE
- Tokens for testing can be generated with
```sh
cd generate_token
npm ci
npm run-script generate-for-production
```

## Setup application container

On production, credentials either need to be defined by the platform, as pointed 
[here](https://cloud.google.com/java/docs/reference/google-auth-library/latest/com.google.auth.oauth2.GoogleCredentials#com_google_auth_oauth2_GoogleCredentials_getApplicationDefault__), 
or `firebase.credentials.type=FILE` and `firebase.credentials.path=<PATH>` needs to be provided. 
`firebase.credentials.path` property should point to mounted credentials file.

```properties
# application.properties

firebase.credentials.type=FILE
firebase.credentials.path=/config/firebase-credentials.json
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

When mounting a file is not possible due to environment restrictions, credentials can also be passed as a 
base64-encoded environment variable

```
FIREBASE_CREDENTIALS_TYPE=CONTENT
FIREBASE_CREDENTIALS_CONTENT=<base64-encoded firebase-credentials.json file>
```
