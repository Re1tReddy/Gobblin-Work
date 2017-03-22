package com.gobblin.json.hdfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closer;

import gobblin.configuration.ConfigurationKeys;
import gobblin.configuration.WorkUnitState;
import gobblin.password.PasswordManager;
import gobblin.source.extractor.DataRecordException;
import gobblin.source.extractor.Extractor;

/**
 * An implementation of {@link Extractor} for the simple JSON example.
 *
 * <p>
 * This extractor uses the commons-vfs library to read the assigned input file
 * storing json documents confirming to a schema. Each line of the file is a
 * json document.
 * </p>
 *
 */
public class SimpleJsonExtractor implements Extractor<String, String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleJsonExtractor.class);

	private static final String SOURCE_FILE_KEY = "source.file";

	private final WorkUnitState workUnitState;
	private final FileObject fileObject;
	private final BufferedReader bufferedReader;
	private final Closer closer = Closer.create();

	public SimpleJsonExtractor(WorkUnitState workUnitState) throws FileSystemException {
		this.workUnitState = workUnitState;

		// Resolve the file to pull
		if (workUnitState.getPropAsBoolean(ConfigurationKeys.SOURCE_CONN_USE_AUTHENTICATION, false)) {
			// Add authentication credential if authentication is needed
			UserAuthenticator auth = new StaticUserAuthenticator(
					workUnitState.getProp(ConfigurationKeys.SOURCE_CONN_DOMAIN, ""),
					workUnitState.getProp(ConfigurationKeys.SOURCE_CONN_USERNAME),
					PasswordManager.getInstance(workUnitState)
							.readPassword(workUnitState.getProp(ConfigurationKeys.SOURCE_CONN_PASSWORD)));
			FileSystemOptions opts = new FileSystemOptions();
			DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
			this.fileObject = VFS.getManager().resolveFile(workUnitState.getProp(SOURCE_FILE_KEY), opts);
		} else {
			this.fileObject = VFS.getManager().resolveFile(workUnitState.getProp(SOURCE_FILE_KEY));
		}

		// Open the file for reading
		LOGGER.info("Opening file " + this.fileObject.getURL().toString());
		this.bufferedReader = this.closer
				.register(new BufferedReader(new InputStreamReader(this.fileObject.getContent().getInputStream(),
						ConfigurationKeys.DEFAULT_CHARSET_ENCODING)));
	}

	@Override
	public String getSchema() {
		return this.workUnitState.getProp(ConfigurationKeys.SOURCE_SCHEMA);
	}

	@Override
	public String readRecord(@Deprecated String reuse) throws DataRecordException, IOException {
		// Read the next line
		System.out.println("Record --> " + this.bufferedReader.readLine());
		return this.bufferedReader.readLine();
	}

	@Override
	public long getExpectedRecordCount() {
		// We don't know how many records are in the file before actually
		// reading them
		return 0;
	}

	@Override
	public long getHighWatermark() {
		// Watermark is not applicable for this type of extractor
		return 0;
	}

	@Override
	public void close() throws IOException {
		try {
			this.closer.close();
		} catch (IOException ioe) {
			LOGGER.error("Failed to close the input stream", ioe);
		}

		try {
			this.fileObject.close();
		} catch (IOException ioe) {
			LOGGER.error("Failed to close the file object", ioe);
		}
	}
}