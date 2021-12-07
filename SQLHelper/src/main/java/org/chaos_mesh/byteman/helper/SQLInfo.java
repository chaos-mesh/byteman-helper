package org.chaos_mesh.byteman.helper;

import java.util.*;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.*;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class SQLInfo
{
    List<String> tableList = new ArrayList();
    List<String> dbList = new ArrayList();
    String type = "";

    SQLInfo(List<String> dbList, List<String> tableList, String type) {
        this.dbList = dbList;
        this.tableList = tableList;
        this.type = type;
    }
}