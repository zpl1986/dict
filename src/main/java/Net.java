
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Net {
	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("D:\\word\\t.txt"));
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			line = line.trim();
			String[] split = line.split(" ", 2);
			if (split.length == 1) {
				continue;
			}
			save(split[0], split[1].trim());
//			m(split[0]);
		}

//		for(String key : map.keySet()) {
//			if(map.get(key) >1) {
//				System.out.println(key);
//			}
//		}
	}

	static Map<String, Integer> map = new HashMap<>();

	static void m(String key) {
		Integer orDefault = map.getOrDefault(key, 0);
		map.put(key, orDefault + 1);
	}

	private static void save(String q, String result) {
		q = q.trim();
		result = ""+result.trim();

//		System.out.println(q);
//		System.out.println(result);
//		if (true)
//			return;
		Connection conn = null;
		Statement stmt = null;
		try {
			// 注册 JDBC 驱动
			Class.forName("com.mysql.jdbc.Driver");

			// 打开链接
			System.out.println("连接数据库...");
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8", "root", "123456");

			// 执行查询
			System.out.println(" 实例化Statement对象...");
			stmt = conn.createStatement();
			String sql;
			try {
				sql = "insert into word(w) values('%s')";
				stmt.execute(String.format(sql, q));
			} catch (Exception e) {
			}
			sql = "update word set content = '%s', tem4 = 1 where w = '%s'";
			stmt.execute(String.format(sql, result, q));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
		}

	}

}