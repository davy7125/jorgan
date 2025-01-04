#!/bin/bash

set -e
echo 'Note: requires rpmdevtools ant java-21-openjdk alsa-lib-devel fluidsynth fluidsynth-devel'
fedpkg --release f41 local 
