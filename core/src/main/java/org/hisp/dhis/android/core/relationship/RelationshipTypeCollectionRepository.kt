/*
 *  Copyright (c) 2004-2021, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.relationship

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeCollectionRepositoryHelper.availabilityEnrollmentQuery
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeCollectionRepositoryHelper.availabilityEventQuery
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeCollectionRepositoryHelper.availabilityTeiQuery
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeFields
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import javax.inject.Inject

@Reusable
class RelationshipTypeCollectionRepository @Inject internal constructor(
    store: IdentifiableObjectStore<RelationshipType>,
    private val teiStore: TrackedEntityInstanceStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    private val dhisVersionManager: DHISVersionManager,
    childrenAppenders: MutableMap<String, ChildrenAppender<RelationshipType>>,
    scope: RepositoryScope
) : ReadOnlyIdentifiableCollectionRepositoryImpl<RelationshipType, RelationshipTypeCollectionRepository>(store,
    childrenAppenders,
    scope,
    FilterConnectorFactory<RelationshipTypeCollectionRepository>(scope) { s: RepositoryScope ->
        RelationshipTypeCollectionRepository(
            store, teiStore, enrollmentStore, eventStore,
            dhisVersionManager, childrenAppenders, s
        )
    }
) {
    fun byBidirectional(): BooleanFilterConnector<RelationshipTypeCollectionRepository> {
        return cf.bool(RelationshipTypeTableInfo.Columns.BIDIRECTIONAL);
    }

    fun byConstraint(
        relationshipEntityType: RelationshipEntityType,
        relationshipEntityUid: String
    ): RelationshipTypeCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inTableWhere(
            RelationshipConstraintTableInfo.TABLE_INFO.name(),
            RelationshipConstraintTableInfo.Columns.RELATIONSHIP_TYPE,
            constraintClauseBuilder(relationshipEntityType, relationshipEntityUid)
        )
    }

    fun byConstraint(
        relationshipEntityType: RelationshipEntityType,
        relationshipEntityUid: String,
        relationshipConstraintType: RelationshipConstraintType
    ): RelationshipTypeCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inTableWhere(
            RelationshipConstraintTableInfo.TABLE_INFO.name(),
            RelationshipConstraintTableInfo.Columns.RELATIONSHIP_TYPE,
            constraintClauseBuilder(relationshipEntityType, relationshipEntityUid)
                .appendKeyStringValue(
                    RelationshipConstraintTableInfo.Columns.CONSTRAINT_TYPE,
                    relationshipConstraintType
                )
        )
    }

    @JvmOverloads
    fun byTrackedEntityInstanceAvailability(
        teiUid: String,
        type: RelationshipConstraintType? = null
    ): RelationshipTypeCollectionRepository {
        return if (dhisVersionManager.is2_29) {
            // All relationships are allowed for TEIs in 2.29
            this
        } else {
            val trackedEntityInstance = teiStore.selectByUid(teiUid)
            cf.subQuery(IdentifiableColumns.UID).inTableWhere(
                RelationshipConstraintTableInfo.TABLE_INFO.name(),
                RelationshipConstraintTableInfo.Columns.RELATIONSHIP_TYPE,
                availabilityTeiQuery(trackedEntityInstance, type)
            )
        }
    }

    @JvmOverloads
    fun byEventAvailability(
        eventUid: String,
        type: RelationshipConstraintType? = null
    ): RelationshipTypeCollectionRepository {
        return if (dhisVersionManager.is2_29) {
            // Event relationships are not supported in 2.29
            cf.string(IdentifiableColumns.UID).`in`(emptyList())
        } else {
            val event = eventStore.selectByUid(eventUid)
            cf.subQuery(IdentifiableColumns.UID).inTableWhere(
                RelationshipConstraintTableInfo.TABLE_INFO.name(),
                RelationshipConstraintTableInfo.Columns.RELATIONSHIP_TYPE,
                availabilityEventQuery(event, type)
            )
        }
    }

    @JvmOverloads
    fun byEnrollmentAvailability(
        enrollmentUid: String,
        type: RelationshipConstraintType? = null
    ): RelationshipTypeCollectionRepository {
        return if (dhisVersionManager.is2_29) {
            // Enrollment relationships are not supported in 2.29
            cf.string(IdentifiableColumns.UID).`in`(emptyList())
        } else {
            val enrollment = enrollmentStore.selectByUid(enrollmentUid)
            cf.subQuery(IdentifiableColumns.UID).inTableWhere(
                RelationshipConstraintTableInfo.TABLE_INFO.name(),
                RelationshipConstraintTableInfo.Columns.RELATIONSHIP_TYPE,
                availabilityEnrollmentQuery(enrollment, type)
            )
        }
    }

    fun withConstraints(): RelationshipTypeCollectionRepository {
        return cf.withChild(RelationshipTypeFields.CONSTRAINTS)!!
    }

    private fun constraintClauseBuilder(
        relationshipEntityType: RelationshipEntityType,
        relationshipEntityUid: String
    ): WhereClauseBuilder {
        return WhereClauseBuilder()
            .appendKeyStringValue(
                RelationshipConstraintTableInfo.Columns.RELATIONSHIP_ENTITY, relationshipEntityType
            )
            .appendKeyStringValue(getRelationshipEntityColumn(relationshipEntityType), relationshipEntityUid)
    }

    private fun getRelationshipEntityColumn(relationshipEntityType: RelationshipEntityType): String {
        return when (relationshipEntityType) {
            RelationshipEntityType.TRACKED_ENTITY_INSTANCE ->
                RelationshipConstraintTableInfo.Columns.TRACKED_ENTITY_TYPE
            RelationshipEntityType.PROGRAM_INSTANCE ->
                RelationshipConstraintTableInfo.Columns.PROGRAM
            RelationshipEntityType.PROGRAM_STAGE_INSTANCE ->
                RelationshipConstraintTableInfo.Columns.PROGRAM_STAGE
        }
    }
}