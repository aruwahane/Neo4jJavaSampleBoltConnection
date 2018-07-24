/**
 * 
 */
/**
 * @author arundhatiwahane
 *
 */
package com;

import java.io.File;
import java.util.*;

import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

@SuppressWarnings("deprecation")

public class sampledb
{
	File dbPath = new File("/Users/arundhatiwahane/Documents/Neo4j/default.graphdb");
	GraphDatabaseService gds;

	Config noSSL = Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig();

	/* BOLT protocol port specification and the login credentials where neo4j is my user name and hello is the password */
	Driver driver = GraphDatabase.driver("bolt://127.0.0.1:7687", AuthTokens.basic( "neo4j", "admin" ), noSSL);
	/*public static void main( String[] args )
	{

		//System.out.println("In main");

		sampledb dbObj = new sampledb();
		//dbObj.deleteDatabase();
		dbObj.createDatabase();
		dbObj.shutdown();

	}*/
	void shutdown()
	{
		try 
		{
			gds.shutdown();
			System.out.println("\nGraphDatase Shutdown");
		}
		catch (Exception e)
		{
			//System.err.println(e.getMessage());
			System.out.println("No Graph Database found to Shutdown!");
		}
	}
	
	void createDatabase()
	{
		GraphDatabaseService dfs = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		System.out.println("Database created!!");	
		Config noSSL = Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig();
		
		try(Driver dri = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic( "neo4j", "neo4j" ), noSSL))
		{
			System.out.println("Database connected");
			
			Session session = dri.session();
			session.run("CREATE(a:User{name:{name}, title:{title}})", parameters("name","Aru","title","Admin"));
			System.out.println("\n\t1.CREATED"); 
			
			StatementResult result =session.run("MATCH(a:User) WHERE a.name = {name}" + 
													"RETURN a.name AS name, a.title AS title",
													parameters("name","Aru"));
			System.out.println("\n\t2.Match"); 
			
			while(result.hasNext())
			{
				Record record = result.next();
				System.out.println(record.get("title").asString() + " " + record.get("name").asString());
				
			}
			System.out.println("\n\t3.Record Found"); 
			
			
			session.close();
			dri.close();
		}
	}
}


