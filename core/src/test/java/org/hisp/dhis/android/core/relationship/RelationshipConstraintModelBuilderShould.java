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

package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RelationshipConstraintModelBuilderShould {

    private RelationshipConstraint pojo;

    private RelationshipConstraintModel model;

    private String TEI_TYPE_UID = "tei_type_uid";

    private RelationshipConstraintType CONSTRAINT_TYPE = RelationshipConstraintType.FROM;

    private RelationshipType relationshipType = RelationshipType.create("uid", null, null, null, null, null,
            null, null, null, null, null);

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        pojo = buildPojo();
        model = buildModel();
    }

    private RelationshipConstraint buildPojo() {
        return RelationshipConstraint.create(
                RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
                ObjectWithUid.create(TEI_TYPE_UID),
                null,
                null
        );
    }

    private RelationshipConstraintModel buildModel() {
        return new RelationshipConstraintModelBuilder(relationshipType, CONSTRAINT_TYPE).buildModel(pojo);
    }

    @Test
    public void copy_pojo_relationship_properties() {
        assertThat(model.relationshipType()).isEqualTo(relationshipType.uid());
        assertThat(model.constraintType()).isEqualTo(CONSTRAINT_TYPE);
        assertThat(model.relationshipEntity()).isEqualTo(RelationshipEntityType.TRACKED_ENTITY_INSTANCE);
        assertThat(model.trackedEntityType()).isEqualTo(TEI_TYPE_UID);
        assertThat(model.program()).isNull();
        assertThat(model.programStage()).isNull();
    }
}