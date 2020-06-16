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

package org.hisp.dhis.android.core.user;

import com.google.common.truth.ComparableSubject;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.D2Factory;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import io.reactivex.observers.TestObserver;
import static com.google.common.truth.Truth.assertThat;

//@Ignore("Tests with real servers. Depend on server state and network connection.")
public class LoginErrorHandlingRealIntegrationShould extends BaseRealIntegrationTest {

    private D2 d2;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.forNewDatabase();
    }

    @Test
    public void succeed_for_android_current() {
        TestObserver<User> testObserver = d2.userModule().logIn(username, password, RealServerMother.android_current).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertComplete();
    }

    @Test
    public void fail_with_bad_credentials_for_android_current() {
        assertThatErrorCode(username, "wrong-pw", RealServerMother.android_current).isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void fail_with_no_dhis_server_for_android_current_with_http() {
        assertThatErrorCode("http://play.dhis2.org/android-current/").isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    @Test
    public void fail_with_no_dhis_server_for_his_kenya_with_https() {
        assertThatErrorCode("https://test.hiskenya.org").isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    @Test
    public void fail_with_no_dhis_server_for_his_kenya_with_http() {
        assertThatErrorCode("https://test.hiskenya.org").isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    @Test
    public void fail_with_no_dhis_server_for_google() {
        assertThatErrorCode("https://www.google.com").isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    /**
     * Unfortunately it's not possible to differentiate a server that can't be resolved from an actual one
     * when we are offline
     */
    @Test
    public void fail_with_no_authenticated_user_offline_non_existent_server() {
        assertThatErrorCode("https://www.ddkfwefwefkwefowgwekfnwefwefjwe.com").isEqualTo(D2ErrorCode.NO_AUTHENTICATED_USER_OFFLINE);
    }

    @Test
    public void fail_with_no_dhis_server_for_play_base_url() {
        assertThatErrorCode("https://play.dhis2.org/").isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    @Test
    public void fail_with_no_dhis_server_for_his_kenya_dhiske_with_http() {
        assertThatErrorCode("http://test.hiskenya.org/dhiske/").isEqualTo(D2ErrorCode.NO_DHIS2_SERVER);
    }

    @Test
    public void fail_with_bad_credentials_for_his_kenya_dhiske() {
        assertThatErrorCode("https://test.hiskenya.org/dhiske/").isEqualTo(D2ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    public void fail_with_url_maloformed_for_malformed_url() {
        assertThatErrorCode("asdasdwueihfew").isEqualTo(D2ErrorCode.SERVER_URL_MALFORMED);
    }

    @Test
    public void fail_with_url_maloformed_for_url_without_protocol() {
        assertThatErrorCode("play.dhis2.org/android-current/").isEqualTo(D2ErrorCode.SERVER_URL_MALFORMED);
    }

    private ComparableSubject<?, D2ErrorCode> assertThatErrorCode(String serverUrl) {
        return assertThatErrorCode(username, password, serverUrl);
    }

    private ComparableSubject<?, D2ErrorCode> assertThatErrorCode(String username, String password, String serverUrl) {
        TestObserver<User> testObserver = d2.userModule().logIn(username, password, serverUrl).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertError(D2Error.class);
        D2Error d2Error = (D2Error) testObserver.errors().get(0);
        return assertThat(d2Error.errorCode());
    }
}
