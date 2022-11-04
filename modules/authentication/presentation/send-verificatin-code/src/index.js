import * as firebaseApp from "firebase/app"
import * as firebaseAuth from "firebase/auth"
import logo from "/src/assets/logo.png"

const config = {
    apiKey: "AIzaSyAQrpXY1_TzVTWSqL17AeeUtgGEiWoF6xI",
    authDomain: "app-x-x-x.firebaseapp.com",
    projectId: "app-x-x-x",
    storageBucket: "app-x-x-x.firebasestorage.app",
    messagingSenderId: "560050292747",
    appId: "1:560050292747:web:c81a024953241d31411f0e",
    measurementId: "G-NQ3WN5SCGV"
};

const app = firebaseApp.initializeApp(config)
const auth = firebaseAuth.getAuth(app)
const queryParams = _getQueryParams()
auth.languageCode = queryParams.languageCode

// https://developers.google.com/recaptcha/docs/display#render_param
window.recaptchaVerifier = new firebaseAuth.RecaptchaVerifier("sc_recaptcha_container", {
    "theme": "dark", // dark | light
    size: "normal", // compact | normal
    tabindex: 0,
    callback: async (response) => {
        try {
            const rawResponse = await fetch(`https://identitytoolkit.googleapis.com/v1/accounts:sendVerificationCode?key=${config.apiKey}`, {
                method: "POST",
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    phoneNumber: `${queryParams.phoneNumber}`,
                    recaptchaToken: response
                })
            });
            const content = await rawResponse.text();
            SendVerificationCode.onSend(content)
        } catch (error) {
            console.log(error)
        } finally {
            window.location.reload()
            // window.recaptchaVerifier.recaptcha.reset();
        }
    },
}, auth)

window.recaptchaVerifier.render().then(async (id) => {
    try {
        const img = document.getElementById("sc_logo")
        img.setAttribute("src", logo)
        img.style.visibility = "visible"

        const title = document.getElementById("sc_title")
        title.style.visibility = "visible"

        const loading = document.getElementById("loading")
        loading.remove();

        SendVerificationCode.onRender()
    } catch (error) {
        console.log(error)
    }
})

function _getQueryParams() {
    const urlSearchParams = new URLSearchParams(window.location.search)
    return Object.fromEntries(urlSearchParams.entries())
}