package seman;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class Final {

	public static void main(String[] args) {
		String csvFile = "C:\\hosei\\3年前期\\セマンティックweb\\最終\\bukka-shisu.csv";
		
		File file =new File(csvFile);
		if(!file.exists()) {
			System.out.println("File not found"+csvFile);
			return;
		}
		
		String ex = "http://example.org/";
		
		Model model=ModelFactory.createDefaultModel();
		
		Property sisuu =model.createProperty(ex+"指数");
		Resource year = model.createProperty(ex+"年");
		Resource data=model.createProperty("https://www.city.yokohama.lg.jp/city-info/yokohamashi/tokei-chosa/portal/opendata/bukka-shisu.files/bukka-shisu.csv");
		data.addProperty(RDF.type,RDFS.Class);
		year.addProperty(RDF.type, RDFS.Class);
		sisuu.addProperty(RDF.type, RDF.Property);
		year.addProperty(RDFS.subClassOf, data);
		
		try {
			Reader reader = new InputStreamReader(new FileInputStream(file),"UTF-8");
			CSVParser csvParser = new CSVParser(reader,CSVFormat.DEFAULT.withFirstRecordAsHeader());
			
			for (CSVRecord csvRecord : csvParser) {
				String recordURI = ex + csvRecord.get(1);
				Resource resource = model.createResource(recordURI);
				resource.addProperty(RDF.type, year);
				
				for(String header : csvParser.getHeaderMap().keySet()) {
					String value = csvRecord.get(header);
					String test="年";
					if(false==header.contains(test)) {
						Property property = model.createProperty(ex,header);
						property.addProperty(RDFS.subPropertyOf, sisuu);
						resource.addProperty(property, value);
					}
					
				}
			}
			
			csvParser.close();
			reader.close();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
		model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		model.write(System.out,"TTL");

	}

}
