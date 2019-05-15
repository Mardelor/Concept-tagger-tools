#!/usr/bin/env bash
if [ $# -ne 1 ];then
    echo "usage : ./dl-resources.sh [TARGET]"
    exit
fi
OUT=$1

cd $HOME/Downloads
curl https://liquidtelecom.dl.sourceforge.net/project/iramuteq/iramuteq-0.7-alpha2/iramuteq_0.7-alpha2.tar.gz -o ./iramuteq.tar.gz
tar xvzf iramuteq.tar.gz
cp iramuteq-0.7-alpha2/dictionnaires/lexique_fr.txt $OUT