/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.stdlib.websocket;

import io.ballerina.runtime.api.TypeTags;
import io.ballerina.runtime.api.types.ArrayType;
import io.ballerina.runtime.api.types.Type;
import io.ballerina.runtime.api.types.UnionType;
import io.ballerina.stdlib.http.api.HttpErrorType;
import io.ballerina.stdlib.http.api.HttpUtil;
import io.ballerina.stdlib.http.transport.contract.websocket.WebSocketConnectorException;

import java.util.List;

/**
 * Represents a inbound request header parameter details.
 */
public class HeaderParam {
    private int typeTag;
    private final String token;
    private boolean nilable;
    private int index;
    private Type type;
    private String headerName;

    HeaderParam(String token) {
        this.token = token;
    }

    public void init(Type type, int index) throws WebSocketConnectorException {
        this.type = type;
        this.typeTag = type.getTag();
        this.index = index;
        validateHeaderParamType();
    }

    private void validateHeaderParamType() throws WebSocketConnectorException {
        if (this.type instanceof UnionType) {
            List<Type> memberTypes = ((UnionType) this.type).getMemberTypes();
            int size = memberTypes.size();
            if (size > 2 || !this.type.isNilable()) {
                throw new WebSocketConnectorException("invalid header param type '" + this.type.getName() +
                        "': a string or an array of a string can only be union with '()'." +
                        "Eg: string|() or string[]|()");
            }
            this.nilable = true;
            for (Type type : memberTypes) {
                if (type.getTag() == TypeTags.NULL_TAG) {
                    continue;
                }
                validateBasicType(type);
                break;
            }
        } else {
            validateBasicType(this.type);
        }
    }

    // Note the validation is only done for the non-object header params. i.e for the string, string[] types
    private void validateBasicType(Type type) {
        if (isValidBasicType(type.getTag()) || (type.getTag() == TypeTags.ARRAY_TAG && isValidBasicType(
                ((ArrayType) type).getElementType().getTag()))) {
            // Assign element type as the type of header param
            this.typeTag = type.getTag();
            return;
        }
        throw HttpUtil.createHttpError("Incompatible header parameter type: '" + type.getName() + "'. " +
                "expected: string or string[]", HttpErrorType.GENERIC_LISTENER_ERROR);
    }

    private boolean isValidBasicType(int typeTag) {
        return typeTag == TypeTags.STRING_TAG;
    }

    public String getToken() {
        return this.token;
    }

    public int getTypeTag() {
        return this.typeTag;
    }

    public boolean isNilable() {
        return this.nilable;
    }

    public int getIndex() {
        return this.index * 2;
    }

    public String getHeaderName() {
        return headerName;
    }

    void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
