package org.chaos_mesh.byteman.helper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for SQLParser.
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
        SQLParserTestCase[] testCases1 = {
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

            // test multiple database and table
            new SQLParserTestCase("SELECT t1.id, t2.name FROM test1.t1 INNER JOIN test2.t2 ON test1.t1=test2.t2;", "test1", "t1", "select", true),
            new SQLParserTestCase("SELECT t1.id, t2.name FROM test1.t1 INNER JOIN test2.t2 ON test1.t1=test2.t2;", "test2", "t2", "select", true),
        };

        for (int i = 1; i < testCases1.length; i++) {
            System.out.println(testCases1[i].Info());
            boolean match = SQLParser.matchDBTable("", testCases1[i].SQL, testCases1[i].Database, testCases1[i].Table, testCases1[i].SQLType);
            assertTrue(match == testCases1[i].Match);
        }

        // without database information in SQL
        SQLParserTestCase[] testCases2 = {
            new SQLParserTestCase("select * from t1", "test", "t1", "select", true),
            new SQLParserTestCase("select * from t1", "test", "", "select", true),
            new SQLParserTestCase("select * from t1", "", "t1", "select", true),
            new SQLParserTestCase("select * from t1", "", "", "select", true),
            new SQLParserTestCase("select * from t1", "test", "t1", "", true),
            new SQLParserTestCase("select * from t1", "test", "t1", "insert", false),
            new SQLParserTestCase("select * from t1", "test1", "t1", "select", false),
            new SQLParserTestCase("select * from t1", "test", "t2", "select", false),

            new SQLParserTestCase("insert into t1 values(1)", "test", "t1", "insert", true),
            new SQLParserTestCase("insert into t1 values(1)", "test", "", "insert", true),
            new SQLParserTestCase("insert into t1 values(1)", "", "t1", "insert", true),
            new SQLParserTestCase("insert into t1 values(1)", "", "", "insert", true),
            new SQLParserTestCase("insert into t1 values(1)", "test", "t1", "", true),
            new SQLParserTestCase("insert into t1 values(1)", "test", "t1", "update", false),
            new SQLParserTestCase("insert into t1 values(1)", "test1", "t1", "insert", false),
            new SQLParserTestCase("insert into t1 values(1)", "test", "t2", "insert", false),

            new SQLParserTestCase("replace into t1 values(1)", "test", "t1", "replace", true),
            new SQLParserTestCase("replace into t1 values(1)", "test", "", "replace", true),
            new SQLParserTestCase("replace into t1 values(1)", "", "t1", "replace", true),
            new SQLParserTestCase("replace into t1 values(1)", "", "", "replace", true),
            new SQLParserTestCase("replace into t1 values(1)", "test", "t1", "", true),
            new SQLParserTestCase("replace into t1 values(1)", "test", "t1", "update", false),
            new SQLParserTestCase("replace into t1 values(1)", "test1", "t1", "replace", false),
            new SQLParserTestCase("replace into t1 values(1)", "test", "t2", "replace", false),

            new SQLParserTestCase("delete from t1 where id = 1", "test", "t1", "delete", true),
            new SQLParserTestCase("delete from t1 where id = 1", "test", "", "delete", true),
            new SQLParserTestCase("delete from t1 where id = 1", "", "t1", "delete", true),
            new SQLParserTestCase("delete from t1 where id = 1", "", "", "delete", true),
            new SQLParserTestCase("delete from t1 where id = 1", "test", "t1", "", true),
            new SQLParserTestCase("delete from t1 where id = 1", "test", "t1", "update", false),
            new SQLParserTestCase("delete from t1 where id = 1", "test1", "t1", "delete", false),
            new SQLParserTestCase("delete from t1 where id = 1", "test", "t2", "delete", false),

            new SQLParserTestCase("update t1 set id = 1", "test", "t1", "update", true),
            new SQLParserTestCase("update t1 set id = 1", "test", "", "update", true),
            new SQLParserTestCase("update t1 set id = 1", "", "t1", "update", true),
            new SQLParserTestCase("update t1 set id = 1", "", "", "update", true),
            new SQLParserTestCase("update t1 set id = 1", "test", "t1", "", true),
            new SQLParserTestCase("update t1 set id = 1", "test", "t1", "select", false),
            new SQLParserTestCase("update t1 set id = 1", "test1", "t1", "update", false),
            new SQLParserTestCase("update t1 set id = 1", "test", "t2", "update", false),

        };

        for (int i = 1; i < testCases2.length; i++) {
            System.out.println(testCases2[i].Info());
            boolean match = SQLParser.matchDBTable("test", testCases2[i].SQL, testCases2[i].Database, testCases2[i].Table, testCases2[i].SQLType);
            assertTrue(match == testCases2[i].Match);
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