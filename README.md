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

### Similarity scoring
