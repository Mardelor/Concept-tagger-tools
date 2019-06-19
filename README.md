# Concept-Tagger
Un pipeline Stanford Core NLP pour reconnaître les concepts statistiques 
dans les publications de l'insee.

**NLP** ?? [Par ici](https://corenlp.run)

Ce dépôt propose un service permettant d'effectuer la reconnaissance
d'entités nommées sur des textes en français, à l'aide de la bibliothèque 
[Stanford Core NLP](https://stanfordnlp.github.io/CoreNLP). Il contient
trois modules maven:

* **french-lemmatiser** : un lemmatiseur pour le français à base de dictionnaires
* **tokensregex-builder** : un outil permettant de générer des règles à 
intégrer dans un pipeline Stanford Core NLP permettant d'effectuer de la 
reconnaissance d'entités nommées
* **insee-ner-tagger** : plugin spécifique à l'insee permettant de tagger 
les publications web4g avec les concepts insee

## Quick Start

```bash
git clone https://git.stable.innovation.insee.eu/s4pzz7/concept-tagger.git
```
...