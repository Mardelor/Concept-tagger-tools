# Concept-Tagger
Core NLP pipeline including NER for statistical concepts.

## Quickly
Goal in this repository is to provide some Java code in order to do NER
(Named Entity Recognition) in texts, giving a corpus of named entities
and XML files containing texts ! It uses the NLP library Stanford CoreNLP.

For now, project is structured as follow :
* **s3-manager** : a s3 (Simple Storage System) client
* **concept-parser** : used to prepare concepts to the Core NLP pipeline
* **publi-parser** : used to prepare XML files to the Core NLP pipeline
* **tagger** : the Core NLP pipeline
* **test-core-nlp-fr** : tests...

## For dev confort
- [ ] Create a minio bucket for this app
- [ ] Develop Data-export tool & merge it with s3 ??
- [ ] Implements JUnit which test all of that
- [ ] Build a test pipeline with data from minio bucket, which push results on IP in Minio !
