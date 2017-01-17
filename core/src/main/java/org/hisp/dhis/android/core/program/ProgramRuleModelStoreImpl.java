package org.hisp.dhis.android.core.program;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.program.ProgramRuleModel.Columns;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class ProgramRuleModelStoreImpl implements ProgramRuleModelStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            Tables.PROGRAM_RULE + " (" +
            Columns.UID + ", " +
            Columns.CODE + ", " +
            Columns.NAME + ", " +
            Columns.DISPLAY_NAME + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.PRIORITY + ", " +
            Columns.CONDITION + ", " +
            Columns.PROGRAM + ", " +
            Columns.PROGRAM_STAGE + ") " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private final SQLiteStatement sqLiteStatement;

    public ProgramRuleModelStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
                       @NonNull String displayName, @NonNull Date created,
                       @NonNull Date lastUpdated, @Nullable Integer priority,
                       @Nullable String condition, @NonNull String program,
                       @Nullable String programStage) {
        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, BaseIdentifiableObject.DATE_FORMAT.format(created));
        sqLiteBind(sqLiteStatement, 6, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));
        sqLiteBind(sqLiteStatement, 7, priority);
        sqLiteBind(sqLiteStatement, 8, condition);
        sqLiteBind(sqLiteStatement, 9, program);
        sqLiteBind(sqLiteStatement, 10, programStage);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}