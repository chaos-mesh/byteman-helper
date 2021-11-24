# byteman-helper

## MySQLHelper

`MySQLHelper` is a [Byteman's User-Defined Rule Helper](https://downloads.jboss.org/byteman/4.0.17/byteman-programmers-guide.html#user-defined-rule-helpers), which used to parse SQL and judge whether this SQL match specified database and table.

| Type | Method | Description |
| ---- | ------ | ------------|
| boolean | matchDBTable(String sql, String filterDatabase, String filterTable) | parse the `sql`, and judge whether this SQL match specified `filterDatabase` and `filterTable` |

### Example

```txt
RULE mysql test
CLASS com.mysql.cj.jdbc.StatementImpl
METHOD executeQuery
HELPER org.chaos_mesh.byteman.helper.MySQLHelper
AT ENTRY
BIND
     flag:boolean=matchDBTable($1, "test", "t1");
IF flag
DO
        throw new java.sql.SQLException("BOOM");
ENDRULE
```

SQLs match database `test` and table `t1` will get exception when execute query.

### Build

```bash
mvn -X package -Dmaven.test.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
```
