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

package org.hisp.dhis.android.core.event.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueFields;

import static org.hisp.dhis.android.core.event.EventTableInfo.Columns;

public final class EventFields {

    public static final String UID = "event";
    public static final String ENROLLMENT = Columns.ENROLLMENT;
    public static final String CREATED = Columns.CREATED;
    public static final String LAST_UPDATED = Columns.LAST_UPDATED;
    public static final String STATUS = Columns.STATUS;
    private static final String COORDINATE = "coordinate";
    public static final String PROGRAM = Columns.PROGRAM;
    public static final String PROGRAM_STAGE = Columns.PROGRAM_STAGE;
    public static final String ORGANISATION_UNIT = "orgUnit";
    public static final String EVENT_DATE = Columns.EVENT_DATE;
    public static final String COMPLETE_DATE = Columns.COMPLETE_DATE;
    public static final String DUE_DATE = Columns.DUE_DATE;
    public static final String DELETED = "deleted";
    public static final String TRACKED_ENTITY_DATA_VALUES = "dataValues";
    public static final String ATTRIBUTE_OPTION_COMBO = Columns.ATTRIBUTE_OPTION_COMBO;;
    private final static String GEOMETRY = "geometry";

    private static FieldsHelper<Event> fh = new FieldsHelper<>();

    public static final Fields<Event> allFields = Fields.<Event>builder()
            .fields(fh.<String>field(UID),
                    fh.<String>field(ENROLLMENT),
                    fh.<String>field(CREATED),
                    fh.<String>field(LAST_UPDATED),
                    fh.<EventStatus>field(STATUS),
                    fh.<Coordinates>field(COORDINATE),
                    fh.<Geometry>field(GEOMETRY),
                    fh.<String>field(PROGRAM),
                    fh.<String>field(PROGRAM_STAGE),
                    fh.<String>field(ORGANISATION_UNIT),
                    fh.<String>field(EVENT_DATE),
                    fh.<String>field(COMPLETE_DATE),
                    fh.<Boolean>field(DELETED),
                    fh.<String>field(DUE_DATE),
                    fh.<String>field(ATTRIBUTE_OPTION_COMBO),
                    fh.<TrackedEntityDataValue>nestedField(TRACKED_ENTITY_DATA_VALUES)
                            .with(TrackedEntityDataValueFields.allFields)
    ).build();

    private EventFields() {}

}