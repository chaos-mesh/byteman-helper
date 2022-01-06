package org.chaos_mesh.byteman.helper;

import java.util.*;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.*;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class SQLParser {
    public SQLParser(){}

    public static SQLInfo parseSQL(String sql) {
        System.out.println("parseSQL: " + sql);
        List<String> dbTableList = new ArrayList();
        List<String> tableList = new ArrayList();
        List<String> dbList = new ArrayList();
        String type = "";
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            switch(statement.getClass().getName()) {
                case "org.chaosmesh.shaded.net.sf.jsqlparser.statement.select.Select":
                case "net.sf.jsqlparser.statement.select.Select":
                    Select selectStatement = (Select)statement;
                    dbTableList = tablesNamesFinder.getTableList(selectStatement);
                    type = "select";
                    break;
                case "org.chaosmesh.shaded.net.sf.jsqlparser.statement.update.Update":
                case "net.sf.jsqlparser.statement.update.Update":
                    Update updateStatement = (Update)statement;
                    dbTableList = tablesNamesFinder.getTableList(updateStatement);
                    type = "update";
                    break;
                case "org.chaosmesh.shaded.net.sf.jsqlparser.statement.insert.Insert":
                case "net.sf.jsqlparser.statement.insert.Insert":
                    Insert insertStatement = (Insert)statement;
                    dbTableList = tablesNamesFinder.getTableList(insertStatement);
                    type = "insert";
                    break;
                case "org.chaosmesh.shaded.net.sf.jsqlparser.statement.replace.Replace":
                case "net.sf.jsqlparser.statement.replace.Replace":
                    Replace replaceStatement = (Replace)statement;
                    dbTableList = tablesNamesFinder.getTableList(replaceStatement);
                    type = "replace";
                    break;
                case "org.chaosmesh.shaded.net.sf.jsqlparser.statement.delete.Delete":
                case "net.sf.jsqlparser.statement.delete.Delete":
                    Delete deleteStatement = (Delete)statement;
                    dbTableList = tablesNamesFinder.getTableList(deleteStatement);
                    type = "delete";
                    break;
                // TODO: support more SQL type
                default:
            }
        } catch(Exception e) {
            System.out.println("parseSQL get exception:" + e);
        }

        for (int i=0; i< dbTableList.size(); i++) {
            String[] dbTable = dbTableList.get(i).split("\\.");
            if (dbTable.length == 2) {
                dbList.add(dbTable[0]);
                tableList.add(dbTable[1]);
            } else if (dbTable.length == 1) {
                tableList.add(dbTable[0]);
            } else {
                System.out.println("parse database and table failed:" + dbTableList.get(i));
            }
        }

        System.out.println("database list: " + dbList.toString() + ", table list: " + tableList.toString() + ", sql type: " + type);

        return new SQLInfo(dbList, tableList, type);
    }

    public static boolean matchDBTable(String database, String sql, String filterDatabase, String filterTable, String sqlType) {
        System.out.println("database: " + database + ", sql: " + sql + ", filterDatabase: " + filterDatabase + ", filterTable: " + filterTable + ", sqlType: " + sqlType);
        SQLInfo sqlInfo = SQLParser.parseSQL(sql);
        sqlInfo.dbList.add(database);

        if (sqlType != null && sqlType != "") {
            if (sqlInfo.type != sqlType) {
                return false;
            }
        }

        if (filterDatabase != null && filterDatabase != "") {
            if (!sqlInfo.dbList.contains(filterDatabase)) {
                return false;
            }
        }

        if (filterTable != null && filterTable != "") {
            if (!sqlInfo.tableList.contains(filterTable)) {
                return false;
            }
        }

        System.out.println("sql: " + sql + ", match filter database:" + filterDatabase + ", filter table: " + filterTable + ", sql type:" + sqlType);
        return true;
    }
}
