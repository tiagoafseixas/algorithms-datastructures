package pt.edx.a201x.p23;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

class Table {

    Table parent;
    int rows;
    int rank;

    public Table(final int nrRows) {
        this.rows = nrRows;
        rank = 0;
    }

    public int getRank() {
        return this.rank;
    }

    public int getRows() {
        return this.rows;
    }

    public void addRows(final int nrRows) {
        this.rows += nrRows;
    }

    public void setParent(Table p) {
        this.parent = p;
        this.parent.addRows(this.rows);
        this.rows = 0;
    }

    public void setRank(final int i) {
        this.rank = i;
    }

    public Table getParent() {
        return this.parent;
    }

    @Override
    public String toString() {
        return "rows = " + this.rows + ", rank = " + this.rank;
    }
}

class TableSet {

    // array that stores the parent of the node corresponding to the index
    // of the array.
    int[] parent;
    int[] rank;
    Table[] tables;

    int maxRows;

    public TableSet(final int size) {
        this.parent = new int[size];
        this.rank = new int[size];
        this.tables = new Table[size];
        this.maxRows = 0;
    }

    public void makeSet(final int i, final int nrRows) {
        this.parent[i] = i;
        this.rank[i] = 0;
        this.tables[i] = new Table(nrRows);
        if(nrRows > this.maxRows) {
            this.maxRows = nrRows;
        }
    }

    public int find(final int i) {
        if(i != parent[i]) {
            parent[i] = find(parent[i]);
            tables[i].setParent(tables[parent[i]]);
        }

        return parent[i];
    }

    public void union(final int i, final int j) {
        final int iId = find(i);
        final int jId = find(j);

        if (iId == jId) { return; }

        if(rank[iId] > rank[jId]) {
            parent[jId] = iId;
            tables[jId].setParent(tables[iId]);

            if(tables[iId].getRows() > this.maxRows) {
                this.maxRows = tables[iId].getRows();
            }
        } else {
            parent[iId] = jId;
            tables[iId].setParent(tables[jId]);
            if(tables[jId].getRows() > this.maxRows) {
                this.maxRows = tables[jId].getRows();
            }
            if(rank[iId] == rank[jId]) {
                rank[jId] = rank[jId] + 1;
                tables[jId].setRank(tables[jId].getRank() + 1);
            }
        }
    }

    public Table get(final int i) {
        return this.tables[i];
    }

    /**
     * @return the maxRows
     */
    public int getMaxRows() {
        return maxRows;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.tables);
    }
}

class MergeTablesService {

    public void mergeTables(final int n, final int[] rows, final int[][] merges) {
        final TableSet ds = new TableSet(n);
        for(int i = 0; i < n; i++) { ds.makeSet(i, rows[i]); }
        //System.out.println(ds);

        for(int[] m : merges) {
            ds.union(m[0] - 1, m[1] - 1);
            //System.out.println(ds);
            System.out.println(ds.getMaxRows());
        }
    }

} 

class MergeTablesTest {
    public void test() {
        fiveTablesWithOneRow();
        sixTablesWithDifferentRowsAndSixMerges();
    }

    public void fiveTablesWithOneRow() {
        final String testName = "fiveTablesWithOneRow";

        final int n = 5;
        final int[] rows = {1, 1, 1, 1, 1};
        final int[][] merges = {{3, 5}, {2, 4}, {1, 4}, {5, 4}, {5, 3}};

        MergeTablesService mts = new MergeTablesService();
        mts.mergeTables(n, rows, merges);
    }

    public void sixTablesWithDifferentRowsAndSixMerges() {
        final String testName = "sixTablesWithDifferentRowsAndSixMerges";

        final int n = 6;
        final int[] rows = {10, 0, 5, 0, 3, 3};
        final int[][] merges = {{6, 6}, {6, 5}, {5, 4}, {4, 3}};

        MergeTablesService mts = new MergeTablesService();
        mts.mergeTables(n, rows, merges);
    }
}

public class MergeTables {
    private static final boolean TEST_MODE_ON = true;

    public static void main(String[] args) throws Exception {
        
        if(TEST_MODE_ON) {
            MergeTablesTest mtt = new MergeTablesTest();
            mtt.test();
        } else {
            final InputReader reader = new InputReader(System.in);
            int n = reader.nextInt();
            int m = reader.nextInt();
            int[] rows = new int[n];
            for (int i = 0; i < n; i++) { rows[i] = reader.nextInt(); }

            int[][] merges = new int[m][2];
            for (int i = 0; i < m; i++) {
                merges[i] = new int[] {reader.nextInt(),reader.nextInt()};
            }

            MergeTablesService mts = new MergeTablesService();
            mts.mergeTables(n, rows, merges);
        }
    }

    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public double nextDouble() {
            return Double.parseDouble(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
    }
}