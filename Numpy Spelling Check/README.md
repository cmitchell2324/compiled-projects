This project demonstrates the data science capabilities of the numpy library for Python. This script performs the following operations:
  - Read a list of words from a text file. In this case, I chose a randomly generated text file, where each line represents one word
  - Compute the Levenshtein edit distance between two words. Levenshtein distance is the number of edit operations (add, remove, replace) that are required to transform one string into another
  - Prompt the user to enter a single misspelled word, comparing the given input to each word in the text file, printing out a list of the top 5 most likely correct spellings.
  - If there are no correct spellings, a separate case is handled
