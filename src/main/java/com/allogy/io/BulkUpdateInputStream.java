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

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Iterator;

public class BulkUpdateInputStream extends InputStream
{
    private final Reader headerReader;
    private final Reader footerReader;

    Iterator<InputStream> currentInputStreamIterator;
    InputStream currentInputStream;

    public BulkUpdateInputStream(Iterable<InputStream> innerInputStreams)
    {
        headerReader = new StringReader("[");
        footerReader = new StringReader("]");

        currentInputStreamIterator = innerInputStreams.iterator();
        moveToNextInnerInputStream();
    }

    @Override
    public int read() throws IOException
    {
        int headerValue = headerReader.read();
        if(headerValue != -1)
            return headerValue;

        int innerValue = currentInputStream.read();
        if(innerValue != -1)
            return innerValue;

        if(currentInputStreamIterator.hasNext())
        {
            moveToNextInnerInputStream();

            innerValue = currentInputStream.read();
            if(innerValue != -1)
                return innerValue;
        }

        int footerValue = footerReader.read();
        if(footerValue != -1)
            return footerValue;

        return -1;
    }

    private void moveToNextInnerInputStream()
    {
        currentInputStream = currentInputStreamIterator.next();
        if(currentInputStreamIterator.hasNext())
            currentInputStream = new SequenceInputStream(currentInputStream, IOUtils.toInputStream(","));
    }
}
