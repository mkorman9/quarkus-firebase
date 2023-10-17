# quarkus-firebase

Example of Quarkus HTTP API with Bearer-Token authorization based on Firebase Auth.

## Setup

- Navigate to [Firebase Console](https://console.firebase.google.com) and create a project
- Go to Project settings -> Service accounts -> Generate new private key, save it as `firebase-credentials.json` 
in the project root directory
- Add `GOOGLE_APPLICATION_CREDENTIALS=firebase-credentials.json` environment variable to run configuration in IDE.
Application container will also need to have a private key mounted in the file system, and 
`GOOGLE_APPLICATION_CREDENTIALS` pointed to it
