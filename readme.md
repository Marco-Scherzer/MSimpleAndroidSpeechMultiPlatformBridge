# Speech Recognition Multiplatform Bridge for Android (formerly MSimpleAndroidSpeechMultiPlatformBridge)
#### (A speech recognition server running on Android plus platform independent java client-code)
#### This software (Speech Recognition Multiplatform Bridge for Android and my own custom pairing protocol) is proprietary and protected by copyright law.
#### Idea, Author, and Copyright: Marco Scherzer
#### All rights reserved. This repository is to be treated as private.
###
### ( Android™ is a trademark of Google LLC. )
###
#### External dependencies: None (uses only classes of the Runtime Platform), ensuring this little project remains independent of changes in other software.
#### I originally developed Speech Recognition Multiplatform Bridge to use Android speech recognition within JavaFX/Gluon. It became my first Android‑specific project.
#### Development of "Speech Recognition Multiplatform Bridge for Android" started in Summer 2025 (two weeks) and had reached approx. 2 weeks of progress when it started to work for my own simple scenario purposes.
#### Features after 2 weeks:

- **Enables client devices running on non‑Android systems to use the Android Speech Recognition that runs on an Android device**  

- **Secure pairing: Allows only secure 1:1 paired communication between a client device and the Android device**

- **2 speech recognition modes choosable for server-side: implementation 1 uses Android's SpeechRecognizer, implementation 2 uses a RecognizerIntent**

- **Sends no direct audio streams via network/https: Instead uses the Android connected microphone to transmit audio and sends the recognized text to the registerd/paired client device**

- **Use can be low distance‑secured by the physical range of the microphone connected to the Android (server) device**

- **Platform‑independent Java client code**

- **Test GUI: Server plus client for testing**

- **Works with my own custom network device pairing protocol**  
  MSimpleAndroidSpeechMultiPlatformBridge is the example implementation of my custom pairing protocol.  
  In case of MSimpleAndroidSpeechMultiPlatformBridge, using my protocol ensures that no parallel interaction  
  with Android’s speech recognition can occur, even while providing speech recognition services to another device over the network.  
  This security design choice was the main reason for developing it.

  My protocol complements existing base security mechanisms (e.g., HTTPS and certificate pinning)  
  by adding an extra layer of protection. It is specifically designed to ensure that 1:1‑only client‑server connections remain exactly that.

---

## Protocol Definition (my own custom network device pairing protocol)

### Handshake
- **Step 0:** The user has to start the pairing handshake process on the server (e.g., by pressing a button)
- **Step 1:** The client registers with ID at the server.
- **Step 2:** The server answers with a one time usable endpoint(a String token) for the next request.


### Content Transmission
- **Step 1:** The client requests the next content by using this one-time usable endpoint(a String token).
- **Step 2:** The server sends the content plus the next one‑time usable endpoint … and so on (1,2, 1,2, …)



**Features:**
- Once a client was connected for the first time, the server blocks any other (unknown) client that has another ID.
- One‑time usable endpoints make it impossible to reuse the current prepared endpoint.
  If an unauthenticated client steals the ID and uses the current endpoint, the authenticated client cannot use the endpoint anymore and so cannot connect. This indicates unauthenticated use (and can optionally trigger an alarm on the client-side as well as on the server-side).
  Establishing a new connection after a case of unauthenticated use forces the user to actively restart the pairing (handshake) process on the server (e.g., by pressing a button) to reconnect and so to prevent unauthorized access.
- Protocol-Mode shutdownOnPossibleSecurityRisk: If shutdownOnPossibleSecurityRisk protocol-mode is activated and the authenticated client cannot connect, or a client with a wrong id or endpoint tried to connect, the server is for security reasons shutdown immediatly an has to be restartet actively by the user (e.g., by pressing a button) and then the pairing with the client has to be renewed.

---



#### Since Dec. 30, 2025, I decided to develop Speech Recognition Multiplatform Bridge for Android on GitHub, continuing from this point.
#### Note: Because I use Git as an addition to my history for proof of authorship, I regularly commit things in my (anyway *to treat as private*) repositories in an unready state (nothing works).




## Legal Notice
This software is proprietary and protected by copyright law.  
Idea, Author, and Copyright: Marco Scherzer  
All rights reserved.
This repository is to be treated as **private**.  
It is not intended for public collaboration or external contributions.  
Access is restricted, and any interaction with the repository is strictly forbidden.
Strictly prohibited:  
Forking, copying, reverse engineering, decompiling, modifying, redistributing, or any unauthorized
use of this software.

My source code and any compiled versions that may sometimes appear here,  
as well as any texts or other content on this page, are protected by copyright.
All rights are reserved, which means that any kind of use, copying, linking or downloading and many
things more is not permitted. If I ever decide to write a license for the binary, so that other people may at least be allowed to
download the executable or installer, this text will also include the license and the exact location where the binaries can be downloaded.

## Repository Sale Notice

This repository is offered non-exclusiv for sale in its current, up‑to‑date code state.
If you are interested, please contact me via my listed email address

**Important Notice:**
For security reasons, contracts are concluded exclusively after personal identification and
presentation of the buyer’s official ID document in the presence of my trusted notary in Karlsruhe,
Germany.
I always identify with ID-Card.
Since my childhood, I have always had exactly and only one banking account at a trusted local bank,
ensuring protocolized secure banking.
I never accept any transactions other than traditional, documented transactions processed directly
through my local bank.

Contact: fahrservice.1@gmail.com

# Declaration to Avoid Scamming, Theft of Intellectual Property, and Deception by Fraudsters

To prevent scamming, theft of intellectual property, and the deception of persons by fraudulent
actions, I hereby make the following statement once and for all, clearly and explicitly:

**Please note:** I never grant any permissions, not in the past, not now, and not in the future.

---

## 1. Abuse and Phishing

To protect against abuse and phishing, please note:
There are **no other websites, email addresses, or communication channels** associated with this software except the official contact listed here.

If you encounter the code or binaries anywhere other than:

- [https://github.com/Marco-Scherzer](https://github.com/Marco-Scherzer)
- Wayback Machine (pre‑publication archiving of of this repository's content and URLs)
- https://archive.today/ and https://archive.ph/ (pre‑publication archiving of of this repository's content and URLs)

then it constitutes **abuse, a scam, and theft of law‑protected intellectual property**.

In such a case, please inform GitHub and email me.

---

## 2. False Claims of Involvement or Permission

Any false claim by any person to be in any way involved in my projects, or to have received any
permission from me – whether for usage, reproduction, replication, especially of APIs,
functionality, modularity, architecture, or for public display – is untrue and constitutes a *
*serious criminal offense**.

This includes in particular:

- Scamming and fraudulent deception
- Theft of intellectual property
- Always implicit defamation of the true author of a work and his business, since the truth about
  the origin of a work is reputation‑critical

I explicitly declare that I **never grant any licenses of any kind for an open source work and
especially not for its code – not in the past, not now, and not in the future.**

---

## 3. Reporting Criminal Acts

If you have information pointing to criminal acts as described under points 1–2, I request that you
immediately:

- Inform the **Economic Cybercrime Division of the German Police (Zentrale Ansprechstellen
  Cybercrime, ZAC)**
    - [Polizei.de – Zentrale Ansprechstellen Cybercrime](https://www.polizei.de/Polizei/DE/Einrichtungen/ZAC/zac_node.html)
    - [ZAC Contact List (Bund & Länder, PDF)](https://www.wirtschaftsschutz.info/DE/Themen/Cybercrime/Ansprechpartner/ZACErreichbarkeiten.pdf?__blob=publicationFile&v=3)

- Contact **GitHub** via its official abuse reporting email: **abuse@github.com**
    - [GitHub Docs – Reporting Abuse or Spam](https://docs.github.com/en/communities/maintaining-your-safety-on-github/reporting-abuse-or-spam)

**Your civil courage counts. Help prevent such crimes, make Open Source safer, and protect the
reputation of authors.**