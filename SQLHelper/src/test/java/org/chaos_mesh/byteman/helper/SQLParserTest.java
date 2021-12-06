package org.chaos_mesh.byteman.helper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class SQLParserTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SQLParserTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SQLParserTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
         SQLParserTestCase[] testCases = {
            new SQLParserTestCase("select * from test.t1", "test", "t1", "select", true),
            new SQLParserTestCase("select * from test.t1", "test", "", "select", true),
            new SQLParserTestCase("select * from test.t1", "", "t1", "select", true),
            new SQLParserTestCase("select * from test.t1", "", "", "select", true),
            new SQLParserTestCase("select * from test.t1", "test", "t1", "", true),
            new SQLParserTestCase("select * from test.t1", "test", "t1", "insert", false),
            new SQLParserTestCase("select * from test.t1", "test1", "t1", "select", false),
            new SQLParserTestCase("select * from test.t1", "test", "t2", "select", false),

            new SQLParserTestCase("insert into test.t1 values(1)", "test", "t1", "insert", true),
            new SQLParserTestCase("insert into test.t1 values(1)", "test", "", "insert", true),
            new SQLParserTestCase("insert into test.t1 values(1)", "", "t1", "insert", true),
            new SQLParserTestCase("insert into test.t1 values(1)", "", "", "insert", true),
            new SQLParserTestCase("insert into test.t1 values(1)", "test", "t1", "", true),
            new SQLParserTestCase("insert into test.t1 values(1)", "test", "t1", "update", false),
            new SQLParserTestCase("insert into test.t1 values(1)", "test1", "t1", "insert", false),
            new SQLParserTestCase("insert into test.t1 values(1)", "test", "t2", "insert", false),

            new SQLParserTestCase("replace into test.t1 values(1)", "test", "t1", "replace", true),
            new SQLParserTestCase("replace into test.t1 values(1)", "test", "", "replace", true),
            new SQLParserTestCase("replace into test.t1 values(1)", "", "t1", "replace", true),
            new SQLParserTestCase("replace into test.t1 values(1)", "", "", "replace", true),
            new SQLParserTestCase("replace into test.t1 values(1)", "test", "t1", "", true),
            new SQLParserTestCase("replace into test.t1 values(1)", "test", "t1", "update", false),
            new SQLParserTestCase("replace into test.t1 values(1)", "test1", "t1", "replace", false),
            new SQLParserTestCase("replace into test.t1 values(1)", "test", "t2", "replace", false),

            new SQLParserTestCase("delete from test.t1 where id = 1", "test", "t1", "delete", true),
            new SQLParserTestCase("delete from test.t1 where id = 1", "test", "", "delete", true),
            new SQLParserTestCase("delete from test.t1 where id = 1", "", "t1", "delete", true),
            new SQLParserTestCase("delete from test.t1 where id = 1", "", "", "delete", true),
            new SQLParserTestCase("delete from test.t1 where id = 1", "test", "t1", "", true),
            new SQLParserTestCase("delete from test.t1 where id = 1", "test", "t1", "update", false),
            new SQLParserTestCase("delete from test.t1 where id = 1", "test1", "t1", "delete", false),
            new SQLParserTestCase("delete from test.t1 where id = 1", "test", "t2", "delete", false),

            new SQLParserTestCase("update test.t1 set id = 1", "test", "t1", "update", true),
            new SQLParserTestCase("update test.t1 set id = 1", "test", "", "update", true),
            new SQLParserTestCase("update test.t1 set id = 1", "", "t1", "update", true),
            new SQLParserTestCase("update test.t1 set id = 1", "", "", "update", true),
            new SQLParserTestCase("update test.t1 set id = 1", "test", "t1", "", true),
            new SQLParserTestCase("update test.t1 set id = 1", "test", "t1", "select", false),
            new SQLParserTestCase("update test.t1 set id = 1", "test1", "t1", "update", false),
            new SQLParserTestCase("update test.t1 set id = 1", "test", "t2", "update", false),
        };

        for (int i = 1; i < testCases.length; i++) {
            System.out.println(testCases[i].Info());
            boolean match = SQLParser.matchDBTable(testCases[i].SQL, testCases[i].Database, testCases[i].Table, testCases[i].SQLType);
            assertTrue(match == testCases[i].Match);
        }
    }
}

class SQLParserTestCase
{
    String SQL; 
    String Database;  
    String Table;
    String SQLType;
    boolean Match; 

    SQLParserTestCase(String sql, String database, String table, String sqlType, boolean match) {
        this.SQL = sql;
        this.Database = database;
        this.Table = table;
        this.SQLType = sqlType;
        this.Match = match;
    }

    String Info() {
        return "SQL: " + this.SQL + ", Database: " + this.Database + ", Table: " + this.Table + ", SQLType: " + this.SQLType + ", Match: " + this.Match;
    }
 };