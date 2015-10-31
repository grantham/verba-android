# Verba for Android
Verba is an android Latin-English dictionary application based on the public
domain dictionary [An Elementary Latin Dictionary (1895)](http://www.archive.org/details/anelementarylat01lewigoog)
by Charlton T. Lewis.

The sister project for desktop is:[Verba-Console](http://magnopere.org/#verba-console)

## Features

- look up a word using any inflectional form of that word, e.g. identifies `arma` to one of:


        - noun nom neut pl
	    - noun voc neut pl
	    - noun acc neut pl
	    - verb second person sg pres imperat act
and leads in the first three cases to the definition of the noun, and in the last case the definition of the verb.

- complete offline access to the entire [An Elementary Latin Dictionary (1895)](http://www.archive.org/details/anelementarylat01lewigoog)
by Charlton T. Lewis.


## License
[GPL v3](http://www.gnu.org/licenses/)

## Source
[https://github.com/grantham/verba-android](https://github.com/grantham/verba-android)

## Prerequisites
- maven 3
- android SDK

## Building

    $mvn clean install

## Device Installation
Presently the apk is about 25M. But, you will need an additional 89M since the application decompresses a SQLite
database on initial use/setup.

## TODOs / New Features

- *HIGH* remember frequently looked-up words, making them available for review and study
- *HIGH* reduce space requirements (perhaps winnow the DB's morphology table, removing all enclitic entries)
- lookup service
- lookup Widget
