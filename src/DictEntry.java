class DictEntry {
    private int term_freq;
    public Posting pList;

    public DictEntry() {
        term_freq = 0;
        pList = null;
    }

    public void incrementTermFrequency() {
        term_freq++;
    }

    public int getTerm_freq() {
        return term_freq;
    }
}
