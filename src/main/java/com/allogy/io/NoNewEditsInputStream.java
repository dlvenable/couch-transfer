/*
 * Copyright (c) 2012 David Venable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.allogy.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class NoNewEditsInputStream extends InputStream
{
    private final InputStream innerInputStream;
    private final StringReader footerReader;

    public NoNewEditsInputStream(InputStream innerInputStream)
    {
        this.innerInputStream = innerInputStream;
        footerReader = new StringReader(", \"new_edits\": false");
    }

    @Override
    public int read() throws IOException
    {
        int innerValue = innerInputStream.read();
        if(innerValue != -1)
            return innerValue;

        return footerReader.read();
    }
}
