# Sankofa Royale — projet Android

Projet Capacitor prêt à compiler. Le jeu tourne dans une WebView, **entièrement hors ligne** :
polices intégrées en base64, aucune requête réseau, aucune permission Android demandée.

- Identifiant : `com.sankofa.royale`
- Version : 1.0 (versionCode 1)
- Orientation : portrait verrouillé
- Taille attendue de l'APK : environ 4 Mo

---

## 1. La voie la plus rapide : compiler dans le cloud

Aucun outil à installer sur votre machine.

1. Créez un dépôt GitHub et poussez-y ce dossier.
2. Onglet **Actions** → **Compiler l'APK Android** → **Run workflow** → `debug`.
3. Après 3 à 5 minutes, téléchargez l'artefact `sankofa-royale-debug`.
4. Transférez le `.apk` sur un téléphone Android, autorisez l'installation depuis
   cette source, installez.

C'est l'APK de test : installable immédiatement, mais **non publiable** sur le Play Store.

## 2. Compiler sur votre machine

Prérequis : Node 20+, JDK 21, Android SDK (le plus simple est d'installer Android Studio,
qui pose le SDK et définit `ANDROID_HOME`).

```bash
./build-apk.sh              # APK de test
./build-apk.sh release      # APK + AAB signés (voir §3)
```

Ou en ouvrant le projet dans Android Studio :

```bash
npm install
npx cap sync android
npx cap open android
```

## 3. Signer pour la production

Le Play Store refuse les builds de debug. Créez une clé — **gardez-la précieusement,
la perdre rend toute mise à jour impossible** :

```bash
keytool -genkey -v -keystore sankofa-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 -alias sankofa
```

En local :

```bash
export KEYSTORE_PATH=/chemin/absolu/sankofa-release.jks
export KEYSTORE_PASSWORD='…'
export KEY_ALIAS=sankofa
export KEY_PASSWORD='…'
./build-apk.sh release
```

Dans GitHub Actions, ajoutez quatre secrets de dépôt :

| Secret | Contenu |
| --- | --- |
| `KEYSTORE_BASE64` | `base64 -w0 sankofa-release.jks` |
| `KEYSTORE_PASSWORD` | mot de passe du magasin |
| `KEY_ALIAS` | `sankofa` |
| `KEY_PASSWORD` | mot de passe de la clé |

Puis lancez le workflow en mode `release`.

Le fichier à téléverser sur le Play Store est le **`.aab`**, pas le `.apk` : Google
n'accepte plus les APK pour les nouvelles applications. Le `.apk` sert à la distribution
directe et aux tests.

## 4. Ce que le Play Store réclamera en plus

- Icône 512 × 512 : `www/icons/icon-1024.png`, à redimensionner
- Bandeau 1024 × 500 : à produire
- Au moins deux captures d'écran par format d'appareil
- Politique de confidentialité en ligne — **obligatoire même sans collecte de données**
- Déclaration « Data safety » : ici, aucune donnée collectée, tout reste sur l'appareil
- Classification de contenu (questionnaire IARC)
- Compte développeur Google Play : 25 $ une fois

## 5. Modifier le jeu

Tout le jeu tient dans `www/index.html`. Après chaque modification :

```bash
npx cap sync android
```

Pour changer le numéro de version avant une mise à jour, éditez `versionCode` et
`versionName` dans `android/app/build.gradle`. Le Play Store refuse deux téléversements
avec le même `versionCode`.

## 6. Version web installable

`www/` est aussi une PWA complète (manifeste + service worker). Déposé sur n'importe
quel hébergement HTTPS, le dossier s'installe depuis le navigateur avec « Ajouter à
l'écran d'accueil », sans passer par un magasin d'applications.

---

## Ce que ce build est, et ce qu'il n'est pas

C'est un **prototype jouable empaqueté** : parfait pour faire tester le jeu, le montrer
à un éditeur ou un investisseur, ou le mettre entre les mains de joueurs tests.

Ce n'est pas encore une production commerciale. Il manque, dans l'ordre :

1. **Achats intégrés et publicités** — nécessitent Google Play Billing et un SDK
   publicitaire, donc du code natif. La boutique du prototype est une maquette.
2. **Un serveur** — progression liée à l'appareil, sans compte ni sauvegarde cloud.
   Pas de classements, pas de clans, pas d'événements pilotés à distance.
3. **Les analytics** — aucune mesure de rétention ni d'économie, donc aucun pilotage
   possible du calibrage.
4. **Les performances** — une WebView convient à ce volume d'animation, mais pas aux
   effets d'un match-3 haut de gamme sur téléphone d'entrée de gamme. Le passage à
   Unity, Godot ou Cocos se décide avant d'écrire la vraie production.

Le GDD chiffre cette suite : environ huit mois jusqu'au soft launch.
