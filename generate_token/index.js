import { initializeApp } from 'firebase/app';
import { connectAuthEmulator, getAuth, signInAnonymously } from 'firebase/auth';
import { readFile } from 'fs';

const profile = process.argv[2] || 'emulator';

readFile(`${profile}.json`, {}, (err, data) => {
    if (err) {
        console.error(err);
        process.exit(1);
    }

    const firebaseConfig = JSON.parse(data.toString());
    const app = initializeApp(firebaseConfig);
    const auth = getAuth(app);

    if (profile === 'emulator') {
        connectAuthEmulator(auth, 'http://127.0.0.1:9099');
    }

    signInAnonymously(auth)
        .then(credentials => {
            console.log(`uid = ${credentials.user.uid}`)
            return credentials.user.getIdToken();
        })
        .then(token => console.log(`token = ${token}`))
        .catch(e => console.error(e));
});
