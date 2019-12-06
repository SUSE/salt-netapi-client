#!/bin/bash

if [[ "$TRAVIS_JDK_VERSION" == "openjdk11"  &&  "$TRAVIS_PULL_REQUEST" == "false"  &&  ("$TRAVIS_BRANCH" == "master" || "$TRAVIS_TAG") ]]; then

  cp -R target/site/apidocs $HOME/javadoc-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/${TRAVIS_REPO_SLUG} gh-pages

  if [[ "$TRAVIS_TAG" ]]; then
    DOC_DIR=$TRAVIS_TAG
  elif [[ "$TRAVIS_BRANCH" == "master" ]]; then
    DOC_DIR=$TRAVIS_BRANCH
  fi

  cd gh-pages
  git rm -rf ./docs/${DOC_DIR}
  cp -Rf $HOME/javadoc-latest ./docs/${DOC_DIR}
  git add -f .
  git commit -m "Travis auto publish javadoc for $DOC_DIR"
  git push -fq origin gh-pages

fi
