# SpellBlaze  - A million times faster spell correction

A [SymSpell](https://github.com/wolfgarbe/symspell) implementation for spelling correction & Fuzzy search. It's 1 million times faster through Symmetric Delete spelling correction algorithm

## SymSpell

The fundamental beauty of SymSpell is the Symmetric Delete spelling correction algorithm which reduces the complexity of edit candidate generation and dictionary lookup for a given edit distance. It is six orders of magnitude faster (than the standard approach with deletes + transposes + replaces + inserts) and language independent.

Additionally only deletes are required, no transposes + replaces + inserts. Transposes + replaces + inserts of the input phrase are transformed into deletes of the dictionary term. Replaces and inserts are expensive and language dependent: e.g. Chinese has 70,000 Unicode Han characters!

## SpellBlaze
