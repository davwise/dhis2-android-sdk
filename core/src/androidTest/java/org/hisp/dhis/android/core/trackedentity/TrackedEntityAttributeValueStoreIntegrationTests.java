package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.organisationunit.CreateOrganisationUnitUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeValueStoreIntegrationTests extends AbsStoreTestCase {
    //BaseDataModel:
    private static final State STATE = State.SYNCED;
    //TrackedEntityAttributeValueModel:
    private static final String VALUE = "TestValue";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "TestTrackedEntityAttributeUid";
    private static final String TRACKED_ENTITY_INSTANCE = "TestTrackedEntityInstanceUid";
    private static final String ORGANIZATION_UINT = "TestOrganizationUnitUid";

    private static final String[] TRACKED_ENTITY_ATTRIBUTE_VALUE_PROJECTION = {
            TrackedEntityAttributeValueModel.Columns.STATE,
            TrackedEntityAttributeValueModel.Columns.VALUE,
            TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE,
            TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE
    };

    private TrackedEntityAttributeValueStore store;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        this.store = new TrackedEntityAttributeValueStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistTrackedEntityAttributeValueInDatabase() {
        insertForeignKeys();

        long rowId = store.insert(STATE, VALUE, TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_INSTANCE);

        Cursor cursor = database().query(Tables.TRACKED_ENTITY_ATTRIBUTE_VALUE,
                TRACKED_ENTITY_ATTRIBUTE_VALUE_PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(STATE, VALUE, TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_INSTANCE)
                .isExhausted();
    }

    @Test
    public void insert_shouldPersistTrackedEntityAttributeValueNullableInDatabase() throws ParseException {
        insertForeignKeys();

        long rowId = store.insert(STATE, null, TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_INSTANCE);

        Cursor cursor = database().query(Tables.TRACKED_ENTITY_ATTRIBUTE_VALUE,
                TRACKED_ENTITY_ATTRIBUTE_VALUE_PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(STATE, null, TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_INSTANCE)
                .isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        store.close();
        assertThat(database().isOpen()).isTrue();
    }

    /**
     * Creates and inserts the foreign key rows in their respective tables.
     * For TrackedEntityAttributeValue : TrackedEntityAttribute , TrackedEntityAttributeInstance
     * For TrackedEntityInstance : OrganisationUnit
     *
     * @throws ParseException
     */
    private void insertForeignKeys() {
        ContentValues organisationUnit = CreateOrganisationUnitUtils.createOrgUnit(1L, ORGANIZATION_UINT);
        ContentValues trackedEntityInstance = CreateTrackedEntityInstanceUtils.createWithOrgUnit(TRACKED_ENTITY_INSTANCE, ORGANIZATION_UINT);
        ContentValues trackedEntityAttribute = CreateTrackedEntityAttributeUtils.create(1L, TRACKED_ENTITY_ATTRIBUTE, null);

        database().insert(Tables.ORGANISATION_UNIT, null, organisationUnit);
        database().insert(Tables.TRACKED_ENTITY_INSTANCE, null, trackedEntityInstance);
        database().insert(Tables.TRACKED_ENTITY_ATTRIBUTE, null, trackedEntityAttribute);
    }
}