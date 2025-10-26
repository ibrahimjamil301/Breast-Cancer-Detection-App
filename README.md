
## 📖 Introduction
Welcome to the Breast Cancer Detection App — an Android application designed to assist users in identifying potential breast cancer symptoms through AI-based image analysis.

The app analyzes uploaded medical images using an AI model and provides quick, accurate predictions.
It also integrates Firebase for authentication, real-time database, and cloud storage.

## 🚀 Features

* 📷 Upload medical images directly from your device

* 🧠 AI-powered breast cancer detection

* ⚡ Instant prediction and result display

* 👩‍⚕️ Admin panel to manage users and results

* 🔒 Secure login & registration with Firebase Authentication

* ☁️ Image storage and data management via Firebase

## 🛠️ Tech Stack

* Language: Java

* UI Design: XML Layouts

* Backend & Storage: Firebase (Auth, Firestore, Cloud Storage)

* IDE: Android Studio

* Architecture: Activity-based modular structure

## 📂 Project Structure


```
Android-App/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/breastcancerapp/
│   │   │   │       ├── AdminPanelActivity.java
│   │   │   │       ├── AwarenessActivity.java
│   │   │   │       ├── EditUserActivity.java
│   │   │   │       ├── HomeActivity.java
│   │   │   │       ├── LoginActivity.java
│   │   │   │       ├── MainActivity.java
│   │   │   │       ├── ProfileActivity.java
│   │   │   │       ├── ResultActivity.java
│   │   │   │       ├── ResultActivity2.java
│   │   │   │       ├── ScanActivity.java
│   │   │   │       └── SignUpActivity.java
│   │   │   ├── res/
│   │   │   │   └── layout/
│   │   │   │       ├── activity_main.xml
│   │   │   │       ├── activity_login.xml
│   │   │   │       ├── activity_signup.xml
│   │   │   │       ├── activity_home.xml
│   │   │   │       ├── activity_result.xml
│   │   │   │       └── etc...
│   │   │   └── AndroidManifest.xml
│   │   ├── build.gradle.kts
│   │   └── proguard-rules.pro
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md

```

## 📲 Getting Started

1. Clone the repository:

```
git clone https://github.com/ibrahimjamil301/Breast-Cancer-Detection-App.git

```
Open the project in Android Studio

Sync Gradle and make sure you add your own Firebase configuration file (google-services.json)

Connect a device or start an emulator

Run the app ▶️

---

## 🖼️ Screenshots

| Started Screen | Login Screen |
|--------------|----------------------|
| <img width="605" height="879" alt="Screenshot_1" src="https://github.com/user-attachments/assets/c8c89d73-4662-4e9a-892e-0e11caffbc22" /> | <img width="521" height="892" alt="Screenshot_2" src="https://github.com/user-attachments/assets/dca06470-8f99-4bd5-8da2-54743adbc028" /> |

| Sign Up Screen | Home Screen |
|------------------------|------------------|
| <img width="563" height="883" alt="Screenshot_3" src="https://github.com/user-attachments/assets/f5534606-c545-4807-8099-9301b5fb0b8a" /> | <img width="551" height="886" alt="Screenshot_4" src="https://github.com/user-attachments/assets/cadfe628-172a-4d6e-aabd-f1c58e8ee4b9" /> |

| Scan Screen | Capture Screen |
|-------------------|-----------|
| <img width="540" height="873" alt="Screenshot_5" src="https://github.com/user-attachments/assets/e027acb3-2b83-4a86-9372-e8ba15edeaac" /> | <img width="683" height="888" alt="Screenshot_6" src="https://github.com/user-attachments/assets/8f2f3007-baf6-4d75-ac63-9c978b74009b" /> |


| Results Screen | Profile Screen |
|-------------------|-----------|
| <img width="430" height="932" alt="Result" src="https://github.com/user-attachments/assets/554a1aa8-02b5-4adc-8d9c-67e23965a745" /> | <img width="582" height="882" alt="Screenshot_7" src="https://github.com/user-attachments/assets/937760ad-6fe9-4be2-a470-749f644850cb" /> |

---

## 🎯 What I Learned

* Implementing Firebase Authentication and Firestore

* Integrating AI model API within Android

* Handling image uploads and results efficiently

* Designing clean UI and smooth user experience

* Managing app structure and navigation

## 🧠 Contribution

* Contributions, issues, and feature requests are welcome!
  
* Feel free to Fork the repo and submit a Pull Request

## 📃 License

* This project is licensed under the MIT License




