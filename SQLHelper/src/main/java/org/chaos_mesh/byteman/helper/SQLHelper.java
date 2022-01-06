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

/**
 * Helper class used by SQLHelper script to ...
 */
public class SQLHelper extends Helper
{
    protected SQLHelper(Rule rule) {
        super(rule);
    }

    // matchDBTable returns true if the sql match the filterDatabase, filterTable and sqlType.
    // sql may not contain database infomation, so the database parameter is required.
    public boolean matchDBTable(String database, String sql, String filterDatabase, String filterTable, String sqlType) {
        return SQLParser.matchDBTable(database, sql, filterDatabase, filterTable, sqlType);
    }
}



