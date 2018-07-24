//https://github.com/neo4j/neo4j-java-driver/blob/1.5/examples/src/main/java/org/neo4j/docs/driver/HelloWorldExample.java
//Aru Final Runnable 
//https://neo4j.com/docs/developer-manual/current/drivers/sessions-transactions/

package com;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;



public class boltexample implements AutoCloseable
{
	
	private final Driver dri;
	public boltexample(String uri, String user, String password)
	{
		dri = GraphDatabase.driver(uri,AuthTokens.basic(user, password));
	}
	
	@Override
	public void close() throws Exception
	{
		dri.close();
	}
	
	public void createDB(final String message)
	{
		try(Session ses = dri.session())
		{
			String answer = ses.writeTransaction(new TransactionWork<String>()
					{
						@Override
						public String execute(Transaction tx)
						{
							//Failed:
							//StatementResult result = tx.run("CREATE(a:User{name:{name}, title:{title}})", parameters("name","Aru","title","Admin"));
							//tx.failure();
							//return result.single().get(0).asString();
							
							//Success: CREATION QUERY 1
							/*StatementResult result = tx.run( "CREATE (a:Greeting1) " +
                                    "SET a.message1 = $message1 " +
                                    "RETURN a.message1 + ', from node ' + id(a)",
                                    parameters( "message1", message ) );
							return result.single().get( 0 ).asString();*/
							
							//Success: CREATION QUERY 2
							String name_A = "Akshay";
							StatementResult result = tx.run("CREATE(per:Persona) "+ 
															"SET per.name = $name " +
															"RETURN per.name + ',from node ' + id(per)",
															parameters("name",name_A));
							return result.single().get(0).asString();
						
						}
					});
			System.out.println(answer);
		}
	}
	
	//Success : MATCH QUERY for single record entry
	public void matchDB(final String message)
	{
		try(Session ses = dri.session())
		{
			long answer = ses.readTransaction(new TransactionWork<Long>()
			{
				@Override
	            public Long execute(Transaction tx)
	            {
					StatementResult result = tx.run( "MATCH (a:Greeting {message: $name}) RETURN id(a)", parameters( "name", message ) );
				    return result.single().get( 0).asLong();
	            }
			});
			System.out.println(answer);
		}
		
	}
	
	public void matchManyDB(final String message)
	{
		try(Session ses = dri.session())
		{
			List<String> answer = ses.readTransaction(new TransactionWork<List<String>>()
			{
				@Override
	            public List<String> execute( Transaction tx )
	            {
					/*Output:
					 1. Return a - node<184>
					 			   node<185>
					 2. Return id(a) - 184, 185
					 3. Return a.message1 - "hello,Aru"
											"hello,Aru"
					 4.  Return id(a),a.message1 - 184,185
					 5. Return a.message1,id(a) -"hello,Aru"
												"hello,Aru"
					*/
				    List<String> nameList = new ArrayList<>();
				   // StatementResult result = tx.run( "MATCH (a:Person) RETURN a.name ORDER BY a.name" );
				   // StatementResult result = tx.run("MATCH (a:Greeting1 {message1: $name}) RETURN id(a), a.message1", parameters( "name", message));
				    StatementResult result = tx.run("MATCH(a:User) RETURN a.name");
				    while ( result.hasNext() )
				    {
				    	nameList.add( result.next().get(0).toString());
				    }
				    return nameList;

	            }
				
			});
			for (String str : answer)
			{

					System.out.println(str);

			}
		}
	}
	
	public void createRelationshipDB(final String message1, final String message2)
	{
		try(Session ses = dri.session())
		{
			long answer = ses.writeTransaction(new TransactionWork<Long>()
					{
						@Override
						public Long execute(Transaction tx)
						{
							StatementResult result = tx.run("MATCH (a:Person) WHERE a.name = $name1 " +
						            						"MATCH (b:Persona) WHERE b.name = $name2 " +
						            						"WITH a, b " +
						            						"MERGE (a)-[ab:Loves]->(b) " +
						            						"RETURN id(ab)",
						            						parameters("name1",message1,"name2",message2));
							return result.single().get(0).asLong();
						
						}
					});
			System.out.println(answer);
		}
		
	}
	
	public static void main(String[] args)
	{
		boltexample exm = new boltexample("bolt://127.0.0.1:7687", "neo4j", "admin");
		try
		{
			exm.createDB("hello,Meenal");
			//exm.matchDB("hello,Arundhati");
			//exm.matchManyDB("hello,Aru");
			//exm.createRelationshipDB("Arundhati","Akshay");
			
			exm.close();
		}
		catch(Exception Ex)
		{
			System.out.println(Ex.getMessage());
		}
	}
	

}


/*
 * Delete Query - 
 * match(n:Persona)
where id(n) = 185
delete n
*/
