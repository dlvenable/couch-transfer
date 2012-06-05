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

public class ImmediateCouchImporterTest
{
    private CouchDbConnector targetCouchDbConnector;
    private ImportCommand importCommand;
    private String id;
    private InputStream dataInputStream;
    private long size;

    @Before
    public void setUp()
    {
        targetCouchDbConnector = mock(CouchDbConnector.class);

        id = UUID.randomUUID().toString();
        dataInputStream = mock(InputStream.class);
        size = 123l;

        importCommand = mock(ImportCommand.class);
        stub(importCommand.getTargetCouchDbConnector()).toReturn(targetCouchDbConnector);
        stub(importCommand.getId()).toReturn(id);
        stub(importCommand.getDataStream()).toReturn(dataInputStream);
        stub(importCommand.getSize()).toReturn(size);
    }

    private CouchImporter createObjectUnderTest()
    {
        return new ImmediateCouchImporter();
    }

    @Test
    public void commandImport_without_a_boundary_should_call_target_CouchDbConnector_update()
    {
        createObjectUnderTest().commandImport(importCommand);

        ArgumentCaptor<Options> optionsArgumentCaptor = ArgumentCaptor.forClass(Options.class);
        verify(targetCouchDbConnector).update(eq(id),
                eq(dataInputStream), eq(size),
                optionsArgumentCaptor.capture());

        Options updateOptions = optionsArgumentCaptor.getValue();
        assertThat(updateOptions, notNullValue());
        assertThat(updateOptions.getOptions(), notNullValue());
        assertThat(updateOptions.getOptions().get("new_edits"), is("false"));
    }

    @Test
    public void commandImport_with_a_boundary_should_call_target_CouchDbConnector_updateMultipart()
    {
        String boundary = UUID.randomUUID().toString();
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
    public void finishImport_is_ok()
    {
        createObjectUnderTest().finishImport();
    }
}
