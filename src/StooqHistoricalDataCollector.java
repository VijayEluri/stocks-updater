import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class StooqHistoricalDataCollector extends DataCollector {

	private String asset;
	private String fullName;
	private Date start;
	private Date end;
	private StooqHistoricalDataInterval interval;

	public StooqHistoricalDataCollector(String asset, String fullName, Date start, Date end,
			StooqHistoricalDataInterval interval) {
		this.asset = asset;
		this.fullName = fullName;
		this.start = start;
		this.end = end;
		this.interval = interval;
	}

	@Override
	public List<Data> collectData() {
		List<Data> result = new ArrayList<Data>();
		try {
			InputStream inputStream = getInput();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				String[] split = line.split(",");
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date d = df.parse(split[0]);
				float open =  Float.parseFloat(split[1]);
				float high =  Float.parseFloat(split[2]);
				float low =  Float.parseFloat(split[3]);
				float close =  Float.parseFloat(split[4]);
				int volume =  Integer.parseInt(split[5]);
				StooqHistoricalData data = new StooqHistoricalData(d, open, high, low, close, volume, asset, fullName);
				result.add(data);
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	protected InputStream getInput() throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		HttpGet httpget = new HttpGet("http://stooq.pl/q/d/l/?s=" + asset
				+ "&d1=" + sdf.format(start) + "&d2=" + sdf.format(end) + "&i="
				+ interval.toString());
//		HttpGet httpget = new HttpGet("http://stooq.pl/q/d/?s=invfiz&c=0&d1=20080122&d2=20110111");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
//		String responseBody = httpclient.execute(httpget, responseHandler);
		return httpclient.execute(httpget).getEntity().getContent();
//		System.out.println(responseBody);
//		httpclient.getConnectionManager().shutdown();
//		return new ByteArrayInputStream(responseBody.getBytes());
	}
	
	/*
	protected InputStream getInput() {
		HttpClient client = new HttpClient();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		GetMethod method = new GetMethod("http://stooq.pl/q/d/l/?s="+asset+"&d1="+sdf.format(start)+"&d2="+sdf.format(end)+"&i="+interval.toString());
		try {
			System.out.print("Executing GET... ");
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
				// TODO: return null
			}
			System.out.println("done.");
			return method.getResponseBodyAsStream();
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return null;
	}
	*/

}
