# Concept-Tagger
Core NLP pipeline including NER for statistical concepts.

New to **N**atural **L**anguage **P**rocessing ? [Try it !](https://corenlp.run)

## French

Le but de ce dépôt est de proposer un service de reconnaissance d'entités
nommées en utilisant la bibliothèque [Stanford Core NLP](https://stanfordnlp.github.io/CoreNLP). 
Ce service contient trois principaux modules maven :
* **french-lemmatiser** : ce module contient la classe permettant d'effectuer
la lemmatisation, ie. trouver pour chaque mot dans une phrase, à partir du 
contexte, le mot qui lui correspond dans le dictionnaire.

* **tokensregex-builder** : ce module maven permet, à partir d'une liste 
d'entités nommées repérer par un libelle et un identifiant, de construire
un fichier de règles permettant au second service de reconnaître les 
entités nommées dans du texte.

* **tagger** : ce module est un pipeline Stanford Core NLP qui effectue 
les traitements **Part du discours**, **Lemmatisation** et **TokensRegex**.

Les modules **test** et **utils** permettent respectivement de tester des 
nouvelles fonctionnalités et de faciliter l'acquisition des données.

## English
TODO