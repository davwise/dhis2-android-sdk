/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.common;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isNull;

public class ObjectWithoutUidStoreImpl<M extends BaseModel>
        extends ObjectStoreImpl<M> implements ObjectWithoutUidStore<M> {
    protected final SQLiteStatement updateWhereStatement;
    protected final SQLiteStatement deleteWhereStatement;

    ObjectWithoutUidStoreImpl(DatabaseAdapter databaseAdapter, SQLiteStatement insertStatement,
                              SQLiteStatement updateWhereStatement, SQLiteStatement deleteWhereStatement,
                              SQLStatementBuilder builder) {
        super(databaseAdapter, insertStatement, builder);
        this.updateWhereStatement = updateWhereStatement;
        this.deleteWhereStatement = deleteWhereStatement;
    }

    @Override
    public void updateWhere(@NonNull M m) throws RuntimeException {
        isNull(m);
        m.bindToStatement(updateWhereStatement);
        m.bindToUpdateWhereStatement(updateWhereStatement);
        executeUpdateDelete(updateWhereStatement);
    }

    @Override
    public void updateOrInsertWhere(@NonNull M m) throws RuntimeException {
        try {
            updateWhere(m);
        } catch (Exception e){
            insert(m);
        }
    }

    @Override
    public void deleteWhere(@NonNull M m) throws RuntimeException {
        isNull(m);
        m.bindToDeleteWhereStatement(deleteWhereStatement);
        executeUpdateDelete(deleteWhereStatement);
    }

    @Override
    public List<M> selectWhere(@NonNull CursorModelFactory<M> modelFactory, @NonNull String... selectionArgs)
            throws RuntimeException {
        Cursor cursor = databaseAdapter.query(builder.selectWhere(), selectionArgs);
        return listObjectsFromCursor(cursor, modelFactory);
    }

    private List<M> listObjectsFromCursor(Cursor cursor, CursorModelFactory<M> modelFactory) {
        List<M> objects = new ArrayList<>(cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    objects.add(modelFactory.fromCursor(cursor));
                }
                while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return objects;
    }
}