package com.github.zaza.stockreader;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BaksyScrapper extends Scrapper {

	private static final URI BAKSY_URI = URI.create("http://www.baksy.pl/kurs-walut");

	private Collection<String> ids;

	private String date;

	public BaksyScrapper(Collection<String> ids) {
		this.ids = ids;
	}

	List<Map<String, Map<String, String>>> collectItems() {
		try {
			Document document = Jsoup.parse(BAKSY_URI.toURL(), FIVE_SECONDS);
			setDate(document);
			return collectItems(document);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void setDate(Document document) {
		String dateTime = document.select("main#container > div.container > div.price__top > div.price__time").text();
		try {
			this.date = formatDate(parseDate(dateTime));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private Date parseDate(String dateTime) throws ParseException {
		return new SimpleDateFormat("yyyy.MM.dd HH:mm").parse(dateTime);
	}

	private List<Map<String, Map<String, String>>> collectItems(Document document) {
		Elements rows = document.select("main#container > div.container > table.rate-table > tbody > tr");
		return rows.stream().filter(byIds()).map(asMap()).collect(Collectors.toList());
	}

	private Predicate<Element> byIds() {
		return new Predicate<Element>() {

			@Override
			public boolean test(Element row) {
				return ids.contains(getWaluta(row));
			}
		};
	}

	private Function<Element, Map<String, Map<String, String>>> asMap() {
		return new Function<Element, Map<String, Map<String, String>>>() {

			@Override
			public Map<String, Map<String, String>> apply(Element row) {
				return asMap(getWaluta(row), row.child(3).text(), date);
			}
		};
	}

	private String getWaluta(Element row) {
		return row.child(0).text();
	}

}
