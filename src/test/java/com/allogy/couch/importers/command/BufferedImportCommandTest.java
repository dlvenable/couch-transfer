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

package com.allogy.couch.importers.command;

import org.apache.commons.io.IOUtils;
import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class BufferedImportCommandTest
{
    private ImportCommand innerImportCommand;
    private String innerDataStream;

    @Before
    public void setUp()
    {
        innerImportCommand = mock(ImportCommand.class);
        innerDataStream = UUID.randomUUID().toString();
        stub(innerImportCommand.getDataStream()).toReturn(IOUtils.toInputStream(innerDataStream));
    }

    private ImportCommand createObjectUnderTest() throws IOException
    {
        return new BufferedImportCommand(innerImportCommand);
    }

    @Test
    public void getTargetCouchDbConnector_should_return_inner_getTargetCouchDbConnector() throws IOException
    {
        CouchDbConnector targetCouchDbConnector = mock(CouchDbConnector.class);
        stub(innerImportCommand.getTargetCouchDbConnector()).toReturn(targetCouchDbConnector);
        assertThat(createObjectUnderTest().getTargetCouchDbConnector(), is(innerImportCommand.getTargetCouchDbConnector()));
    }

    @Test
    public void getId_should_return_inner_getId() throws IOException
    {
        String id = UUID.randomUUID().toString();
        stub(innerImportCommand.getId()).toReturn(id);
        assertThat(createObjectUnderTest().getId(), is(innerImportCommand.getId()));
    }

    @Test
    public void getSize_should_return_inner_getSize() throws IOException
    {
        long size = 576;
        stub(innerImportCommand.getSize()).toReturn(size);
        assertThat(createObjectUnderTest().getSize(), is(size));
    }

    @Test
    public void getBoundary_should_return_inner_getBoundary() throws IOException
    {
        String boundary = UUID.randomUUID().toString();
        stub(innerImportCommand.getBoundary()).toReturn(boundary);
        assertThat(createObjectUnderTest().getBoundary(), is(boundary));
    }

    @Test
    public void getDataStream_should_an_InputStream_with_the_same_value_as_inner_getDataStream() throws IOException
    {
        InputStream dataStreamUnderTest = createObjectUnderTest().getDataStream();

        assertThat(dataStreamUnderTest, notNullValue());
        assertThat(IOUtils.toString(dataStreamUnderTest), is(innerDataStream));
    }

    @Test
    public void getDataStream_should_not_return_the_same_InputStream_instance_as_inner_getDataStream() throws IOException
    {
        assertThat(createObjectUnderTest().getDataStream(), not(innerImportCommand.getDataStream()));
    }
}
