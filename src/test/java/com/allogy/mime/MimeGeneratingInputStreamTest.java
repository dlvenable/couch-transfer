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

package com.allogy.mime;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class MimeGeneratingInputStreamTest
{
	private List<Header> headers;
	private InputStream bodyStream;
	private String body;

	@Before
	public void setUp()
	{
		headers = new ArrayList<Header>();
		for(int i = 0; i < 3; i++)
		{
			Header header = new BasicHeader(UUID.randomUUID().toString(), UUID.randomUUID().toString());
			headers.add(header);
		}

		body = UUID.randomUUID().toString();
		bodyStream = IOUtils.toInputStream(body);
	}

	private MimeGeneratingInputStream createObjectUnderTest()
	{
		return new MimeGeneratingInputStream(headers, bodyStream);
	}

	@Test
	public void stream_should_return_headers_split_by_CRLF_ending_with_dual_CRLF_followed_by_body() throws IOException
	{
		InputStream stream = createObjectUnderTest();

		Reader reader = new InputStreamReader(stream);

		for(Header header: headers)
		{
			String headerString = header.toString();
			char[] streamCharacters = new char[headerString.length()];

			int readStatus = reader.read(streamCharacters, 0, headerString.length());

			assertThat(readStatus, not(-1));
			assertThat(streamCharacters, is(headerString.toCharArray()));
			assertThat(reader.read(), is(MimeUtilities.CarriageReturnCharacter));
			assertThat(reader.read(), is(MimeUtilities.LineFeedCharacter));
		}

		assertThat(reader.read(), is(MimeUtilities.CarriageReturnCharacter));
		assertThat(reader.read(), is(MimeUtilities.LineFeedCharacter));

		char[] streamCharacters = new char[body.length()];
		int readStatus = reader.read(streamCharacters, 0, body.length());

		assertThat(readStatus, not(-1));
		assertThat(streamCharacters, is(body.toCharArray()));

		assertThat(stream.read(), is(-1));
	}
}
