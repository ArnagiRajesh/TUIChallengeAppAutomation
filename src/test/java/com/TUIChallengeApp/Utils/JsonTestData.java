package com.TUIChallengeApp.Utils;

import java.io.IOException;
import java.io.InputStream;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public final class JsonTestData {
	private static final String DATA_RESOURCE = "testdata/scenarios.json";
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private JsonTestData() {
	}

	public static String value(String dataset, String field) {
		try (InputStream input = JsonTestData.class.getClassLoader().getResourceAsStream(DATA_RESOURCE)) {
			if (input == null) {
				throw new IllegalStateException("Missing test data resource: " + DATA_RESOURCE);
			}
			JsonNode root = MAPPER.readTree(input);
			JsonNode datasetNode = root.path(dataset);
			if (datasetNode.isMissingNode() || datasetNode.isNull()) {
				throw new IllegalArgumentException("Unknown test data set: " + dataset);
			}
			JsonNode value = datasetNode.path(field);
			if (value.isMissingNode() || value.isNull() || value.asText().isBlank()) {
				throw new IllegalArgumentException("Missing field '" + field + "' in test data set: " + dataset);
			}
			return value.asText();
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to read JSON test data from " + DATA_RESOURCE, exception);
		}
	}
}