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

package org.hisp.dhis.android.core.maintenance;

import org.hisp.dhis.android.core.arch.db.TableInfo;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.utils.Utils;

import static org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel.Columns.UID;

public final class D2ErrorTableInfo {

    private D2ErrorTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "D2Error";
        }

        @Override
        public BaseModel.Columns columns() {
            return new Columns();
        }
    };

    static class Columns extends BaseModel.Columns {
        private static final String RESOURCE_TYPE = "resourceType";
        private static final String URL = "url";
        private static final String ERROR_COMPONENT = "errorComponent";
        private static final String ERROR_CODE = "errorCode";
        private static final String ERROR_DESCRIPTION = "errorDescription";
        private static final String HTTP_ERROR_CODE = "httpErrorCode";
        private static final String CREATED = "created";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    RESOURCE_TYPE,
                    UID,
                    URL,
                    ERROR_COMPONENT,
                    ERROR_CODE,
                    ERROR_DESCRIPTION,
                    HTTP_ERROR_CODE,
                    CREATED);
        }
    }
}