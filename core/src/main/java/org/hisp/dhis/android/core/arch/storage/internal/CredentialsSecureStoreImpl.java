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

package org.hisp.dhis.android.core.arch.storage.internal;

import android.content.Context;

public class CredentialsSecureStoreImpl implements CredentialsSecureStore {

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private final SecureStore secureStore;

    private Credentials credentials;

    public CredentialsSecureStoreImpl(Context context) {
        this.secureStore = new AndroidSecureStore(context);
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
        this.secureStore.setData(USERNAME_KEY, credentials.username());
        this.secureStore.setData(PASSWORD_KEY, credentials.password());
    }

    public Credentials getCredentials() {
        if (this.credentials == null) {
            String password = this.secureStore.getData(PASSWORD_KEY);
            String username = this.secureStore.getData(USERNAME_KEY);

            if (password != null && username != null) {
                this.credentials = Credentials.create(username, password);
            }
        }
        return this.credentials;
    }

    public void removeCredentials() {
        this.credentials = null;
        this.secureStore.removeData(USERNAME_KEY);
        this.secureStore.removeData(PASSWORD_KEY);
    }
}