package com.mysaasa.interfaces;

/**
 * These services will be made available at run time by the velocity template engine.
 *
 * It's best to think of this as "Interface Details"
 *
 * As with the API, these functions should be wrappers of the implementation in a DataService
 * or some ISimpleService dedicated to implementation details. This is a interface to the implementation, for Velocity.
 *
 * It seems redundant, but if you visualize the tree it's simple.
 * <pre>
 *                /Api Service
 *     DataService
 *                \Template Service
 * </pre>
 * <p>
 *     The implementation would all be in your DataService, and the Api Service and Template Service are just
 *     appropriate proxies. The functions should all be one liners, with the implementation in one place.
 *</p>
 *
 * <h2>
 *     ****** SECURITY NOTICE ******
 * </h2>
 * <p>
 *     Template Services are accessed via reflection, so don't fucking expose things that template doesn't need.
 *     That includes Private members/functions. If you don't want someone to access it, don't put it in a Template
 *     function. The likelihood of this attack is low, and somebody would need to modify the root templates.
 *</p>
 *<h2>
 *     ****** PERFORMANCE NOTICE ******
 *</h2>
 * <p>
 *     These Classes will be processed by Reflection, functions are invoked via reflection, etc. So don't be overly
 *     redundant.
 * </p>
 * <p>
 *     Also, don't take a long time, because these calls will be the direct metric on site loading time. If there is a
 *     cache, this is where it would be used. However if you implement a cache, use a CacheService (if I getInstance around to
 *     it) or implement your own, and use DI to getInstance at it. Don't make it a field, don't give user's access.
 * </p>
 *   <b>Rules of Template Services</b>
 *  <ol>
 *     <li> We don't put any implementation in the Template Service</li>
 *     <li> 2) We don't put any implementation in the Template Service</li>
 *</ol>



 */

public interface ITemplateService {
	public String getTemplateInterfaceName();
}
