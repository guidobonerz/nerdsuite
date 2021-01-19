package de.drazil.nerdsuite.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.drazil.nerdsuite.model.Address;

public class CPCAdress_ConfigGenerator {

	public static void main(String args[]) {

		try {

			File inFile = new File("c:\\Users\\drazil\\.nerdsuiteWorkspace\\cpc_464_addresses.txt");
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			String line = null;
			List<Address> list = new ArrayList<>();

			while ((line = br.readLine()) != null) {
				String item[] = line.split(";");
				item[0] = item[0].trim();

				String address[] = item[0].split(" ");
				Address a = new Address();
				a.setAddress(address[0]);
				a.setConstName(address[1]);
				a.setDescription(item.length > 1 ? item[1] : null);
				list.add(a);

			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.setSerializationInclusion(Include.NON_NULL);
			mapper.writeValue(new File("c:\\\\Users\\\\drazil\\\\.nerdsuiteWorkspace\\\\cpc_464_addresses.json"), list);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
