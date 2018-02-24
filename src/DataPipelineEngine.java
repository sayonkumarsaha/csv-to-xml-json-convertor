import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.UrlValidator;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.opencsv.CSVReader;
import models.Hotel;

/**
 * The DataPipelineEngine is a tool read a given CSV file, validate the contents
 * of the fields in CSV file, and convert it into JSON and XML format.
 * 
 * @author Sayon Kumar Saha
 * @since 29.05.2017
 */

public class DataPipelineEngine {

	String path;
	private String DIR;
	private String CSV_FILENAME;
	private int conversionChoice;
	Logger LOGGER = DataPipeline.LOGGER;

	public DataPipelineEngine() {
	}

	public DataPipelineEngine(String path, int conversionChoice) {

		this.path = path;

		/*
		 * The source directory and the source filename is extracted to be used
		 * for the new data format files to be generated.
		 */

		DIR = path.substring(0, path.lastIndexOf("/") + 1);
		CSV_FILENAME = path.substring(path.lastIndexOf("/") + 1,
				path.lastIndexOf(".csv"));

		this.conversionChoice = conversionChoice;
	}

	public void dataConverterEngine() {

		if (conversionChoice == 1) {
			jsonConvertor();
		} else if (conversionChoice == 2) {
			xmlConvertor();
		} else {
			jsonConvertor();
			xmlConvertor();
		}
	}

	/**
	 * This method is used to convert the CSV data into JSON format. After
	 * reading the data from CSV file, the Key Value pairs are extracted from
	 * the list of hotel objects. These Key Value pairs in the form of map are
	 * fed to the external Jackson library for conversion. The new JSON file
	 * created has the same name as that of the CSV file with different
	 * extension. It is stored in the same directory as the source.
	 */
	public void jsonConvertor() {

		List<Hotel> hotelList = readCSV();

		List<Map<String, String>> kvList = new ArrayList<Map<String, String>>();

		for (Hotel hotel : hotelList) {
			kvList.add(hotel.getKvMap());
		}

		ObjectMapper jsonMapper = new ObjectMapper();
		try {
			jsonMapper.writeValue(new File(DIR + CSV_FILENAME + ".json"),
					kvList);
		} catch (JsonGenerationException e1) {
			LOGGER.error(e1);
		} catch (JsonMappingException e1) {
			LOGGER.error(e1);
		} catch (IOException e1) {
			LOGGER.error("Error;: Input Output exception!", e1);
		}

		LOGGER.info("Finished JSON Conversion and saved new file at " + DIR
				+ CSV_FILENAME + ".json");
	}

	/**
	 * This method is used to convert the CSV data into CML format. After
	 * reading the data from CSV file, the Key Value pairs are extracted from
	 * the list of hotel objects. These Key Value pairs in the form of map are
	 * fed to the external Jackson library for conversion. The new XML file
	 * created has the same name as that of the CSV file with different
	 * extension. It is stored in the same directory as the source.
	 */
	public void xmlConvertor() {

		List<Hotel> hotelList = readCSV();

		List<Map<String, String>> kvList = new ArrayList<Map<String, String>>();

		for (Hotel hotel : hotelList) {
			kvList.add(hotel.getKvMap());
		}

		JacksonXmlModule module = new JacksonXmlModule();
		module.setDefaultUseWrapper(false);
		XmlMapper xmlMapper = new XmlMapper(module);

		try {
			xmlMapper.writer().withRootName("hotels")
					.writeValue(new File(DIR + CSV_FILENAME + ".xml"), kvList);
		} catch (com.fasterxml.jackson.core.JsonGenerationException e) {
			LOGGER.error(e);
		} catch (com.fasterxml.jackson.databind.JsonMappingException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error("Error;: Input Output exception!", e);
		}

		LOGGER.info("Finished XML Conversion and saved new file at " + DIR
				+ CSV_FILENAME + ".xml");
	}

	/**
	 * This method is used to read a CSV file row by row, validate the content
	 * of the fields, convert each field of the row into key-value pair along
	 * with the corresponding header.
	 * 
	 * @return List<Hotel> hotelList This returns a list of HOtel objects where
	 *         each object has the attributes from the fields of CSV file, and a
	 *         list containing key-value pairs with the header.
	 */
	public List<Hotel> readCSV() {

		List<Hotel> hotelList = new ArrayList<Hotel>();

		try {
			CSVReader reader = new CSVReader(new FileReader(DIR + CSV_FILENAME
					+ ".csv"));
			String[] header = reader.readNext();
			String[] row = null;

			while ((row = reader.readNext()) != null) {

				if (!dataValidation(row)) {
					LOGGER.warn("Skipped reading CSV File Row: "
							+ "Validation Error!");
					continue;
				}

				Hotel hotel = prepareHotelObj(header, row);
				hotelList.add(hotel);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			LOGGER.error("Error: Source file not found!", e);
		} catch (IOException e) {
			LOGGER.error("Error;: Input Output exception!", e);
		}

		LOGGER.info("Finished reading " + hotelList.size()
				+ " rows from CSV File at " + DIR + CSV_FILENAME + ".csv");
		return hotelList;
	}

	/**
	 * This method is used to set the attributes of a given hotel object.
	 * 
	 * @param String
	 *            [] header: The list of header fields.
	 * @param String
	 *            [] line: The list of corresponding field entries of a hotel.
	 * 
	 * @return Hotel hotel: This return an hotel object after setting all its
	 *         attributes.
	 */
	public Hotel prepareHotelObj(String[] header, String[] line) {

		Hotel hotel = new Hotel();
		hotel.setName(line[0]);
		hotel.setAddress(line[1]);
		hotel.setStars(line[2]);
		hotel.setContact(line[3]);
		hotel.setPhone(line[4]);
		hotel.setUri(line[5]);
		Map<String, String> mapHotel = prepareKvMap(header, line);
		hotel.setKvMap(mapHotel);

		return hotel;
	}

	/**
	 * This method creates a HashMap with the corresponding Key-Value pairs
	 * between the fields and the headers. Linked HashMap is used to preserve
	 * field order.
	 * 
	 * @param String
	 *            [] header: The list of header fields.
	 * @param String
	 *            [] line: The list of corresponding field entries of a hotel.
	 * 
	 * @return Map<String, String> mapHotel: This return Linked Hash Map of the
	 *         hotel headers and the fields.
	 */
	public Map<String, String> prepareKvMap(String[] header, String[] line) {

		Map<String, String> mapHotel = new LinkedHashMap<String, String>();
		for (int i = 0; i < header.length; i++)
			mapHotel.put(header[i], line[i]);
		return mapHotel;
	}

	/**
	 * This is the parent method for validation of the individual fields after
	 * reading them from the CSV file. It also gives a warning in case of null
	 * or empty values in the field.
	 * 
	 * @param String
	 *            [] listString: The list of corresponding field entries of a
	 *            hotel.
	 * @return boolean: This returns true or false as per the validation result.
	 */

	public boolean dataValidation(String[] listString) {

		for (int i = 0; i < listString.length; i++) {

			if ((StringUtils.isEmpty(listString[i]))
					|| (StringUtils.isBlank(listString[i]))) {

				StringBuilder rowData = new StringBuilder();
				for (String s : listString) {
					rowData.append(s + ", ");
				}
				LOGGER.info("Warning: Found null or blank entry "
						+ "for the row:" + rowData);
			}
		}

		if (!validateStringUTF8(listString[0]))
			return false;
		if (!validateRating(listString[2]))
			return false;
		if (!validateHTTP_URI(listString[5]))
			return false;

		return true;

	}

	/**
	 * This method is used to validate if a hotel name only contains UTF-8
	 * characters. In case of invalid string, exception message is written in
	 * the log-file.
	 * 
	 * @param String
	 *            s: The string to be validated for UTF-8 characters.
	 * @return boolean: This returns true or false as per the validation result.
	 */
	public boolean validateStringUTF8(String s) {

		byte[] sBytes;
		try {
			sBytes = s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Validation Error: Conversion into UTF-8 byte "
					+ "array failed for string " + s + "!", e);
			return false;
		}
		try {
			Charset.availableCharsets().get("UTF-8").newDecoder()
					.decode(ByteBuffer.wrap(sBytes));

		} catch (CharacterCodingException e) {
			LOGGER.error("Validation Error: UTF-8 characters validation"
					+ " failed for string " + s + "!", e);
			return false;
		}

		return true;
	}

	/**
	 * This method is used to validate if hotel ratings, which is supposed to be
	 * a number from 0 to 5 stars. In case of invalid rating, exception message
	 * is written in the log-file.
	 * 
	 * @param String
	 *            s: The hotel rating to be validated.
	 * @return boolean: This returns true or false as per the validation result.
	 */
	public boolean validateRating(String stars) {

		if ((Integer.parseInt(stars) < 0) || (Integer.parseInt(stars) > 5)) {
			LOGGER.error("Validation Error: Invalid Rating " + stars + "! "
					+ "Valid rating between 0 and 5");
			return false;
		}
		return true;
	}

	/**
	 * This method is used to validate if a hotel URL is valid. In case of
	 * invalid URL, exception message is written in the log-file.
	 * 
	 * @param String
	 *            s: The URL to be validated.
	 * @return boolean: This returns true or false as per the validation result.
	 */

	@SuppressWarnings("deprecation")
	public boolean validateHTTP_URI(String uri) {

		UrlValidator urlValidator = new UrlValidator();
		if (urlValidator.isValid(uri)) {
			return true;
		}
		LOGGER.error("Validation Error: Invalid URI" + uri + "!");
		return false;
	}

	/**
	 * This method is used to display the content of the list of objects after
	 * reading the data from the CSV file. This method is not used by the tool.
	 */

	public void displayCsvData() {

		List<Hotel> hotelList = readCSV();

		for (Hotel hotel : hotelList) {

			System.out.println("Hotel Details:");
			System.out.println(hotel.getName() + " --> " + hotel.getAddress()
					+ " --> " + hotel.getStars() + " --> " + hotel.getContact()
					+ " --> " + hotel.getPhone() + " --> " + hotel.getUri());
			System.out.println("Hotel Details from Map:");
			Map<String, String> hotelKV = hotel.getKvMap();

			for (String key : hotelKV.keySet()) {
				System.out.println(key + "-->" + hotelKV.get(key));
			}
			System.out.println("\n");
		}
	}
}
