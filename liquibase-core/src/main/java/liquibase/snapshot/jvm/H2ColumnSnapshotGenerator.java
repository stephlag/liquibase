package liquibase.snapshot.jvm;

import liquibase.database.Database;
import liquibase.database.core.H2Database;
import liquibase.exception.DatabaseException;
import liquibase.snapshot.JdbcDatabaseSnapshot;
import liquibase.statement.DatabaseFunction;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Column;

import java.sql.SQLException;
import java.util.Map;

public class H2ColumnSnapshotGenerator extends ColumnSnapshotGenerator {
    @Override
    public int getPriority(Class<? extends DatabaseObject> objectType, Database database) {
        if (Column.class.isAssignableFrom(objectType) && database instanceof H2Database) {
            return PRIORITY_DATABASE;
        } else {
            return PRIORITY_NONE;
        }
    }

    @Override
    protected Object readDefaultValue(JdbcDatabaseSnapshot.CachedRow columnMetadataResultSet, Column columnInfo, Database database) throws SQLException, DatabaseException {
        Object defaultValue = super.readDefaultValue(columnMetadataResultSet, columnInfo, database);
        if (defaultValue != null && defaultValue instanceof DatabaseFunction && ((DatabaseFunction) defaultValue).getValue().startsWith("NEXT VALUE FOR ")) {
            columnInfo.setAutoIncrement(true);
            return null;
        }
        return defaultValue;
    }

}
