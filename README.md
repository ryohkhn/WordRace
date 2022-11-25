# Word Race

## Installation and usage

1. Install Java jdk 17 or higher
2. Install Gradle 7.4 or higher
3. Clone the repository and go to the project folder

```bash
git clone git@gaufre.informatique.univ-paris-diderot.fr:hammouho/wordrace.git
# or
git clone https://gaufre.informatique.univ-paris-diderot.fr/hammouho/wordrace.git
```

4. Build and run the project

```shell
./gradlew run
```

## Authors

| Nom       | Prénom | Numéro étudiant |
|:----------|:-------|:----------------|
| HAMMOUCHE | Hocine | 22006279        |
| RODRIGUEZ | Lucas  | 22002335        |

## Features

- [ ] Menu
  - [ ] Options
    - [ ] Number of words between 15 and 50
    - [ ] Number of players at least 2
    - [ ] Number of lives at least 5
    - [ ] Host a game
    - [ ] Join a game
    - [ ] The ip of the host
    - [ ] Ready button
- [ ] Game
  - [ ] Solo
    - [ ] Generate blue word who gives lives
    - [ ] Timeout for the word generation
    - [ ] Decrease the timeout when the level increases
    - [ ] Increase the level every 100 words
    - [ ] If the queue of word is not half empty don't add a new word
    - [ ] Every mistake remove a live
    - [ ] Every word wrote without mistake add lives
    - [ ] When no lives remaining the game is over
  - [ ] Multiplayer
    - [ ] Same options ↑ except the timeout and levels
    - [ ] Generate red word, if written without mistake is added to the
      other player's queue
