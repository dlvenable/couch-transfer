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

import com.allogy.io.BulkUpdateInputStream;
import org.ektorp.CouchDbConnector;
import org.ektorp.Options;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A CouchImporter which buffers documents without attachments
 * and performs bulk updates with those buffered documents.
 */
public class BufferedCouchImporter implements CouchImporter
{
    private final int bufferSize;
    private List<ImportCommand> bufferedImportCommands;
    private long sizeOfBufferedImportCommands;

    /**
     * Constructs a new BufferedCouchImporter with a specified
     * buffer size. Once the combined size of buffered documents
     * meets or exceeds this size, those documents are bulk
     * updated.
     * @param bufferSize the buffer size in bytes
     */
    public BufferedCouchImporter(int bufferSize)
    {
        this.bufferSize = bufferSize;
        bufferedImportCommands = new ArrayList<ImportCommand>();
    }

    public void commandImport(ImportCommand importCommand)
    {
        if(importCommand.getBoundary() != null)
        {
            importMultipartImmediately(importCommand);
        }
        else
        {
            bufferCommand(importCommand);
        }
    }

    public void finishImport()
    {
        if(!bufferedImportCommands.isEmpty())
            bulkImportBufferedImportCommands();
    }

    private void importMultipartImmediately(ImportCommand importCommand)
    {
        Options updateOptions = new Options().param("new_edits", "false");
        importCommand.getTargetCouchDbConnector().updateMultipart(importCommand.getId(),
                importCommand.getDataStream(), importCommand.getBoundary(),
                importCommand.getSize(), updateOptions);
    }

    private void bufferCommand(ImportCommand importCommand)
    {
        if(!bufferedImportCommands.isEmpty() && importCommand.getSize() + sizeOfBufferedImportCommands >= bufferSize)
        {
            bulkImportBufferedImportCommands();
        }

        try
        {
            bufferedImportCommands.add(new BufferedImportCommand(importCommand));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        sizeOfBufferedImportCommands += importCommand.getSize();

        if(sizeOfBufferedImportCommands >= bufferSize)
        {
            bulkImportBufferedImportCommands();
        }
    }

    private void bulkImportBufferedImportCommands()
    {
        List<InputStream> bufferedInputStreams = new ArrayList<InputStream>();
        CouchDbConnector targetCouchDbConnector = bufferedImportCommands.get(0).getTargetCouchDbConnector();
        for(ImportCommand importCommand : bufferedImportCommands)
        {
            bufferedInputStreams.add(importCommand.getDataStream());
        }

        BulkUpdateInputStream bulkUpdateInputStream = new BulkUpdateInputStream(bufferedInputStreams);
        targetCouchDbConnector.executeBulk(bulkUpdateInputStream);

        bufferedImportCommands.clear();
        sizeOfBufferedImportCommands = 0;
    }
}
