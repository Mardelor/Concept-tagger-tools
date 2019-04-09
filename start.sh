#!/bin/bash
cd ~
mkdir Projects && cd Projects
git config --global user.name "Mardelor"
git config --global user.email remy.zirnheld@telecom-sudparis.eu
git clone https://github.com/Mardelor/Concept-Tagger.git

git config --global user.name "Rémy Zirnheld"
git config --global user.email remy.zirnheld@insee.fr

cat Concept-Tagger/bashrc.txt >> ~/.bashrc
