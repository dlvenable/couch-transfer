couch-transfer
==============

A Java library to support exporting and importing [Couch][couch] databases.

Features
--------
 * Keep Couch's memory usage low by using Couch's MIME multipart document API for documents with attachments
 * Uses Java streams when exporting and importing so that user's of this library maintain low memory usage
 * Preserves document revisions
 * Provides a mechanism for filtering on import, allowing existing documents to be ignored
 * Uses [Ektorp][ektorp] for accessing Couch


Export
------

    OutputStream exportOutputStream;
    CouchDbConnector couchDbConnector;

    CouchDocumentExporter documentExporter = new MimeCouchDocumentExporter();
    CouchDatabaseExporter databaseExporter = new ZipCouchDatabaseExporter(documentExporter);

    try
    {
        databaseExporter.export(couchDbConnector, exportOutputStream);
    }
    catch (IOException e)
    {
        // handle as you wish
    }

Import
------

    InputStream importInputStream;
    CouchDbConnector couchDbConnector;

    CouchDocumentImporter documentImporter = new MimeCouchDocumentImporter(IncludeAllDocumentFilter.documentFilter());
    CouchDatabaseImporter databaseImporter = new ZipCouchDatabaseImporter(documentImporter);

    try
    {
        databaseImporter.importDatabase(couchDbConnector, importInputStream);
    }
    catch (IOException e)
    {
        // handle as you wish
    }

Include Using Maven
-------------------

    <repositories>
        <repository>
            <id>allogy-maven-public</id>
            <name>Allogy public Maven release repository</name>
            <url>https://s3.amazonaws.com/allogy.maven.public/release</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.allogy</groupId>
            <artifactId>couch-transfer</artifactId>
            <version>1.1</version>
        </dependency>
    </dependencies>

License
-------

Copyright (c) 2012 David Venable.

Released under the [Apache License, Version 2.0][apache-license].

[couch]: http://couchdb.apache.org/
[apache-license]: http://www.apache.org/licenses/LICENSE-2.0
[ektorp]: http://www.ektorp.org/
