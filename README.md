# Concept-Tagger
Core NLP pipeline including NER for statistical concepts.

## Quickly
Goal in this repository is to provide some Java code in order to do NER
(Named Entity Recognition) in texts, giving a corpus of named entities
and XML files containing texts ! It uses the NLP library Stanford CoreNLP.

For now, project is structured as follow :
* **s3-manager & publi-parser** : gather utils to load corpus datas
* **test-core-nlp-fr** : some tests on Core NLP
* **tagger & tokensregex-builder** : the core of the project, which contains tools to do NER
