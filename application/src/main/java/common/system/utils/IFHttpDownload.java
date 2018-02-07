/*
 * Copyright 2018 fr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package common.system.utils;

/**
 * Simplify migration from {@link DownloadByServer} to {@link HttpDownload}.
 *
 * @author fr
 */
public interface IFHttpDownload {

	public static final int STATUS_NOT_INITED = 0;
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_FAILURE = 2;

	byte[] getBindata();

	String getData();

	String getFilename();

	String getMimeType();

	int getStatus();

	String getUrl();
}
