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
- Go to Project settings -> Service accounts -> Generate new private key, save it as `serviceAccountKey.json` 
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

The app tries to source credentials in 3 different ways, in this exact order:

- Via `FIREBASE_CREDENTIALS` environment variable, containing the content of `serviceAccountKey.json`.

```sh
creds="$(cat serviceAccountKey.json | jq -r tostring)"
docker run -it --rm -p 8080:8080 -e FIREBASE_CREDENTIALS="${creds}" quarkus-firebase
```

- Via `serviceAccountKey.json` file. Its path can be changed via `firebase.credentials.path=<PATH>` property.
For example:

```properties
# application.properties

firebase.credentials.path=/config/serviceAccountKey.json
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
      - "${PWD}/serviceAccountKey.json:/config/serviceAccountKey.json:ro"
```

- Via default platform credentials (for example, when running on Google Cloud), as pointed [here](https://cloud.google.com/java/docs/reference/google-auth-library/latest/com.google.auth.oauth2.GoogleCredentials#com_google_auth_oauth2_GoogleCredentials_getApplicationDefault__)

If all the methods above fail than the app refuses to start.
