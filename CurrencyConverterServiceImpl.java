package com.anz.currency.convertor.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
@Component("service")
public class CurrencyConverterServiceImpl implements CurrencyConverterService {


	public String convertCurrency( String sourceCurr, BigDecimal amount,String desCurr, Map<String, Double> currencyMap ) throws ServiceException {
		String baseTerm = sourceCurr +"_" +desCurr;
		//This is a strait match
		Double term = currencyMap.get(baseTerm);

		if(StringUtils.isEmpty(term)){
			String reverseBaseTerm = desCurr +"_" +sourceCurr;
			//This is a reverse match;
			term = currencyMap.get(reverseBaseTerm);
		}

		if(StringUtils.isEmpty(term)){
			/*
			 * If we get here means no strait or reverse match exists.
			 */
			List<String> currencies = new ArrayList<>();
			currencies.addAll(currencyMap.keySet());
			String base1 = "";
			String base2 = "";
			for(String curr : currencies) {
				if(StringUtils.isEmpty(base1)) base1 = getBase(sourceCurr, curr);
				if(StringUtils.isEmpty(base2)) base2 = getBase(desCurr, curr);
				
				if(!StringUtils.isEmpty(base1) && !StringUtils.isEmpty(base2)){

					Double base1Term = currencyMap.get(base1);
					Double base2Term = currencyMap.get(base2);
					
					//We have got matches
					if(!StringUtils.isEmpty(base1Term) && !StringUtils.isEmpty(base2Term)) {
						//Check if both currencies have a common denominator. If not, then first find the 
						//common denominator. Eg .convert USD to EUR or vice versa.

						String[] base1Split = base1.split("_");
						String[] base2Split = base2.split("_");
						Arrays.sort(base1Split);
						Arrays.sort(base2Split);
						
						String commonDenominator = getCommonDenominator(base1Split, base2Split);
						if(!StringUtils.isEmpty(commonDenominator)) {
							//source and destination currencies have a common denominator. Eg: Either both are in USD or
							//both are in EUR.
							term = base1Term * base2Term;
							break;
							
						} else {
							//We do not have a common denominator and will have to convert USD to EUR or vice versa first.
							String base1Denom = "";
							String base2Denom = "";
							if(base1.contains(Messages.USD_BASE.toString())) {
								base1Denom = Messages.USD_BASE.toString();
							} else if(base1.contains(Messages.EURO_BASE.toString())){
								base1Denom = Messages.EURO_BASE.toString();
							}
							
							if(base2.contains(Messages.USD_BASE.toString())) {
								base2Denom = Messages.USD_BASE.toString();
							} else if(base2.contains(Messages.EURO_BASE.toString())){
								base2Denom = Messages.EURO_BASE.toString();
							}
							
							//Get denominator conversion term. Eg: USD to EUR
							Double commonTerm = currencyMap.get(base1Denom + "_" +base2Denom);
							term = base1Term * base2Term;
							if(!StringUtils.isEmpty(commonTerm)){
								term = term * commonTerm;
								break;
							}
						}
					}
					
				}
			}
		}

		if(StringUtils.isEmpty(term)){
			//If the source or destination currency was not found, throw an exception to the user.
			throw new ServiceException(Messages.CURRENCY_NOT_LISTED.toString());
		}
		
		Double newAmount = amount.doubleValue() * term;
		BigDecimal finalTerm = new BigDecimal(newAmount);
		finalTerm = finalTerm.round(new MathContext(2));
		StringBuilder builder = new StringBuilder();
		builder.append(sourceCurr +" " +amount.doubleValue() +" = " + finalTerm.doubleValue() +" " +desCurr);
		return builder.toString();
	}

	/**
	 * This method is used to build a source_destination currency pair when a direct pair does not exist in the currency map.
	 * @param currToFind
	 * @param currency
	 * @return base - source_destination currency pair
	 */
	private String getBase( String currToFind, String currency) {
		String[] splitCurr = currency.split("_");
		String base = "";
		for(int i = 0; i < splitCurr.length; i++) {
			if(splitCurr[i].equalsIgnoreCase(currToFind)){
				if(currency.contains(Messages.USD_BASE.toString()))
					base = currToFind +"_" +Messages.USD_BASE.toString();
				else if(currency.contains(Messages.EURO_BASE.toString())) {
					base = currToFind +"_" +Messages.EURO_BASE.toString();
				}
				break;
			}
		}
		return base;
	}
	
	/**
	 * This method is used to find the common currency between source and destinations currencies.
	 * @param arr1
	 * @param arr2
	 * @return common denominator.
	 */
	private String getCommonDenominator(String arr1[], String arr2[]) {
		int m = arr1.length;
		int n = arr2.length;
		int i = 0, j = 0;
		while (i < m) {
			if (!arr1[i].equalsIgnoreCase(arr2[j]))
				i++;
			else
				return arr1[i];
		}
		i = 0;
		while(j < n){
			if(!arr2[j].equalsIgnoreCase(arr1[i]))
				j++;
			else
				return arr2[j];
		}
		return null;
	}
}
