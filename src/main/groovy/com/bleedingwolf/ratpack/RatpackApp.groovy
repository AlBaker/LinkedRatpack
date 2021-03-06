package com.bleedingwolf.ratpack

import com.bleedingwolf.ratpack.routing.Route
import com.bleedingwolf.ratpack.routing.RoutingTable

class RatpackApp {
	
    def handlers = [
        'GET': new RoutingTable(),
        'POST': new RoutingTable(),
    ]
    
	// autoSerialize - see RatpackServlet
    def config = [
        port: 5000,
		autoSerialize: false
    ]
    
    def set = { setting, value ->
        config[setting] = value
    }
       
    void register(List methods, path, handler) {
        methods.each {
            register(it, path, handler)
        }
    }
    
    void register(method, path, handler) {
        method = method.toUpperCase()

        if(path instanceof String) {
            path = new Route(path)
        }
        
        def routingTable = handlers[method]
        if(routingTable == null) {
            routingTable = new RoutingTable()
            handlers[method] = routingTable
        }
        routingTable.attachRoute path, handler
    }
    
    Closure getHandler(method, subject) {
        return handlers[method.toUpperCase()].route(subject)
    }
    
    def get = { path, handler ->
        register('GET', path, handler)
    }
    
    def post = { path, handler ->
        register('POST', path, handler)
    }
    
	def put = { path, handler ->
        register('PUT', path, handler)
    }
    
    def delete = { path, handler ->
        register('DELETE', path, handler)
    }
	

}
