package com.anz.currency.convertor;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.anz.currency.convertor.constants.Messages;
import com.anz.currency.convertor.exceptions.ServiceException;
import com.anz.currency.convertor.service.CurrencyConverterService;

/**
 * 
 * @author Harsh
 *
 */
@EnableAutoConfiguration
@Component
@ComponentScan
@SpringBootApplication
public class CurrencyConverter implements CommandLineRunner {

	private @Autowired @Qualifier("service") CurrencyConverterService service;

	/**
	 * Expects args[] in the form of <sourceCurr> Amount in <descCurr>
	 * @param args
	 */
	public static void main( String[] args ) {
		SpringApplication.run(CurrencyConverter.class, args);
	}

	public void run( String... args ) throws Exception {
		if(args.length != 4)
			throw new ServiceException(Messages.INVALID_INPUT_LENGTH.toString());

		String sourceCurr = args[0];
		String amount = args[1];
		String desCurr = args[3];
		BigDecimal dAmount = null;
		if(!StringUtils.isEmpty(amount))
			dAmount = new BigDecimal(amount);
		
		Map<String, Double> currencyMap = loadProperties();
		String desiredCurrency = service.convertCurrency(sourceCurr, dAmount, desCurr, currencyMap);
		System.out.println(desiredCurrency);
	}
	/**
	 * Loads properties file and creates various combinations of BaseTerms.
	 * @throws ServiceException
	 */
	private Map<String, Double> loadProperties() throws ServiceException, IOException {
		Properties props = new Properties();
		InputStream is = getClass().getClassLoader().getResourceAsStream("exchangeRate.properties");
		if(is != null) props.load(is);
		else throw new ServiceException(Messages.PROPS_NOT_FOUND.toString());


		Set<Entry<Object, Object>> propertiesEntrySet = props.entrySet();
		Iterator<Entry<Object, Object>> propertiesIt = propertiesEntrySet.iterator();

		//Create a map of all currencies listed in the properties files as well as and their reverse orders.
		Map<String, Double> currencyMap = new HashMap<>();
		
		//Get all the entries from the properties file.
		while(propertiesIt.hasNext()) {
			Entry<Object, Object> entry = propertiesIt.next();
			String base = (String) entry.getKey();
			String sTerm = (String) entry.getValue();
			Double term = Double.valueOf(sTerm);
			String[] splitBase = base.split("_");
			
			String base1 = splitBase[0];
			String base2 = splitBase[1];
			currencyMap.put(base, term);
			String reverseBase = base2 +"_" +base1;
			Double reverseTerm = 1 / term;
			currencyMap.put(reverseBase, reverseTerm);
		}

		return currencyMap;
	}
}
