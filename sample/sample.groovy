
set 'port', 4999

// All three methods (RDFBuilder closure, construct, resolve) return
// Jena models and can be further processed with Groovy SPARQL's Sparql class
// link(String endpoint) returns the Sparql endpoint or you can instantiate new
// ones.  Jena+ ARQ will be on the classpath

get("/") {
	setHeader("Content-Type", "application/rdf+xml")
	rdf.xml { 
		defaultNamespace "http://localhost:4999/test"
		namespace foaf:"http://xmlns.com/foaf/0.1"
		
		subject("#clarkkent") {
	        property "foaf:gender":"male"
	        property "foaf:title":"Mr"
	        property "foaf:givenname":"Clark"
	        property "foaf:family_name":"Kent"
	    }
	} 
}

get("/groovy") { 
	link("http://dbpedia.org/sparql").construct("""
        CONSTRUCT { 
            <http://dbpedia.org/resource/Groovy_%28programming_language%29> <http://dbpedia.org/ontology/abstract> ?b
        } wHERE { 
            <http://dbpedia.org/resource/Groovy_%28programming_language%29> <http://dbpedia.org/ontology/abstract> ?b
        } 
	""") 
}

get("/tim") { 
	resolve('http://www.w3.org/People/Berners-Lee/card')
}
