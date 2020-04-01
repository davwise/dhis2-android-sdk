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

package org.hisp.dhis.android.core.systeminfo;

public enum DHISPatchVersion {
    V2_29(DHISVersion.V2_29, "2.29", null),
    V2_30(DHISVersion.V2_30, "2.30", null),

    V2_31_0(DHISVersion.V2_31, "2.31.0", null),
    V2_31_1(DHISVersion.V2_31, "2.31.1", null),
    V2_31_2(DHISVersion.V2_31, "2.31.2", null),
    V2_31_3(DHISVersion.V2_31, "2.31.3", null),
    V2_31_4(DHISVersion.V2_31, "2.31.4", null),
    V2_31_5(DHISVersion.V2_31, "2.31.5", null),
    V2_31_6(DHISVersion.V2_31, "2.31.6", null),
    V2_31_7(DHISVersion.V2_31, "2.31.7", null),
    V2_31_8(DHISVersion.V2_31, "2.31.8", null),

    V2_32_0(DHISVersion.V2_32, "2.32.0", null),
    V2_32_1(DHISVersion.V2_32, "2.32.1", null),
    V2_32_2(DHISVersion.V2_32, "2.32.2", null),
    V2_32_3(DHISVersion.V2_32, "2.32.3", null),
    V2_32_4(DHISVersion.V2_32, "2.32.4", null),

    V2_33_0(DHISVersion.V2_33, "2.33.0", SMSVersion.V1),
    V2_33_1(DHISVersion.V2_33, "2.33.1", SMSVersion.V1),
    V2_33_2(DHISVersion.V2_33, "2.33.2", SMSVersion.V1),
    V2_33_3(DHISVersion.V2_33, "2.33.3", SMSVersion.V2);

    private String strValue;
    private DHISVersion majorVersion;
    private SMSVersion smsVersion;

    DHISPatchVersion(DHISVersion majorVersion, String strValue, SMSVersion smsVersion) {
        this.majorVersion = majorVersion;
        this.strValue = strValue;
        this.smsVersion = smsVersion;
    }

    public String getStrValue() {
        return strValue;
    }

    public DHISVersion getMajorVersion() {
        return majorVersion;
    }

    public SMSVersion getSmsVersion() {
        return smsVersion;
    }

    public static DHISPatchVersion getValue(String versionStr) {
        for (DHISPatchVersion patchVersion : DHISPatchVersion.values()) {
            if (versionStr.equals(patchVersion.strValue) || versionStr.startsWith(patchVersion.strValue + "-")) {
                return patchVersion;
            }
        }
        return null;
    }
}