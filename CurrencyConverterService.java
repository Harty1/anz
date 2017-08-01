package com.anz.currency.convertor.service;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.anz.currency.convertor.exceptions.ServiceException;

/**
 * 
 * @author Harsh
 *
 */
@Component("service")
public interface CurrencyConverterService {

	/**
	 * Takes valid input currency and amount and converts the amount into the desired currecny. 
	 * @param sourceCurr
	 * @param amount
	 * @param desCurr
	 * @return
	 * @throws ServiceException 
	 */
	String convertCurrency( String sourceCurr, BigDecimal amount, String desCurr, Map<String, Double> currencyMap ) throws ServiceException;
}
