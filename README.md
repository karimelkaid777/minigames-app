# MiniGames App

Application Android (Kotlin / Jetpack Compose) regroupant deux mini-jeux : un **jeu de réaction**
et **Mot caché**. TP noté — Séance 2 (Architecture & Navigation), CNAM 1ʳᵉ année.

## Auteurs

- Karim EL KAID
- Marc GEHI

## Branche à tester

`main`

## Ce qui fonctionne

- **Navigation multi-écrans** via `NavHost` + `NavController`, avec des routes type-safe
  `@Serializable` (accueil, jeu de réaction, mot caché).
- **Écran d'accueil** avec deux boutons menant à chacun des jeux.
- **Jeu de réaction** : toute la logique (états + timer) est déportée dans `ReactionViewModel`,
  l'état étant exposé en `StateFlow` et le timer lancé dans `viewModelScope`. L'écran ne fait
  qu'afficher l'état et appeler les méthodes du ViewModel. Modes vitesse variable et aveugle,
  écran de résultat.
- **Mot caché** :
  - Timer de 60 secondes ; le score correspond au nombre de mots trouvés.
  - Grille 3×3 générée à chaque manche (6 lettres du mot caché + 3 lettres aléatoires mélangées).
  - Sélection des lettres une par une (la cellule se désactive après sélection), zone de saisie
    et bouton effacer.
  - Boutons Valider et Passer, puis écran de fin (GAME_OVER) avec Rejouer / Retour à l'accueil.
- **Bonus implémentés** :
  - Indice : révèle la première lettre du mot (−1 point, une seule fois par grille).
  - Meilleur score de la session, affiché en fin de partie.

## Ce qui ne fonctionne pas

Rien à signaler : toutes les fonctionnalités listées ci-dessus sont opérationnelles.

## Lancer le projet

Ouvrir le dossier dans Android Studio, laisser Gradle se synchroniser, puis lancer la
configuration `app` sur un émulateur ou un appareil (minSdk 26).
