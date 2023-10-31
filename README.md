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
- Run the app via IDE
- Tokens for testing can be generated with
```sh
cd generate_token
npm ci
npm run-script generate-for-production
```

## Credentials sourcing

Credentials can be sourced by the following methods:
- Defined by the platform (for example, when running on Google Cloud), as pointed [here](https://cloud.google.com/java/docs/reference/google-auth-library/latest/com.google.auth.oauth2.GoogleCredentials#com_google_auth_oauth2_GoogleCredentials_getApplicationDefault__)
- Via `firebase-credentials.json` file. Its path can be changed via `firebase.credentials.path=<PATH>` property.
For example:

```properties
# application.properties

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

- Via `FIREBASE_CREDENTIALS_CONTENT` environment variable, containing base64-encoded content of `firebase-credentials.json`.

```sh
docker run -it --rm -p 8080:8080 -e FIREBASE_CREDENTIALS_CONTENT="<CONTENT>" quarkus-firebase
```
