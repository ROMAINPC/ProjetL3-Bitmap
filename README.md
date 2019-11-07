# Application Android de retouche d'images
Application réalisée dans le cadre de l'UE "projet technologique", L3 Informatique de l'Université de Bordeaux.
La version actuelle correspond aux TD 1, 2 et 3.
**NB:** Chaque méthode/classe du projet est documenté par un entête Javadoc

## Lancer l'application:
Le projet prend la forme d'un projet Android Studio, il est donc parfaitement exécutable depuis le logiciel.

Version SDK mimimum : 21 (Lollipop)

### Changer d'image :
Pour changer l'image modifiable, il suffit de modifier la variable de classe `PICTURE` de la classe `MainActivity` et de relancer l'application. Les images disponibles sont incluses dans les resources de l'application, dans le dossier `\app\src\main\res\drawable`


## Structure du projet:
Le projet est divisé en 4 classes :
* `MainActivity`
La classe principale, activité Android où est gérée l'interface et la gestion des évènements de ses éléments. A la création de l'activité l'image sélectionnée est chargée sous la forme d'une instance de `Picture`.
L'image chargée est limitée à 1080 pixels de hauteur, elle est chargée en plus petite dimension si besoin.
La classe charge aussi une version de l'image plus petite, utilisée pour les aperçus.

* `Utils`
Classe contenant des méthodes statiques utiles, comme des conversions HSV<->RGB, ...

* `Picture`
Représente une image éditable.

* `Effects`
Contient des méthodes statiques, une série d'effet applicables sur des instance de `Bitmap` ou de `Picture`

## Classe Picture:
Cette classe encapsule principalement une instance de `Bitmap` mais en simplifie la manipulation.

Le constructeur principal permet de charger l'image, une surcharge permet de spécifier une taille désirée de l'image. Un autre constructeur permet de charger une image par copie (utilisation déconseillée, voir la section "Remarques").

Il est possible de `reload()` l'image, ce qui rechargera l'image depuis le fichier source.

La méthode `reset()` permet de restaurer l'image comme elle était à son chargement initial ou à la dernière sauvegarde avec `quickSave()`.

Est associé également à l'objet une liste d'histogrammes , des méthodes permettent de générer des histogrammes différents (pour l'instant l'histogramme des niveaux de gris et des niveaux de luminance).

## Effets disponibles:
Durée d'exécution sur l'émulateur suivant : NEXUS 5X, API 25, 1080*1920 px, 420dpi.

### `grayLevel` :
Donne une image en noir et blanc:
<img src="readme_src/original.png" width="150"><img src="readme_src/gris.png" width="150">

## Remarques et avis:

