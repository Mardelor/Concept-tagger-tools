# Tokensregex-builder

Un outil pour générer des [tokensregex](https://stanfordnlp.github.io/CoreNLP/tokensregex.html)
pour effectuer de la reconnaissance d'entités nommées ! 

Ce module permet de générer, à partir d'un fichier csv décrivant les
libelles des entités nommées à repérer, leur type d'entités nommées 
(personne, monnais, concept statistique, ...), un identifiant, ainsi 
qu'un eventuel accronyme, un fichier de rèlges.

Ce fichier de règle peut être lu par un pipeline Stanford Core NLP pour
effectuer la reconnaissance des entités nommées du fichier csv.

TODO : schéma