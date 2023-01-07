# Word Race

## Fonctionnalités

Les modes jeu, normal (compétitif dans notre GUI) et multijoueurs sont implémentés.  
Le mode normal se termine lorsque le nombre de mots défini durant la configuration est atteint.  
Le mode jeu a deux configurations possibles, la longueur de la file de mots et le nombre de vies initiale, la partie se termine quand le joueur a moins de 0 vie. Les mots bleus et le timer sont implémentés. Le timer utilise la fonction décroissante du sujet.  
Le mode multijoueur se joue au minimum à 2 joueurs, avec les même options de configuration que le mode jeu. 

## Installation et utilisation

1. Installer Java jdk 17 ou supérieur
2. Installer Gradle 7.4 ou supérieur
3. Cloner le dépôt et aller dans le répertoire

```bash
git clone git@gaufre.informatique.univ-paris-diderot.fr:hammouho/wordrace.git
# or
git clone https://gaufre.informatique.univ-paris-diderot.fr/hammouho/wordrace.git
```

4. Build et lancer le projet

```shell
./gradlew run
```

5. Lancer les tests

```shell
./gradlew
```

## Binôme 3

| Nom       | Prénom | Numéro étudiant |
|:----------|:-------|:----------------|
| HAMMOUCHE | Hocine | 22006279        |
| RODRIGUEZ | Lucas  | 22002335        |