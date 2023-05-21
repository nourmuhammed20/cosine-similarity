class Posting {
    int docId;
    double dtf;
    Posting next;

    public Posting(int docId, double dtf) {
        this.docId = docId;
        this.dtf = dtf;
        this.next = null;
    }

    public int getDocId() {
        return docId;
    }

    public double getDtf() {
        return dtf;
    }
}
