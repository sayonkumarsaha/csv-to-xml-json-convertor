import java.util.Scanner;
import org.apache.log4j.Logger;

/**
 * The dataPipeline is a tool that uses the Data Pipeline Engine to read a given
 * CSV file, validate the contents of the fields in CSV file, and convert it
 * into JSON and XML format.
 * 
 * @author Sayon Kumar Saha
 * @since 29.05.2017
 */

public class DataPipeline {

	// Defining logger for writing required messages in the log-file
	final static Logger LOGGER = Logger.getLogger(DataPipeline.class);

	public static void main(String[] args) {

		// arguments to choose json (1), xml conversion (2), or both (3)
		int conversionChoice;
		String csvFilePath;

		// creating a scanner to be able to read command-line input
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.println("1- CSV to JSON");
			System.out.println("2- CSV to XML");
			System.out.println("3- Both");
			System.out.print("Enter Conversion Choice:");
			conversionChoice = scanner.nextInt();

			if ((conversionChoice != 1) && (conversionChoice != 2)
					&& (conversionChoice != 3)) {
				LOGGER.warn("Invalid Input! Enter again.");
			} else {
				break;
			}
		}

		// path of the .csv file to be read
		System.out.print("Fullpath of the CSV file: ");
		csvFilePath = scanner.next();

		scanner.close();

		// instantiating the DataPipelineEngine with the input arguments
		DataPipelineEngine dataConvertor = new DataPipelineEngine(csvFilePath,
				conversionChoice);
		dataConvertor.dataConverterEngine();
	}
}