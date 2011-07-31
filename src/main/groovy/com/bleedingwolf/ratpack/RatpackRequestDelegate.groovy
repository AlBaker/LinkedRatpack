package com.bleedingwolf.ratpack
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory;

import groovy.sparql.RDFBuilder;
import groovy.sparql.Sparql
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.ParserRegistry;

import org.json.JSONObject

public class RatpackRequestDelegate {

    def renderer

    def params = [:]
    def urlparams = [:]
    def headers = [:]

    def request = null
    def response = null
    def requestParamReader = new RatpackRequestParamReader()
	
	def rdf
	def sparql

    void setHeader(name, value) {
        response.setHeader(name.toString(), value.toString())
    }
    
    void setRequest(req) {
        request = req
        params.putAll(requestParamReader.readRequestParams(req))
		
        
        req.headerNames.each { header ->
            def values = []
            req.getHeaders(header).each { values << it }
            if(values.size == 1)
                values = values.get(0)
            headers[header.toLowerCase()] = values
        }
    }
	
	Sparql link(String endpoint) { 
		sparql = new Sparql(endpoint)
	}
	
	Model resolve(uri) { 
		def http = new HTTPBuilder(uri)
		def model = ModelFactory.createDefaultModel()
		http.parser.'application/rdf+xml' = { resp ->
			
			model = model.read(resp.entity.content,
										  ParserRegistry.getCharset( resp ))
		  }
		  
		http.request(Method.GET, ContentType.ANY ) { req ->
			
			  // executed for all successful responses:
			  response.success = { resp, reader ->
				println 'my response handler!'
				assert resp.statusLine.statusCode == 200
				println resp.statusLine
				System.out << reader // print response stream
			  }
			  
			  // executed only if the response status code is 401:
			  response.'404' = { resp ->
				println 'not found!'
			  }
			}
		 
		return model
	}
		
    void setResponse(res) { 
		response = res
		
		rdf = new RDFBuilder(response?.getOutputStream())
	}
	
    String render(templateName, context=[:]) {
        if(!response.containsHeader('Content-Type')) {
            setHeader('Content-Type', 'text/html')
        }
        renderer.render(templateName, context)
    }

    void contentType(String contentType) {
        setHeader("Content-Type",contentType)
    }

    String renderJson(o) {
        if (!response.containsHeader("Content-Type")) {
            contentType("application/json")
        }
	new JSONObject(o).toString()
    }

}
