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

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRendering;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRenderingTableInfo;
import org.hisp.dhis.android.core.common.ValueTypeRendering;

abstract class ValueTypeRenderingChildrenAppender<M extends ObjectWithUidInterface> extends ChildrenAppender<M> {

    private final ObjectWithoutUidStore<ValueTypeDeviceRendering> store;

    ValueTypeRenderingChildrenAppender(ObjectWithoutUidStore<ValueTypeDeviceRendering> store) {
        this.store = store;
    }

    ValueTypeRendering getValueTypeDeviceRendering(M model) {
        String desktopWhereClause = new WhereClauseBuilder()
                .appendKeyStringValue(BaseIdentifiableObjectModel.Columns.UID, model.uid())
                .appendKeyStringValue(ValueTypeDeviceRenderingTableInfo.Columns.DEVICE_TYPE, ValueTypeRendering.DESKTOP)
                .build();
        ValueTypeDeviceRendering desktop = store.selectOneWhere(desktopWhereClause);

        String mobileWhereClause = new WhereClauseBuilder()
                .appendKeyStringValue(BaseIdentifiableObjectModel.Columns.UID, model.uid())
                .appendKeyStringValue(ValueTypeDeviceRenderingTableInfo.Columns.DEVICE_TYPE, ValueTypeRendering.MOBILE)
                .build();
        ValueTypeDeviceRendering mobile = store.selectOneWhere(mobileWhereClause);

        return ValueTypeRendering.create(desktop, mobile);
    }
}