import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import models.Hotel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

/**
 * The DataPipelineEngineTest has some unit tests to for the utility methods of
 * the Data Pipeline Engine.
 * 
 * @author Sayon Kumar Saha
 * @since 29.05.2017
 */

public class DataPipelineEngineTest {

	DataPipelineEngine testEngine;

	public DataPipelineEngineTest() {
		testEngine = new DataPipelineEngine();
	}

	@Test
	public void testprepareKvMap() {

		Map<String, String> expected = new LinkedHashMap<String, String>();
		expected.put("header_one", "field_one");
		expected.put("header_two", "field_two");

		String[] header = { "header_one", "header_two" };
		String[] line = { "field_one", "field_two" };
		Map<String, String> output = testEngine.prepareKvMap(header, line);

		assertEquals(expected, output);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testPrepareHotelObj() {

		String[] header = { "header_one", "header_two", "header_three",
				"header_four", "header_five", "header_six" };
		String[] line = { "field_one", "field_two", "3", "field_four",
				"http://www.sayonkumarsaha.com/", "field_six" };

		Hotel expected = new Hotel();
		expected.setName(line[0]);
		expected.setAddress(line[1]);
		expected.setStars(line[2]);
		expected.setContact(line[3]);
		expected.setPhone(line[4]);
		expected.setUri(line[5]);
		expected.setKvMap(testEngine.prepareKvMap(header, line));

		Hotel output = testEngine.prepareHotelObj(header, line);

		Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, output));
	}

	@Test
	public void testValidRating() {
		String rating = "5";
		boolean output = testEngine.validateRating(rating);
		assertTrue(output);
	}

	@Test
	public void testInvalidRating() {
		String rating = "-1";
		boolean output = testEngine.validateRating(rating);
		assertFalse(output);
	}

	@Test
	public void testValidHTTP_URI() {
		String uri = "http://www.sayonkumarsaha.com/";
		boolean output = testEngine.validateHTTP_URI(uri);
		assertTrue(output);
	}

	@Test
	public void testInvalidHTTP_URI() {
		String uri = "http:://www.sayonkumarsaha.com/";
		boolean output = testEngine.validateHTTP_URI(uri);
		assertFalse(output);
	}
}
