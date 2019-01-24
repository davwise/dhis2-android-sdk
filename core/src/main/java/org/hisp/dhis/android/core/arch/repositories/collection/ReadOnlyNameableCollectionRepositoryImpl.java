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
package org.hisp.dhis.android.core.arch.repositories.collection;

import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.filters.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyIdentifiableObjectRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeItem;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.BaseNameableObjectModel.Columns;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.NameableObject;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ReadOnlyNameableCollectionRepositoryImpl<M extends Model & NameableObject>
        extends ReadOnlyCollectionRepositoryImpl<M>
        implements ReadOnlyNameableCollectionRepository<M> {

    private final IdentifiableObjectStore<M> store;

    private ReadOnlyNameableCollectionRepositoryImpl(IdentifiableObjectStore<M> store,
                                                    Collection<ChildrenAppender<M>> childrenAppenders,
                                                    List<RepositoryScopeItem> scope) {
        super(store, childrenAppenders, scope);
        this.store = store;
    }

    public ReadOnlyNameableCollectionRepositoryImpl(IdentifiableObjectStore<M> store,
                                                    Collection<ChildrenAppender<M>> childrenAppenders) {
        this(store, childrenAppenders, Collections.<RepositoryScopeItem>emptyList());
    }

    @Override
    public ReadOnlyObjectRepository<M> uid(String uid) {
        return new ReadOnlyIdentifiableObjectRepositoryImpl<>(store, uid, childrenAppenders);
    }

    @Override
    public StringFilterConnector<ReadOnlyNameableCollectionRepository<M>> byUid() {
        return stringConnector(Columns.UID);
    }

    @Override
    public StringFilterConnector<ReadOnlyNameableCollectionRepository<M>> byCode() {
        return stringConnector(Columns.CODE);
    }

    @Override
    public StringFilterConnector<ReadOnlyNameableCollectionRepository<M>> byName() {
        return stringConnector(Columns.NAME);
    }

    @Override
    public StringFilterConnector<ReadOnlyNameableCollectionRepository<M>> byDisplayName() {
        return stringConnector(Columns.DISPLAY_NAME);
    }

    @Override
    public DateFilterConnector<ReadOnlyNameableCollectionRepository<M>> byCreated() {
        return dateConnector(Columns.CREATED);
    }

    @Override
    public DateFilterConnector<ReadOnlyNameableCollectionRepository<M>> byLastUpdated() {
        return dateConnector(BaseIdentifiableObjectModel.Columns.LAST_UPDATED);
    }

    @Override
    public StringFilterConnector<ReadOnlyNameableCollectionRepository<M>> byShortName() {
        return stringConnector(Columns.SHORT_NAME);
    }

    @Override
    public StringFilterConnector<ReadOnlyNameableCollectionRepository<M>> byDisplayShortName() {
        return stringConnector(Columns.DISPLAY_SHORT_NAME);
    }

    @Override
    public StringFilterConnector<ReadOnlyNameableCollectionRepository<M>> byDescription() {
        return stringConnector(Columns.DESCRIPTION);
    }

    @Override
    public StringFilterConnector<ReadOnlyNameableCollectionRepository<M>> byDisplayDescription() {
        return stringConnector(Columns.DISPLAY_DESCRIPTION);
    }

    CollectionRepositoryFactory<ReadOnlyNameableCollectionRepository<M>> repositoryFactory() {
        return new CollectionRepositoryFactory<ReadOnlyNameableCollectionRepository<M>>() {
            @Override
            public ReadOnlyNameableCollectionRepository<M> newWithScope(List<RepositoryScopeItem> updatedScope) {
                return new ReadOnlyNameableCollectionRepositoryImpl<>(store, childrenAppenders, updatedScope);
            }
        };
    }

    private StringFilterConnector<ReadOnlyNameableCollectionRepository<M>> stringConnector(String key) {
        return new StringFilterConnector<>(repositoryFactory(), scope, key);
    }

    private DateFilterConnector<ReadOnlyNameableCollectionRepository<M>> dateConnector(String key) {
        return new DateFilterConnector<>(repositoryFactory(), scope, key);
    }
}