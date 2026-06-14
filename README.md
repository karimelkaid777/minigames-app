# MiniGames App

## Auteurs

- Karim EL KAID
- Ricardo ANNIS

## Branche à tester

`main`

## Ce qui fonctionne

### TP1 — Kotlin + Compose
- **Écran d'accueil** : saisie du pseudo (obligatoire pour jouer), boutons jeux désactivés si pseudo vide, bouton Leaderboard toujours actif.
- **Jeu de réaction** : logique dans `ReactionViewModel` (StateFlow + coroutine). Modes vitesse variable et aveugle. Écran de résultat avec écart affiché.

### TP2 — Architecture + Navigation
- **Navigation multi-écrans** via `NavHost` + routes `@Serializable` type-safe (Accueil, Réaction, Mot caché, Leaderboard).
- **Mot caché** : grille 3×3 (6 lettres du mot + 3 aléatoires), sélection / effacement / validation / passe, timer 60 s, écran GAME_OVER avec Rejouer / Retour.

### TP3 — Persistance avec Room
- **Saisie du pseudo** sur l'écran d'accueil ; le pseudo est transmis aux écrans de jeu via les routes de navigation.
- **Sauvegarde des scores** via Room (`ScoreRepository` → `ScoreDao` → `AppDatabase`) à la fin de chaque partie pour les deux jeux.
- **Écran Leaderboard** : top 10 toutes parties confondues, avec filtre par jeu (Tous / Réaction / Mot caché), rang, pseudo, nom du jeu, score et date.
- **Architecture MVVM** respectée : `ReactionViewModel` et `WordGameViewModel` étendent `AndroidViewModel`.

### Bonus implémentés
- TP1 — Timer aveugle (le chrono se masque à l'approche de la cible).
- TP1 — Vitesse variable (le pas du timer change aléatoirement en cours de partie).
- TP2 — Indice : révèle la première lettre du mot (−1 point, une seule fois par grille).
- TP2 — Meilleur score de la session affiché en fin de partie.
- TP3 — Filtre du leaderboard par jeu ("Tous" / "Réaction" / "Mot caché").
- TP3 — Réinitialisation des scores (bouton avec confirmation dans le leaderboard).

## Ce qui ne fonctionne pas

Rien à signaler : toutes les fonctionnalités listées ci-dessus sont opérationnelles.

## Lancer le projet

Ouvrir le dossier dans Android Studio, laisser Gradle se synchroniser, puis lancer la
configuration `app` sur un émulateur ou un appareil (minSdk 26).
