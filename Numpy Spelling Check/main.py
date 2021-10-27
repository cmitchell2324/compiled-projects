import numpy as np

# Levenshtein distance function that calculates the edit distance between two strings.
# This function will simply return the numeric value (number of operations) that are 
# required to convert s1 into s2 or vice versa. I decided to use a matrix from the numpy
# library because it is easy to calculate the distance in this way. Each letter in a word
# represents an index in the matrix. 
def levenshtein_distance(s1, s2):
    s1_length = len(s1) + 1
    s2_length = len(s2) + 1
    levenshtein_matrix = np.zeros ((s1_length, s2_length))
    for x in range(s1_length):
        levenshtein_matrix[x, 0] = x
    for y in range(s2_length):
        levenshtein_matrix[0, y] = y

    for x in range(1, s1_length):
        for y in range(1, s2_length):
            if s1[x-1] == s2[y-1]:
                levenshtein_matrix[x,y] = min(levenshtein_matrix[x-1, y]+1, levenshtein_matrix[x-1, y-1], levenshtein_matrix[x, y-1]+1)
            else:
                levenshtein_matrix[x,y] = min(levenshtein_matrix[x-1,y]+1, levenshtein_matrix[x-1,y-1]+1, levenshtein_matrix[x,y-1]+1)

    return(levenshtein_matrix[s1_length - 1, s2_length-1])

# The main function that reads in an incorrect word from the user and prints out the top
# 5 most logical words with a correct spelling. In this case, I downloaded a very large text
# file (~84000 lines long), where each line represents some word in the English dictionary.
# The main function will read these words into a list and then calculate the levenshtein
# distance between your input word and the words in the list. From here, in order to filter 
# out unnecessary words, I decided to make the edit similarity threshold value be 0.50, meaning that any word compared with 
# the input word that doesn't meet this threshold will not be considered in the calculation. From here, I created a separate
# list of tuples, where the first element represents the edit distance and the second element represents
# the word from the text file. After performing the for loop, there will be a check to see if there are any elements
# in the list of tuples. If there are no tuple elements, there will be no suggested spelling edits. If there are less than 
# 5 elements, there will also be no suggested spelling edits.
def main():
    while True:
        word = input("Enter a word to spell-check: ")

        text_lines = []
        with open('words_subset.txt') as f:
            text_lines = f.readlines()

        new_text_lines = []
        for line in text_lines:
            edit_distance = levenshtein_distance(word, line)
            edit_similarity = (edit_distance) / (max(len(word), len(line)))
            if edit_similarity >= 0.50:
                edit_tuple = (edit_distance, line.rstrip())
                new_text_lines.append(edit_tuple)
            
        if len(new_text_lines) == 0 or len(new_text_lines) < 5:
            print('There are no words to match your input or there are not enough words to match your input')
        else:
            new_text_lines.sort()
        
            print(f'The top 5 possible correct spellings of {word}: ')
        
            for count in range(5):
                print(new_text_lines[count][1])

        response = input("Continue? (Y/N)")
        if response == "Y":
            continue
        else:
            break

# Simple if statement to run the main function and allow the script to execute
if __name__ == "__main__":
    main()