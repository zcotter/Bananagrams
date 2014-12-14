package com.zachcotter.bananagrams;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
  Uses preprocessed index lists to search for words faster.
  The lists are generated using a ruby tool I wrote, called preprocess_wordlist.rb
  which can be found at the project root. You can open it in your favorite text
  editor.

  The index files denote the location in the next file that the app should start
  it's search from.

  The first index contains letters A -> Z and the locations in the next index
  where the prefixes start with that letter.

  The second index contains prefixes AA -> ZZ and the location in the next index
  where the prefixes start with that two letter prefix

  The third index contains prefixes AAA -> ZZZ and the location in the wordlist
  where the words start with that three letter prefix

  The program can be easily modified to include indexes of further depth, but
  I found 3 to be sufficient.

  Example: Searching for "mobile"
  Since the first letter is 'm', the first index (containing prefixes A -> Z)
  will be searched until a line starting with 'm' is found. This line
  contains the location in the second index where the prefixes that start
  with 'm' begin. This takes a maximum of 26 lines.

  The program will move to the next index (containing prefixes AA -> ZZ), and
  jump to the location specified in the first index, where it will find the
  prefix 'ma'. It will begin a linear search from 'ma' to 'mz', for the purpose
  of finding 'mo'. This line will contain the start location for the next index.
  This operation takes a maximum of 26 lines.

  The program will then move to the last index (containing prefixes AAA->ZZZ),
  and jump to the first prefix containing 'mo'. It will then search 'moa'
  through 'moz' until it finds the line with 'mob', which contains the index in
  the wordlist of the first word starting with 'mob'. This takes a maximum of
   26 lines.

  Finally, the program will linear search from 'mob' until it finds the
  'mobile', or reaches a word that does not start with 'mob'. This takes a
  maximum of ~8000 lines for the worst case scenario "nonzoologically",
  but usually much less. This translates to a trivial amount of time unnoticed
  by the user.
 */
public class DictionarySearcher {

  private static final String PATH = "wordlist_filter_";
  private static final String TYPE = ".txt";
  private static final String WORDLIST_PATH = "wordlist.txt";
  private static final int DEPTH = 3;

  private Context context;

  public DictionarySearcher(Context context) {
    this.context = context;
  }

  public boolean search(String term) throws
                                     IOException {
    // don't need to find words shorter than 3 letters
    if(term.length() < 3) { return false; }

    // ignore case
    term = term.toLowerCase();

    int startIndex = 0;
    //At each depth level, find the position to start from in the next index
    for(int current_depth = 1; current_depth <= DEPTH; current_depth++) {
      startIndex = findNextIndex(current_depth,
                                 term,
                                 startIndex);
      if(startIndex == -1) { return false; }
    }

    //Search for the term in the wordlist, starting at the point specified
    //in the last index, and stopping when the term is found or a word is found
    //that does not start with the first 3 letters of the term
    DictionaryFilterReader wordList = new DictionaryFilterReader();
    return wordList.findWord(term,
                             startIndex,
                             term.substring(0,
                                            DEPTH));
  }

  //open the index file at the given depth and search for the index of the next
  //prefix in the next index
  private int findNextIndex(int depth,
                            String term,
                            int skip) throws
                                      IOException {
    DictionaryFilterReader filterReader = new DictionaryFilterReader(depth);
    String prefix = term.substring(0,
                                   depth);
    return filterReader.findNextIndexFromStringWithStartIndex(prefix,
                                                              skip);
  }

  private String getFilterPath(int depth) {
    return PATH + depth + TYPE;
  }

  private class DictionaryFilterReader {
    private BufferedReader reader;

    public DictionaryFilterReader(int depth) throws
                                             IOException {
      buildReader(getFilterPath(depth));
    }

    public DictionaryFilterReader() throws
                                    IOException {
      buildReader(WORDLIST_PATH);
    }

    // Retrieve the index file or wordlist from storage dir (/assets)
    private void buildReader(String path) throws
                                          IOException {
      AssetManager assetManager = context.getAssets();
      reader = new BufferedReader(new InputStreamReader(assetManager.open(path)));
    }

    /**
     * Searches for the given prefix in this reader's index file, starting
     * at the given character count, and ending when the target is found or a
     * line does not begin with the given prefix
     *
     * @param target A prefix to search for
     * @param start  The character count to skip in the file
     * @return The index to start from in the next file
     * @throws IOException
     */
    public int findNextIndexFromStringWithStartIndex(String target,
                                                     int start) throws
                                                                IOException {
      String line = "";
      reader.skip(start);
      while(line != null && !line.startsWith(target)) {
        line = reader.readLine();
      }
      if(line != null) {
        return indexFromLine(line);
      }
      else { return -1; }
    }

    /*
    Lines given in the format prefix:index, returns the index given a line
     */
    private int indexFromLine(String line) {
      return Integer.parseInt(line.split(":")[1]);
    }

    /**
     * Searches for the given term, starting the given number of characters into
     * the file, and ending when the term is found or a line does not begin
     * with the given prefix, or EOF.
     *
     * @param term   The search temr
     * @param start  The number of characters to skip in the wordlist before
     *               search begins
     * @param prefix The prefix to stop at
     * @return true if the word was found, false otherwise
     * @throws IOException
     */
    public boolean findWord(String term,
                            int start,
                            String prefix) throws
                                           IOException {
      //read each line from start until match found or prefix not found or EOF
      String line = "";
      reader.skip(start);
      reader.readLine(); //burp?
      while(line != null && !line.equals(term)) {
        line = reader.readLine();
        if(line != null && !line.startsWith(prefix)) {
          return false;
        }
      }
      return line != null;
    }
  }
}
