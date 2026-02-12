If Firebase deps won't resolve:
1) Android Studio: Settings -> Build, Execution, Deployment -> Gradle -> UNCHECK "Offline work"
2) File -> Settings -> Appearance & Behavior -> System Settings -> HTTP Proxy -> set to "No proxy" (or your proxy)
3) Sync again. If still fails: use mobile hotspot/VPN once so Gradle can download from google maven.
4) Confirm you have google-services.json in /app and packageName matches: com.example.myapplication
