package org.hisp.dhis.android.testapp.option;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.option.OptionGroupOptionLink;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class OptionGroupOptionLinkPublicAccessShould extends BasePublicAccessShould<OptionGroupOptionLink> {

    @Mock
    private OptionGroupOptionLink object;

    @Override
    public OptionGroupOptionLink object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        OptionGroupOptionLink.create(null);
    }

    @Override
    public void has_public_builder_method() {
        OptionGroupOptionLink.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}