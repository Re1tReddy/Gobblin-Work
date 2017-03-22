package com.gobblin.json.hdfs;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import gobblin.configuration.ConfigurationKeys;
import gobblin.configuration.SourceState;
import gobblin.configuration.WorkUnitState;
import gobblin.source.Source;
import gobblin.source.extractor.Extractor;
import gobblin.source.workunit.Extract;
import gobblin.source.workunit.WorkUnit;

/**
 * An implementation of {@link Source} for the simple JSON example.
 *
 * <p>
 * This source creates one {@link gobblin.source.workunit.WorkUnit} for each
 * file to pull and uses the {@link SimpleJsonExtractor} to pull the data.
 * </p>
 *
 */
public class SimpleJsonSource implements Source<String, String> {

	public static final String SOURCE_FILE_KEY = "source.file";

	@Override
	public List<WorkUnit> getWorkunits(SourceState state) {
		List<WorkUnit> workUnits = Lists.newArrayList();

		if (!state.contains(ConfigurationKeys.SOURCE_FILEBASED_FILES_TO_PULL)) {
			return workUnits;
		}

		// Create a single snapshot-type extract for all files
		Extract extract = new Extract(Extract.TableType.SNAPSHOT_ONLY,
				state.getProp(ConfigurationKeys.EXTRACT_NAMESPACE_NAME_KEY, "ExampleNamespace"), "avro");

		String filesToPull = state.getProp(ConfigurationKeys.SOURCE_FILEBASED_FILES_TO_PULL);
		for (String file : Splitter.on(',').omitEmptyStrings().split(filesToPull)) {
			// Create one work unit for each file to pull
			WorkUnit workUnit = WorkUnit.create(extract);
			workUnit.setProp(SOURCE_FILE_KEY, file);
			workUnits.add(workUnit);
		}

		return workUnits;
	}

	@Override
	public Extractor<String, String> getExtractor(WorkUnitState state) throws IOException {
		System.out.println("SimpleJsonSource called ...");
		return new SimpleJsonExtractor(state);
	}

	@Override
	public void shutdown(SourceState state) {
		// Nothing to do
	}
}