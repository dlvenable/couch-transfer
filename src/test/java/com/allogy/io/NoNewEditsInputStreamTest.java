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
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class NoNewEditsInputStreamTest
{
    @Test
    public void stream_should_return_inner_stream_followed_by_comma_new_edits_false() throws IOException
    {
        String innerString = "[" + UUID.randomUUID().toString() + "]";
        InputStream innerInputStream = IOUtils.toInputStream(innerString);

        String stringFromObjectUnderTest = IOUtils.toString(new NoNewEditsInputStream(innerInputStream));

        assertThat(stringFromObjectUnderTest, notNullValue());

        assertThat(stringFromObjectUnderTest.startsWith(innerString), is(true));
        String prependedString = stringFromObjectUnderTest.substring(innerString.length());
        assertThat(prependedString, is(", \"new_edits\": false"));
    }
}
