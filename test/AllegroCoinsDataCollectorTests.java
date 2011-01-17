import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import stocks.collector.AllegroCoinsDataCollector;
import stocks.collector.DataCollector;
import stocks.data.AllegroData;
import stocks.data.Data;

public class AllegroCoinsDataCollectorTests {

	@Test
	public void testAllegroCoinsData() throws Exception {
		DataCollector allegroCoins = new AllegroCoinsDataCollector();
		List<Data> data = allegroCoins.collectData();
		assertEquals(411 + 28, data.size());

		AllegroData first = (AllegroData) data.get(0);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2010-7-25");
		assertEquals(d, first.getDate());
		assertEquals(96, first.getValue(), 0);
	}
}