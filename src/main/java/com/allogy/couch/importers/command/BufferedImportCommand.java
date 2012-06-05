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

import java.io.IOException;
import java.io.InputStream;

public class BufferedImportCommand implements ImportCommand
{
    private final ImportCommand innerImportCommand;
    private final String dataString;

    public BufferedImportCommand(ImportCommand innerImportCommand) throws IOException
    {
        this.innerImportCommand = innerImportCommand;
        dataString = IOUtils.toString(innerImportCommand.getDataStream());
    }

    public InputStream getDataStream()
    {
        return IOUtils.toInputStream(dataString);
    }

    public CouchDbConnector getTargetCouchDbConnector()
    {
        return innerImportCommand.getTargetCouchDbConnector();
    }

    public String getId()
    {
        return innerImportCommand.getId();
    }

    public long getSize()
    {
        return innerImportCommand.getSize();
    }

    public String getBoundary()
    {
        return innerImportCommand.getBoundary();
    }
}
