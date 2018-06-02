# SpellBlaze  - A million times faster spell correction
[![Build Status](https://api.travis-ci.org/jpsingarayar/SpellBlaze.svg?branch=master)](https://travis-ci.org/jpsingarayar/SpellBlaze)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A [SymSpell](https://github.com/wolfgarbe/symspell) implementation for spelling correction & Fuzzy search. It's 1 million times faster through Symmetric Delete spelling correction algorithm

## SymSpell

The fundamental beauty of SymSpell is the Symmetric Delete spelling correction algorithm which reduces the complexity of edit candidate generation and dictionary lookup for a given edit distance. It is six orders of magnitude faster (than the standard approach with deletes + transposes + replaces + inserts) and language independent.

Additionally only deletes are required, no transposes + replaces + inserts. Transposes + replaces + inserts of the input phrase are transformed into deletes of the dictionary term. Replaces and inserts are expensive and language dependent: e.g. Chinese has 70,000 Unicode Han characters!

## SpellBlaze

SpellBlaze has these features added on top of the Symspell to provide more accuracy,

1. The Damerau-Levenshtein implemtation forces a edit distance of 1 for all operations. However, sometimes not all edits are created equal. For instance, if you are doing OCR correction, maybe substituting '0' for 'O' should have a smaller cost than substituting 'X' for 'O'. If you are doing human typo correction, maybe substituting 'X' for 'Z' should have a smaller cost, since they are located next to each other on a QWERTY keyboard. So we have implemented the Damerau-Levenshtein implementation with a weighted Damerau-Levenshtein implementation: where each operation (delete, insert, swap, replace) can have different edit weights.

2. Included a ported version of a blazingly fast phonetic reduction/hashing algorithm [Eudex](https://github.com/ticki/eudex).

3. Included a prefix proximity algorithm, JaroWinkler calculation

4. A fragment similarity component optimized version of the dice coefficient calculation to find the similarity between two strings.

5. Implemeted the dynamic replacement weight based on the QWERTY keyboard distance(since letters close to each other are more likely to be replaced)

6. Normalization of query before search.

7. This can be used to generate a correct single match for a string or a list of suggestions.

8. Works with multiple words, each word would get its own match with all the similarity scoring.

### Installation

#### Running as a Packaged Application

```
$ git clone git clone https://github.com/jpsingarayar/SpellBlaze.git

$ mvn clean install

$ java -jar target/sc.jar
```

#### Using the Maven Plugin

```
$ git clone git clone https://github.com/jpsingarayar/SpellBlaze.git

$ mvn clean install

$ mvn spring-boot:run
```
If run sucessful, the service would be up listening on port 8080.

Test the service by hitting the below url.

http://localhost:8080/spellcheck/spellcheck?q=dissapear

This should give the following response.
```json
{
   "origPhrase": "dissapear",
   "id": 2,
   "correctedPhrase": "disappear",
   "correctedWords": [
      "disappear"
   ],
   "similarity": 0.779589683121326,
   "similarityMap": {
      "disappear": 0.779589683121326
   },
   "responseTime": 0,
   "proximity": null,
   "editDistanceMax": 2
}
```
For a detailed similarity score between the orginal word and the matched word append a param debug=true to the end of the url.

http://localhost:8080/spellcheck/spellcheck?q=dissapear&debug=true

```json
{
   "origPhrase": "dissapear",
   "id": 3,
   "correctedPhrase": "disappear",
   "correctedWords": [
      "disappear"
   ],
   "similarity": 0.779589683121326,
   "similarityMap": {
      "disappear": 0.779589683121326
   },
   "responseTime": 1,
   "proximity": [
      {
         "count": 45,
         "distance": 1.44,
         "wordFrequency": 0.0000178065989672964,
         "editProximity": 0.28,
         "phoneticProximity": 1,
         "fragmentProximity": 0.875,
         "prefixProximity": 0.9481481313705444,
         "proximity": 0.779589683121326,
         "term": "disappear"
      }
   ],
   "editDistanceMax": 2
}
```






