/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.arch.db.stores.internal;

import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.cursors.internal.CursorModelFactory;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public class LinkModelStoreImpl<M extends Model>
        extends ObjectStoreImpl<M> implements LinkModelStore<M> {

    private final String masterColumn;

    protected LinkModelStoreImpl(DatabaseAdapter databaseAdapter,
                       SQLiteStatement insertStatement,
                       SQLStatementBuilder builder,
                       String masterColumn,
                       StatementBinder<M> binder,
                       CursorModelFactory<M> modelFactory) {
        super(databaseAdapter, insertStatement, builder, binder, modelFactory);
        this.masterColumn = masterColumn;
    }

    @Override
    public void deleteLinksForMasterUid(@NonNull String masterUid) throws RuntimeException {
        deleteWhere(masterColumn + "='" + masterUid + "';");
    }

    @Override
    public int deleteAllLinks() {
        return delete();
    }
}