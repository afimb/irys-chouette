package test.server.model.realtime;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.dryade.siri.chouette.server.model.RealTimeDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import fr.certu.chouette.dao.ChouetteDriverManagerDataSource;

@ContextConfiguration(locations={"classpath:testContext.xml"})


public class SiriDatabaseInitialisation extends AbstractTestNGSpringContextTests
{

	@Autowired
	private RealTimeDao realTimeDao;

	@Autowired 
	private ChouetteDriverManagerDataSource chouetteDataSource;
	
		

	@Test (groups = {"SIRI"}, description = "initialise Siri Data")
	public void initSiri() throws Exception {
		JdbcTemplate jdbcTemplate = realTimeDao.getJdbcTemplate();

		ChouetteDriverManagerDataSource dataSource = (ChouetteDriverManagerDataSource) realTimeDao
		.getDataSource();

		String user = dataSource.getUsername();
		String schema = dataSource.getDatabaseSchema();
		String chouette = chouetteDataSource.getDatabaseSchema();

		// DROP SCHEMA siri CASCADE
		List<String> sql = new ArrayList<String>();
		sql.add("DROP SCHEMA " + schema + " CASCADE;");


		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream("src/main/sql/siri.sql")));
		String line;
		String nextSql = "";
		while ((line = in.readLine()) != null) {
			if (line.trim().isEmpty())
				continue;
			if (line.trim().startsWith("--"))
				continue;
			nextSql += line;
			if (line.trim().endsWith(";")) {
				sql.add(nextSql.replaceAll(":schemaname", schema).replace(
						":username", user).replace(';', ' '));
				nextSql = "";
			}
		}

		in.close();

		in = new BufferedReader(new InputStreamReader(
				new FileInputStream("src/test/data/populate.sql"),"UTF-8"));
		nextSql = "";
		while ((line = in.readLine()) != null) {
			if (line.trim().isEmpty())
				continue;
			if (line.trim().startsWith("--"))
				continue;
			nextSql += line;
			if (line.trim().endsWith(";")) {
				sql.add(nextSql.replaceAll(":schemaname", schema).replaceAll(":chouette", chouette).replace(';', ' '));
				nextSql = "";
			}
		}

		in.close();

		jdbcTemplate.batchUpdate(sql.toArray(new String[0]));


	}
}
