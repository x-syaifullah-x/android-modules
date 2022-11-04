# RECAPTCHA WEB

### After Hosting
```
after hosting add your domain to:
    Firebase Console -> Project -> Authentication -> Settings -> Authorized domains -> Add domain
    Firebase Console -> Project -> Authentication -> Settings -> SMS region policy -> Allow
```

### DEPLOY
```sh
firebase deploy --only hosting:request-otp
```