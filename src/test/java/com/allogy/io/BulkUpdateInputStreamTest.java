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
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BulkUpdateInputStreamTest
{
    private List<String> innerStrings;
    private List<InputStream> innerInputStreams;

    @Before
    public void setUp()
    {
        innerStrings = new ArrayList<String>();
        innerInputStreams = new ArrayList<InputStream>();
        for(int i = 0; i < 3; i++)
        {
            String string = UUID.randomUUID().toString();
            innerStrings.add(string);
            innerInputStreams.add(IOUtils.toInputStream("\"" + string + "\""));
        }
    }

    private BulkUpdateInputStream createObjectUnderTest()
    {
        return new BulkUpdateInputStream(innerInputStreams);
    }

    @Test
    public void stream_should_return_JSON_array_of_inner_InputStream_streams() throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        Collection jsonArray = objectMapper.readValue(createObjectUnderTest(), Collection.class);

        assertThat(jsonArray.size(), is(innerStrings.size()));
        assertThat(jsonArray, is((Collection)innerStrings));
    }
}
