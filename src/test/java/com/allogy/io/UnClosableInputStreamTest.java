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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class UnClosableInputStreamTest
{
	private InputStream innerInputStream;

	@Before
	public void setUp()
	{
		innerInputStream = mock(InputStream.class);
	}

	private UnCloseableInputStream createObjectUnderTest()
	{
		return new UnCloseableInputStream(innerInputStream);
	}

	@Test
	public void read_should_return_read_from_inner_InputStream() throws IOException
	{
		int stubbedReadValue = new Random().nextInt(100) + 100;
		stub(innerInputStream.read()).toReturn(stubbedReadValue);

		int readValue = createObjectUnderTest().read();

		assertThat(readValue, is(stubbedReadValue));
	}

	@Test
	public void close_should_not_call_close_on_inner_InputStream() throws IOException
	{
		createObjectUnderTest().close();

		verify(innerInputStream, never()).close();
	}
}
