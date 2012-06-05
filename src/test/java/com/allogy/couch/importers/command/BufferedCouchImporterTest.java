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
import org.ektorp.Options;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.InputStream;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class BufferedCouchImporterTest
{
    private CouchDbConnector targetCouchDbConnector;
    private int bufferSize;
    private ImportCommand importCommand;
    private String id;
    private InputStream dataInputStream;

    @Before
    public void setUp()
    {
        targetCouchDbConnector = mock(CouchDbConnector.class);

        id = UUID.randomUUID().toString();
        dataInputStream = IOUtils.toInputStream(UUID.randomUUID().toString());

        importCommand = mock(ImportCommand.class);
        stub(importCommand.getTargetCouchDbConnector()).toReturn(targetCouchDbConnector);
        stub(importCommand.getId()).toReturn(id);
        stub(importCommand.getDataStream()).toReturn(dataInputStream);

        bufferSize = 1000;
    }

    private CouchImporter createObjectUnderTest()
    {
        return new BufferedCouchImporter(bufferSize);
    }

    private ImportCommand createImportCommand(long size)
    {
        String id = UUID.randomUUID().toString();
        InputStream dataInputStream = IOUtils.toInputStream(UUID.randomUUID().toString());

        ImportCommand importCommand = mock(ImportCommand.class);
        stub(importCommand.getTargetCouchDbConnector()).toReturn(targetCouchDbConnector);
        stub(importCommand.getId()).toReturn(id);
        stub(importCommand.getDataStream()).toReturn(dataInputStream);
        stub(importCommand.getSize()).toReturn(size);

        return importCommand;
    }

    @Test
    public void commandImport_with_a_boundary_should_immediately_perform_an_updateMultipart()
    {
        String boundary = UUID.randomUUID().toString();
        long size = bufferSize / 2;

        stub(importCommand.getSize()).toReturn(size);
        stub(importCommand.getBoundary()).toReturn(boundary);

        createObjectUnderTest().commandImport(importCommand);

        ArgumentCaptor<Options> optionsArgumentCaptor = ArgumentCaptor.forClass(Options.class);
        verify(targetCouchDbConnector).updateMultipart(eq(id),
                eq(dataInputStream), eq(boundary), eq(size),
                optionsArgumentCaptor.capture());

        Options updateOptions = optionsArgumentCaptor.getValue();
        assertThat(updateOptions, notNullValue());
        assertThat(updateOptions.getOptions(), notNullValue());
        assertThat(updateOptions.getOptions().get("new_edits"), is("false"));
    }

    @Test
    public void commandImport_without_a_boundary_and_a_size_less_than_the_buffer_size_should_not_perform_bulk_update()
    {
        long size = bufferSize / 2;
        stub(importCommand.getSize()).toReturn(size);

        createObjectUnderTest().commandImport(importCommand);

        verify(targetCouchDbConnector, never()).updateMultipart(anyString(),
                any(InputStream.class), anyString(), anyLong(), any(Options.class));
        verify(targetCouchDbConnector, never()).executeBulk(any(InputStream.class));
    }

    @Test
    public void commandImport_twice_without_a_boundary_but_on_second_equals_buffer_size_should_perform_bulk_update()
    {
        long size = bufferSize / 2;
        ImportCommand importCommand1 = createImportCommand(size);
        ImportCommand importCommand2 = createImportCommand(size);

        CouchImporter objectUnderTest = createObjectUnderTest();

        objectUnderTest.commandImport(importCommand1);
        objectUnderTest.commandImport(importCommand2);

        verify(targetCouchDbConnector).executeBulk(any(InputStream.class));
    }

    @Test
    public void commandImport_twice_without_a_boundary_but_on_second_exceeds_buffer_size_should_perform_bulk_update()
    {
        long size = (bufferSize / 2) + 1;
        ImportCommand importCommand1 = createImportCommand(size);
        ImportCommand importCommand2 = createImportCommand(size);

        CouchImporter objectUnderTest = createObjectUnderTest();

        objectUnderTest.commandImport(importCommand1);
        objectUnderTest.commandImport(importCommand2);

        verify(targetCouchDbConnector).executeBulk(any(InputStream.class));
    }

    @Test
    public void commandImport_without_boundary_and_size_greater_than_buffer_size_should_perform_bulk_update()
    {
        long size = bufferSize * 2;
        stub(importCommand.getSize()).toReturn(size);

        createObjectUnderTest().commandImport(importCommand);

        verify(targetCouchDbConnector, times(1)).executeBulk(any(InputStream.class));
    }

    @Test
    public void commandImport_without_boundary_and_size_less_than_buffer_size_after_a_bulk_update_should_not_perform_an_update()
    {
        long size1 = bufferSize + 1;
        ImportCommand importCommand1 = createImportCommand(size1);

        long size2 = bufferSize / 2;
        ImportCommand importCommand2 = createImportCommand(size2);

        CouchImporter objectUnderTest = createObjectUnderTest();

        objectUnderTest.commandImport(importCommand1);
        objectUnderTest.commandImport(importCommand2);

        verify(targetCouchDbConnector, times(1)).executeBulk(any(InputStream.class));
    }

    @Test
    public void finishImport_performs_a_bulk_update_on_buffered_documents()
    {
        long size = bufferSize / 2;
        stub(importCommand.getSize()).toReturn(size);

        CouchImporter objectUnderTest = createObjectUnderTest();
        objectUnderTest.commandImport(importCommand);

        objectUnderTest.finishImport();

        verify(targetCouchDbConnector).executeBulk(any(InputStream.class));
    }

    @Test
    public void finishImport_without_a_buffer_should_not_update_anything()
    {
        createObjectUnderTest().finishImport();
        verify(targetCouchDbConnector, never()).executeBulk(any(InputStream.class));
    }
}
